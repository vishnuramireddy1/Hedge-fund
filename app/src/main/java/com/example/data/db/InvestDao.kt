package com.example.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface InvestDao {

    // Portfolio
    @Query("SELECT * FROM portfolio_holdings")
    fun getAllHoldings(): Flow<List<PortfolioHolding>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHolding(holding: PortfolioHolding)

    @Delete
    suspend fun deleteHolding(holding: PortfolioHolding)

    // Trade Journal
    @Query("SELECT * FROM trade_journal ORDER BY tradeId DESC")
    fun getAllTradeEntries(): Flow<List<TradeJournalEntry>>

    @Query("SELECT * FROM trade_journal ORDER BY tradeId DESC LIMIT 50")
    suspend fun getRecentTradeEntriesSync(): List<TradeJournalEntry>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTradeEntry(entry: TradeJournalEntry)

    // Daily Journal
    @Query("SELECT * FROM daily_journal ORDER BY date DESC")
    fun getAllDailyJournalEntries(): Flow<List<DailyJournalEntry>>

    @Query("SELECT * FROM daily_journal WHERE date = :date LIMIT 1")
    suspend fun getDailyJournalByDate(date: String): DailyJournalEntry?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyJournal(entry: DailyJournalEntry)

    // Agent Logs
    @Query("SELECT * FROM agent_logs ORDER BY id DESC LIMIT 100")
    fun getAllAgentLogs(): Flow<List<AgentLog>>

    @Query("SELECT * FROM agent_logs ORDER BY id DESC LIMIT 10")
    suspend fun getRecentAgentLogsSync(): List<AgentLog>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAgentLog(log: AgentLog)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAgentLogs(logs: List<AgentLog>)

    // Knowledge Base
    @Query("SELECT * FROM knowledge_base ORDER BY updatedAt DESC")
    fun getAllKnowledgeArticles(): Flow<List<KnowledgeArticle>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKnowledgeArticle(article: KnowledgeArticle)

    // Watchlist
    @Query("SELECT * FROM watchlist")
    fun getWatchlist(): Flow<List<WatchlistItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWatchlistItem(item: WatchlistItem)

    @Delete
    suspend fun deleteWatchlistItem(item: WatchlistItem)

    // Stock Quotes
    @Query("SELECT * FROM stock_quotes")
    fun getAllStockQuotes(): Flow<List<StockQuote>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStockQuotes(quotes: List<StockQuote>)
}
