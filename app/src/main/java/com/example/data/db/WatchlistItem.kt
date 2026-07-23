package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "watchlist")
data class WatchlistItem(
    @PrimaryKey val symbol: String,
    val name: String,
    val sector: String,
    val price: Double,
    val changePct: Double,
    val catalyst: String,
    val thesis: String,
    val rating: String // BUY, WATCH, AVOID
)
