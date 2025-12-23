package com.BharatCrypto.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.BharatCrypto.model.Coin;
import com.BharatCrypto.response.ApiResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CoinService {
    
    // ✅ Database operations (for pagination history)
    Page<Coin> getAllCoins(Integer page);
    Coin getCoinById(String coinId);
    Coin saveCoin(Coin coin) throws Exception;

    // ✅ CoinCap API operations (real-time data)
    List<Coin> getCoinList(int page) throws Exception;
    Coin getCoinDetails(String coinId) throws Exception;
    Coin findById(String coinId) throws Exception;
    List<Coin> searchCoin(String keyword) throws Exception;
    List<Coin> getTop50CoinsByMarketCapRank() throws Exception;
    JsonNode getMarketChart(String coinId, int days) throws Exception;
    ApiResponse getTop50() throws Exception;
}
