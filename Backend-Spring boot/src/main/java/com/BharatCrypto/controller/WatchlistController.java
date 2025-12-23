package com.BharatCrypto.controller;

import com.BharatCrypto.model.Coin;
import com.BharatCrypto.model.User;
import com.BharatCrypto.model.Watchlist;
import com.BharatCrypto.service.CoinService;
import com.BharatCrypto.service.UserService;
import com.BharatCrypto.service.WatchlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/watchlist")
@CrossOrigin(allowedHeaders = "*", origins = "*")
public class WatchlistController {

    @Autowired
    private WatchlistService watchlistService;

    @Autowired
    private UserService userService;

    @Autowired
    private CoinService coinService;

    // ✅ GET user's watchlist
    @GetMapping("/user")
    public ResponseEntity<?> getUserWatchlist(
            @RequestHeader("Authorization") String jwt) {
        try {
            System.out.println("📋 Fetching user watchlist");
            User user = userService.findUserProfileByJwt(jwt);
            Watchlist watchlist = watchlistService.findUserWatchlist(user.getId());
            System.out.println("✅ Watchlist fetched: " + watchlist.getCoins().size() + " coins");
            return ResponseEntity.ok(watchlist);
        } catch (Exception e) {
            System.err.println("❌ Error fetching watchlist: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching watchlist: " + e.getMessage());
        }
    }

    // ✅ GET watchlist by ID
    @GetMapping("/{watchlistId}")
    public ResponseEntity<?> getWatchlistById(
            @PathVariable Long watchlistId) {
        try {
            Watchlist watchlist = watchlistService.findById(watchlistId);
            return ResponseEntity.ok(watchlist);
        } catch (Exception e) {
            System.err.println("❌ Error fetching watchlist by ID: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Watchlist not found: " + e.getMessage());
        }
    }

    // ✅ ADD or REMOVE coin from watchlist
    @PatchMapping("/add/coin/{coinId}")
    public ResponseEntity<?> addItemToWatchlist(
            @RequestHeader("Authorization") String jwt,
            @PathVariable String coinId) {
        try {
            System.out.println("➕ Adding/removing coin from watchlist: " + coinId);
            User user = userService.findUserProfileByJwt(jwt);
            
            // ✅ Verify user exists
            if (user == null || user.getId() == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("User not authenticated");
            }
            
            // ✅ Fetch coin from CoinCap API (NOT from database)
            Coin coin = coinService.getCoinDetails(coinId);
            
            if (coin == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Coin not found: " + coinId);
            }
            
            // ✅ Add to watchlist (service will save to DB if needed)
            Coin addedCoin = watchlistService.addItemToWatchlist(coin, user);
            
            System.out.println("✅ Coin processed: " + addedCoin.getId());
            return ResponseEntity.ok(addedCoin);
        } catch (Exception e) {
            System.err.println("❌ Error in watchlist operation: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error: " + e.getMessage());
        }
    }
}
