package com.BharatCrypto.util;

import com.BharatCrypto.repository.CoinRepository;
import com.BharatCrypto.service.CoinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private CoinService coinService;

    @Autowired
    private CoinRepository coinRepository;

    @Override
    public void run(String... args) throws Exception {
        // ✅ DISABLED: No longer load coins into DB on startup
        // All coin data is fetched real-time from CoinCap API
        System.out.println("✅ DataInitializer: Coin data will be fetched real-time from CoinCap API");
    }
}