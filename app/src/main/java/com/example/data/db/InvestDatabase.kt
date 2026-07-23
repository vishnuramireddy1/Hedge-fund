package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        PortfolioHolding::class,
        TradeJournalEntry::class,
        DailyJournalEntry::class,
        AgentLog::class,
        KnowledgeArticle::class,
        WatchlistItem::class,
        StockQuote::class
    ],
    version = 1,
    exportSchema = false
)
abstract class InvestDatabase : RoomDatabase() {
    abstract fun investDao(): InvestDao

    companion object {
        @Volatile
        private var INSTANCE: InvestDatabase? = null

        fun getDatabase(context: Context): InvestDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    InvestDatabase::class.java,
                    "bharat_invest_os.db"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
