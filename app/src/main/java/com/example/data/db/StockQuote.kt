package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stock_quotes")
data class StockQuote(
    @PrimaryKey val symbol: String,
    val name: String,
    val exchange: String = "NSE",
    val sector: String,
    val price: Double,
    val change: Double,
    val changePct: Double,
    val high52: Double,
    val low52: Double,
    val peRatio: Double,
    val marketCapCr: Double, // Market cap in Crores INR
    val volume: Long,
    val rsi14: Double,
    val macdStatus: String, // BULLISH_CROSS, BEARISH_CROSS, NEUTRAL
    val trend: String // UPTREND, CONSOLIDATION, DOWNTREND
)
