package com.BharatCrypto.repository;

import com.BharatCrypto.model.Coin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoinRepository extends JpaRepository<Coin,String> {
}
