package com.BharatCrypto.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "coins")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Coin {

    @Id
    private String id;

    @Column(nullable = false)
    private String symbol;

    @Column(nullable = false)
    private String name;
    
    @Column(name = "price_usd", nullable = true, columnDefinition = "VARCHAR(255) DEFAULT '0'")
    @JsonProperty("priceUsd")
    private String priceUsd;

    @Column(name = "market_cap_usd", nullable = true, columnDefinition = "VARCHAR(255) DEFAULT '0'")
    @JsonProperty("marketCapUsd")
    private String marketCapUsd;

    @Column(name = "volume_usd_24_hr", nullable = true, columnDefinition = "VARCHAR(255) DEFAULT '0'")
    @JsonProperty("volumeUsd24Hr")
    private String volumeUsd24Hr;

    @Column(name = "change_percent_24_hr", nullable = true, columnDefinition = "VARCHAR(255) DEFAULT '0'")
    @JsonProperty("changePercent24Hr")
    private String changePercent24Hr;

    @Column(name = "vwap_24_hr", nullable = true, columnDefinition = "VARCHAR(255) DEFAULT '0'")
    @JsonProperty("vwap24Hr")
    private String vwap24Hr;

    @Column(name = "supply", nullable = true, columnDefinition = "VARCHAR(255) DEFAULT '0'")
    @JsonProperty("supply")
    private String supply;

    @Column(name = "max_supply", nullable = true, columnDefinition = "VARCHAR(255) DEFAULT '0'")
    @JsonProperty("maxSupply")
    private String maxSupply;

    @Column(name = "market_cap_usdc", nullable = true, columnDefinition = "VARCHAR(255) DEFAULT '0'")
    @JsonProperty("marketCapUsdc")
    private String marketCapUsdc;

    @Column(name = "image", nullable = true)
    @JsonProperty("image")
    private String image;

    // ✅ FIXED: Rename from 'rank' to 'coin_rank' (rank is reserved keyword)
    @Column(name = "coin_rank", nullable = true, columnDefinition = "VARCHAR(10)")
    @JsonProperty("rank")
    private String rank;

    // ✅ ADD: Aliases for frontend compatibility
    @Transient
    public String getTotal_volume() {
        return volumeUsd24Hr;
    }

    @Transient
    public String getMarket_cap() {
        return marketCapUsd;
    }

    @Transient
    public String getCurrent_price() {
        return priceUsd;
    }

    @Transient
    public Double getMarket_cap_change_percentage_24h() {
        try {
            return Double.parseDouble(changePercent24Hr != null ? changePercent24Hr : "0");
        } catch (Exception e) {
            return 0.0;
        }
    }

    public String getCurrentPrice() {
        return priceUsd;
    }
}
