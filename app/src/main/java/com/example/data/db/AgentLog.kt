package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "agent_logs")
data class AgentLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val agentName: String,
    val timestamp: String,
    val status: String, // IDLE, RUNNING, SUCCESS, WARNING, ERROR
    val currentTask: String,
    val confidencePct: Int,
    val recentFindings: String,
    val memoryUsageMb: Int = 18,
    val tokenUsage: Int = 340,
    val executionTimeMs: Long = 120,
    val errorMessage: String? = null
)
