package com.BharatCrypto.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.BharatCrypto.model.Coin;
import com.BharatCrypto.repository.CoinRepository;
import com.BharatCrypto.response.ApiResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CoinServiceImpl implements CoinService {
    private final RestTemplate restTemplate;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private CoinRepository coinRepository;
    
    private static final String COINCAP_HOST = "https://rest.coincap.io";
    private static final String COINCAP_V3_BASE = COINCAP_HOST + "/v3";
    private static final String COINCAP_V2_BASE = COINCAP_HOST + "/v2";
    private static final Integer PAGE_SIZE = 10;

    private List<Coin> top50Cache = new ArrayList<>();
    private long top50CacheTimestamp = 0;

    // ✅ Cache for Market Charts (Key: coinId_days, Value: Data + Timestamp)
    private final Map<String, CachedChartData> chartCache = new ConcurrentHashMap<>();

    @Value("${coincap.api.key:}")
    private String coincapApiKey;

    public CoinServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    @Override
    public Page<Coin> getAllCoins(Integer page) {
        try {
            if (page == null || page < 0) {
                page = 0;
            }
            
            System.out.println("📋 Fetching paginated coins from CoinCap API - page: " + page);
            
            // Fetch from CoinCap API instead of database
            String url = COINCAP_V3_BASE + "/assets?limit=" + PAGE_SIZE + "&offset=" + (page * PAGE_SIZE);
            System.out.println(" Calling URL: " + url);
            
            JsonNode response = fetchJsonData(url);
            System.out.println(" API Response received");
            
            JsonNode data = response.get("data");
            if (data == null) {
                System.err.println(" No 'data' field in response");
                throw new Exception("Invalid API response: missing 'data' field");
            }
            
            System.out.println(" Parsing " + data.size() + " coins");
            List<Coin> coins = new ArrayList<>();
            
            for (JsonNode coinNode : data) {
                try {
                    Coin coin = objectMapper.treeToValue(coinNode, Coin.class);
                    coins.add(coin);
                } catch (Exception e) {
                    System.err.println(" Failed to parse coin: " + e.getMessage());
                }
            }
            
            System.out.println("Successfully parsed " + coins.size() + " coins");
            
            // Create Page object manually
            Pageable pageable = PageRequest.of(page, PAGE_SIZE);
            long totalElements = 2000; // CoinCap has ~2000 coins total
            Page<Coin> coinPage = new PageImpl<>(coins, pageable, totalElements);
            
            System.out.println("Returning page with " + coinPage.getNumberOfElements() + " coins");
            
            return coinPage;
        } catch (Exception e) {
            System.err.println("Error in getAllCoins: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to fetch coins from CoinCap API: " + e.getMessage(), e);
        }
    }
    @Override
    public List<Coin> getCoinList(int page) throws Exception {
        String url = COINCAP_V3_BASE + "/assets?limit=" + PAGE_SIZE + "&offset=" + (page * PAGE_SIZE);
        try {
            System.out.println("Fetching coin list from CoinCap - page: " + page);
            JsonNode data = fetchJsonData(url).get("data");
            List<Coin> coins = objectMapper.convertValue(data, new TypeReference<List<Coin>>() {});
            System.out.println("Fetched " + coins.size() + " coins from API");
            return coins;
        } catch (Exception e) {
            System.err.println("Error fetching coin list from CoinCap: " + e.getMessage());
            throw new Exception("Failed to fetch coin list from CoinCap", e);
        }
    }

    @Override
    public JsonNode getMarketChart(String coinId, int days) throws Exception {
        // ✅ Check Cache First
        String cacheKey = coinId + "_" + days;
        if (chartCache.containsKey(cacheKey)) {
            CachedChartData cachedData = chartCache.get(cacheKey);
            if (System.currentTimeMillis() - cachedData.timestamp < 300000) { // 5 minutes cache
                System.out.println("Returning cached chart for " + coinId + " (" + days + " days)");
                return cachedData.data;
            }
        }

        String interval = getInterval(days);
        long end = System.currentTimeMillis();
        long start = end - (long)days * 24 * 60 * 60 * 1000;

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(COINCAP_V3_BASE)
                .path("/assets/" + coinId + "/history")
                .queryParam("interval", interval)
                .queryParam("start", start)
                .queryParam("end", end);

        try {
            System.out.println(" Fetching chart for " + coinId + " (" + days + " days) from CoinCap");
            JsonNode response = fetchJsonData(uriBuilder.toUriString());
            JsonNode data = response.get("data");

            ArrayNode formattedData = objectMapper.createArrayNode();
            if (data != null && data.isArray()) {
                for (JsonNode item : data) {
                    try {
                        long time = item.get("time").asLong();
                        double price = item.get("priceUsd").asDouble();
                        ArrayNode dataPoint = objectMapper.createArrayNode();
                        dataPoint.add(time);
                        dataPoint.add(price);
                        formattedData.add(dataPoint);
                    } catch (Exception e) {
                        System.err.println(" Failed to parse chart point: " + e.getMessage());
                    }
                }
            }
            System.out.println(" Chart data fetched: " + formattedData.size() + " points");
            
            // ✅ Store in Cache
            chartCache.put(cacheKey, new CachedChartData(formattedData, System.currentTimeMillis()));
            
            return formattedData;
        } catch (Exception e) {
            System.err.println("Error fetching market chart: " + e.getMessage());
            throw new Exception("Failed to fetch market chart for " + coinId, e);
        }
    }

    private String getInterval(int days) {
        if (days <= 1) return "m5";      // 5 minutes
        if (days <= 30) return "h1";     // 1 hour
        if (days <= 90) return "h6";     // 6 hours
        if (days <= 365) return "d1";    // 1 day
        return "w1";                      // 1 week
    }
    @Override
    public Coin getCoinDetails(String coinId) throws Exception {
        String url = COINCAP_V3_BASE + "/assets/" + coinId;
        try {
            System.out.println("Fetching coin details from CoinCap: " + coinId);
            JsonNode responseNode = fetchJsonData(url);
            if (responseNode.has("data") && !responseNode.get("data").isNull()) {
                JsonNode dataNode = responseNode.get("data");
                Coin coin = objectMapper.treeToValue(dataNode, Coin.class);
                System.out.println("Coin details fetched: " + coin.getName());
                return coin;
            }
            throw new Exception("Could not find coin details for ID: " + coinId);
        } catch (Exception e) {
            System.err.println("Error fetching coin details: " + e.getMessage());
            throw new Exception("Failed to fetch coin details for " + coinId, e);
        }
    }
    @Override
    public Coin findById(String coinId) throws Exception {
        String url = COINCAP_V3_BASE + "/assets/" + coinId;
        try {
            System.out.println("Finding coin: " + coinId);
            JsonNode data = fetchJsonData(url).get("data");
            Coin coin = objectMapper.treeToValue(data, Coin.class);
            System.out.println("Coin found: " + coin.getId());
            return coin;
        } catch (Exception e) {
            System.err.println("Coin not found: " + coinId);
            throw new Exception("Coin not found with id: " + coinId, e);
        }
    }

    @Override
    public List<Coin> searchCoin(String keyword) throws Exception {
        try {
            System.out.println("Searching coins from CoinCap API: " + keyword);
            
            String url = COINCAP_V3_BASE + "/assets?search=" + keyword;
            System.out.println("? Calling URL: " + url);
            
            JsonNode response = fetchJsonData(url);
            List<Coin> coins = new ArrayList<>();
            
            if (response == null) {
                System.err.println("No response from CoinCap API");
                return coins;
            }
            
            JsonNode data = response.get("data");
            if (data != null && data.isArray()) {
                for (JsonNode item : data) {
                    try {
                        Coin coin = objectMapper.treeToValue(item, Coin.class);
                        if (coin != null && coin.getId() != null) {
                            coins.add(coin);
                        }
                    } catch (Exception e) {
                        System.err.println("Error parsing coin: " + e.getMessage());
                        continue;
                    }
                }
            }
            
            System.out.println("Found " + coins.size() + " search results");
            return coins;
        } catch (Exception e) {
            System.err.println("Error searching coins: " + e.getMessage());
            e.printStackTrace();
            throw new Exception("Failed to search coins", e);
        }
    }
    @Override
    public synchronized List<Coin> getTop50CoinsByMarketCapRank() throws Exception {
        long now = System.currentTimeMillis();
        if (!top50Cache.isEmpty() && (now - top50CacheTimestamp < 300000)) { // Cache for 5 minutes
            System.out.println("Returning cached Top 50 coins (Backend Cache)");
            return top50Cache;
        }

        String url = COINCAP_V3_BASE + "/assets?limit=50&offset=0";
        try {
            System.out.println("Fetching top 50 coins from CoinCap API");
            System.out.println("Calling URL: " + url);
            
            JsonNode response = fetchJsonData(url);
            System.out.println("API Response received");
            
            JsonNode data = response.get("data");
            if (data == null) {
                System.err.println("No 'data' field in response");
                throw new Exception("Invalid API response: missing 'data' field");
            }
            
            System.out.println("Parsing " + data.size() + " coins");
            List<Coin> coins = new ArrayList<>();
            
            for (JsonNode coinNode : data) {
                try {
                    Coin coin = objectMapper.treeToValue(coinNode, Coin.class);
                    coins.add(coin);
                } catch (Exception e) {
                    System.err.println("Failed to parse coin: " + e.getMessage());
                }
            }
            
            System.out.println("Fetched " + coins.size() + " top coins");
            
            // Update Cache
            this.top50Cache = coins;
            this.top50CacheTimestamp = now;
            
            // ✅ Save to database to ensure data persistence
            try {
                coinRepository.saveAll(coins);
            } catch (Exception e) {
                System.err.println("⚠️ Failed to save coins to DB: " + e.getMessage());
            }
            
            return coins;
        } catch (Exception e) {
            System.err.println("❌ Error fetching top 50 coins: " + e.getMessage());
            e.printStackTrace();
            throw new Exception("Failed to fetch top 50 coins", e);
        }
    }

    // ✅ Get top 50 as ApiResponse
    @Override
    public ApiResponse getTop50() throws Exception {
        try {
            List<Coin> top50 = getTop50CoinsByMarketCapRank();
            ApiResponse response = new ApiResponse();
            response.setMessage("Top 50 coins fetched successfully");
            return response;
        } catch (Exception e) {
            throw new Exception("Failed to fetch top 50 coins", e);
        }
    }

    // ✅ Get coin from database by ID (optional, for future optimization)
    @Override
    public Coin getCoinById(String coinId) {
        try {
            Coin coin = coinRepository.findById(coinId).orElse(null);
            if (coin == null) {
                System.out.println("⚠️ Coin not in cache, fetching from API: " + coinId);
                return getCoinDetails(coinId);
            }
            return coin;
        } catch (Exception e) {
            System.err.println("❌ Error in getCoinById: " + e.getMessage());
            throw new RuntimeException("Failed to fetch coin", e);
        }
    }

    // ✅ Save coin to database (for caching)
    @Override
    public Coin saveCoin(Coin coin) throws Exception {
        try {
            return coinRepository.save(coin);
        } catch (Exception e) {
            throw new Exception("Failed to save coin: " + coin.getId(), e);
        }
    }

    // ✅ Fetch JSON data from API
    private JsonNode fetchJsonData(String url) throws Exception {
        return objectMapper.readTree(fetchRawJson(url));
    }

    // ✅ Fetch raw JSON with retry logic and better error handling
    @Retryable(
            value = {ResourceAccessException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    private String fetchRawJson(String url) throws Exception {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept", "application/json");
            headers.set("User-Agent", "BharatCryptoTrading/1.0");
            
            if (coincapApiKey != null && !coincapApiKey.trim().isEmpty()) {
                headers.set("Authorization", "Bearer " + coincapApiKey);
            }
            
            HttpEntity<String> entity = new HttpEntity<>(headers);
            System.out.println("📡 Making HTTP request to: " + url);
            
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            
            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println("✅ HTTP 200 - API Response received");
                return response.getBody();
            } else {
                System.err.println("❌ HTTP " + response.getStatusCodeValue() + " - " + response.getBody());
                throw new Exception("API returned status: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
            System.err.println("❌ Client Error (" + e.getStatusCode() + "): " + e.getResponseBodyAsString());
            throw new Exception("HTTP " + e.getStatusCode() + ": " + e.getMessage(), e);
        } catch (HttpServerErrorException e) {
            System.err.println("❌ Server Error (" + e.getStatusCode() + "): " + e.getResponseBodyAsString());
            throw new Exception("HTTP " + e.getStatusCode() + ": " + e.getMessage(), e);
        } catch (ResourceAccessException e) {
            System.err.println("⏱️  Resource Access Error - retrying...");
            throw e;
        } catch (Exception e) {
            System.err.println("❌ Unexpected Error: " + e.getMessage());
            throw new Exception("Error fetching data from CoinCap API: " + e.getMessage(), e);
        }
    }

    // ✅ Inner class for Cache
    private static class CachedChartData {
        JsonNode data;
        long timestamp;

        CachedChartData(JsonNode data, long timestamp) {
            this.data = data;
            this.timestamp = timestamp;
        }
    }
}