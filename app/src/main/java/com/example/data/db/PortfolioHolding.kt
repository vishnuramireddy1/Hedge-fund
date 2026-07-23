package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class StrategyType {
    SWING,
    LONG_TERM
}

@Entity(tableName = "portfolio_holdings")
data class PortfolioHolding(
    @PrimaryKey val symbol: String, // e.g. RELIANCE, TATAMOTORS, HDFCBANK
    val name: String,
    val exchange: String = "NSE", // NSE or BSE
    val sector: String,
    val quantity: Int,
    val avgPrice: Double,
    val currentPrice: Double,
    val strategyType: StrategyType, // SWING or LONG_TERM
    val targetPrice: Double,
    val stopLoss: Double,
    val thesis: String,
    val riskScore: Int, // 1 to 10
    val beta: Double = 1.0,
    val moat: String = "Moderate",
    val buyDate: String = "2026-06-15"
)
