package com.BharatCrypto.service;

import com.BharatCrypto.model.Coin;
import com.BharatCrypto.model.User;
import com.BharatCrypto.model.Watchlist;

public interface WatchlistService {
    Watchlist findUserWatchlist(Long userId) throws Exception;
    Watchlist findById(Long watchlistId) throws Exception;
    Coin addItemToWatchlist(Coin coin, User user) throws Exception;
}
