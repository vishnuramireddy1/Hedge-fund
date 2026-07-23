package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "knowledge_base")
data class KnowledgeArticle(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val category: String, // COMPANIES, SECTORS, MACRO, ECONOMY, JOURNAL, RECOMMENDATIONS, RESEARCH
    val title: String,
    val markdownContent: String,
    val updatedAt: String,
    val tags: String = ""
)
