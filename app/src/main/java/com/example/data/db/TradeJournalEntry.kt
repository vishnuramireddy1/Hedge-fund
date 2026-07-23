package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trade_journal")
data class TradeJournalEntry(
    @PrimaryKey(autoGenerate = true) val tradeId: Long = 0,
    val symbol: String,
    val companyName: String,
    val buyDate: String,
    val buyPrice: Double,
    val quantity: Int,
    val reason: String,
    val expectedReturnPct: Double,
    val expectedHoldingDays: Int,
    val riskScore: Int,
    val confidencePct: Int,
    val stopLoss: Double,
    val target: Double,
    val exitPrice: Double? = null,
    val exitDate: String? = null,
    val actualReturnPct: Double? = null,
    val lessonsLearned: String = "",
    val aiPerformanceScore: Int = 85, // 0 to 100
    val status: String = "OPEN" // OPEN, CLOSED, STOPPED_OUT, TARGET_HIT
)
