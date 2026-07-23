package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_journal")
data class DailyJournalEntry(
    @PrimaryKey val date: String, // YYYY-MM-DD
    val marketSummary: String,
    val portfolioSummary: String,
    val newsSummary: String,
    val recommendations: String,
    val lessons: String,
    val mistakes: String,
    val watchlistSymbols: String // Comma separated symbols
)
