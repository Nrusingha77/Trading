package com.BharatCrypto.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.BharatCrypto.model.Coin;
import com.BharatCrypto.service.CoinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/coins")
@CrossOrigin(allowedHeaders = "*", origins = "*")
public class CoinController {

    @Autowired
    private CoinService coinService;

    @Autowired
    private ObjectMapper objectMapper;

   
    @GetMapping
    public ResponseEntity<Page<Coin>> getAllCoins(
            @RequestParam(value = "page", defaultValue = "0") Integer page) {
        try {
            System.out.println("Getting paginated coins from database - page: " + page);
            
            if (page > 0) {
                page = page - 1;
            }
            
            Page<Coin> coins = coinService.getAllCoins(page);
            return new ResponseEntity<>(coins, HttpStatus.OK);
            
        } catch (Exception e) {
            System.err.println("Error fetching coins: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    
    @GetMapping("/top50")
    public ResponseEntity<List<Coin>> getTop50() {
        try {
            System.out.println("Getting top 50 coins from CoinCap API");
            List<Coin> coins = coinService.getTop50CoinsByMarketCapRank();
            System.out.println("Top 50 fetched: " + coins.size() + " coins");
            return new ResponseEntity<>(coins, HttpStatus.OK);
        } catch (Exception e) {
            System.err.println("Error fetching top 50: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    
    @GetMapping("/details/{coinId}")
    public ResponseEntity<Coin> getCoinDetails(@PathVariable String coinId) {
        try {
            System.out.println("Getting coin details from CoinCap API: " + coinId);
            Coin coin = coinService.getCoinDetails(coinId);
            return new ResponseEntity<>(coin, HttpStatus.OK);
        } catch (Exception e) {
            System.err.println("Error fetching coin details: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    
    @GetMapping("/{coinId}/chart")
    public ResponseEntity<JsonNode> getMarketChart(
            @PathVariable String coinId,
            @RequestParam(value = "days", defaultValue = "1") int days) {
        try {
            System.out.println("Getting chart data from CoinCap API: " + coinId + " (" + days + " days)");
            JsonNode marketChart = coinService.getMarketChart(coinId, days);
            return ResponseEntity.ok(marketChart);
        } catch (Exception e) {
            System.err.println("Error fetching chart: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    
    @GetMapping("/search")
    public ResponseEntity<List<Coin>> searchCoin(@RequestParam("q") String keyword) {
        try {
            System.out.println("Searching coins from CoinCap API: " + keyword);
            List<Coin> coins = coinService.searchCoin(keyword);
            System.out.println("Found " + coins.size() + " search results");
            return ResponseEntity.ok(coins);
        } catch (Exception e) {
            System.err.println("Error searching coins: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
