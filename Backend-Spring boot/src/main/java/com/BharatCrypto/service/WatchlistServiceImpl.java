package com.BharatCrypto.service;

import com.BharatCrypto.model.Coin;
import com.BharatCrypto.model.User;
import com.BharatCrypto.model.Watchlist;
import com.BharatCrypto.repository.CoinRepository;
import com.BharatCrypto.repository.WatchlistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class WatchlistServiceImpl implements WatchlistService {

    @Autowired
    private WatchlistRepository watchlistRepository;

    @Autowired
    private CoinRepository coinRepository;

    @Override
    public Watchlist findUserWatchlist(Long userId) throws Exception {
        Watchlist watchlist = watchlistRepository.findByUserId(userId);
        if (watchlist == null) {
            throw new Exception("Watchlist not found for user: " + userId);
        }
        return watchlist;
    }

    @Override
    public Watchlist findById(Long watchlistId) throws Exception {
        return watchlistRepository.findById(watchlistId)
                .orElseThrow(() -> new Exception("Watchlist not found with id: " + watchlistId));
    }

    @Override
    public Coin addItemToWatchlist(Coin coin, User user) throws Exception {
        try {
            System.out.println("➕ Adding coin to watchlist for user: " + user.getId());
            
            // ✅ Check if watchlist exists for user
            Watchlist watchlist = watchlistRepository.findByUserId(user.getId());
            
            if (watchlist == null) {
                System.out.println("⚠️ Watchlist not found for user: " + user.getId() + ". Creating new watchlist...");
                
                // ✅ CREATE WATCHLIST IF IT DOESN'T EXIST
                watchlist = new Watchlist();
                watchlist.setUser(user);
                watchlist.setCoins(new ArrayList<>());
                watchlist = watchlistRepository.save(watchlist);
                System.out.println("✅ New watchlist created with ID: " + watchlist.getId());
            }
            
            // ✅ Rest of the logic
            if (coin.getPriceUsd() == null || coin.getPriceUsd().trim().isEmpty()) 
                coin.setPriceUsd("0");
            if (coin.getMarketCapUsd() == null || coin.getMarketCapUsd().trim().isEmpty()) 
                coin.setMarketCapUsd("0");
            if (coin.getVolumeUsd24Hr() == null || coin.getVolumeUsd24Hr().trim().isEmpty()) 
                coin.setVolumeUsd24Hr("0");
            if (coin.getChangePercent24Hr() == null || coin.getChangePercent24Hr().trim().isEmpty()) 
                coin.setChangePercent24Hr("0");
            if (coin.getVwap24Hr() == null || coin.getVwap24Hr().trim().isEmpty()) 
                coin.setVwap24Hr("0");
            if (coin.getSupply() == null || coin.getSupply().trim().isEmpty()) 
                coin.setSupply("0");
            if (coin.getMaxSupply() == null || coin.getMaxSupply().trim().isEmpty()) 
                coin.setMaxSupply("0");
            if (coin.getMarketCapUsdc() == null || coin.getMarketCapUsdc().trim().isEmpty()) 
                coin.setMarketCapUsdc("0");
            if (coin.getRank() == null || coin.getRank().trim().isEmpty()) 
                coin.setRank("0");
            if (coin.getImage() == null || coin.getImage().trim().isEmpty()) 
                coin.setImage("");

            Coin savedCoin = null;
            try {
                savedCoin = coinRepository.findById(coin.getId()).orElse(null);
            } catch (Exception e) {
                System.out.println("⚠️ Error checking coin in DB: " + e.getMessage());
                savedCoin = null;
            }

            if (savedCoin == null) {
                System.out.println("💾 Saving new coin to DB: " + coin.getId());
                savedCoin = coinRepository.save(coin);
            } else {
                System.out.println("✅ Coin already exists in DB: " + coin.getId());
            }

            final Coin finalCoin = savedCoin;
            boolean alreadyExists = watchlist.getCoins().stream()
                    .anyMatch(c -> c.getId().equals(finalCoin.getId()));
            
            if (!alreadyExists) {
                System.out.println("➕ Coin added to watchlist: " + finalCoin.getId());
                watchlist.getCoins().add(finalCoin);
            } else {
                System.out.println("➖ Coin removed from watchlist: " + finalCoin.getId());
                watchlist.getCoins().removeIf(c -> c.getId().equals(finalCoin.getId()));
            }
            
            watchlistRepository.save(watchlist);
            return finalCoin;
        } catch (Exception e) {
            System.err.println("❌ Error adding coin to watchlist: " + e.getMessage());
            e.printStackTrace();
            throw new Exception("Failed to add coin to watchlist: " + e.getMessage(), e);
        }
    }
}
