package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.KnowledgeArticle
import com.example.ui.theme.*

@Composable
fun KnowledgeScreen(
    articles: List<KnowledgeArticle>,
    onAddArticle: (KnowledgeArticle) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    val categories = listOf("ALL", "COMPANIES", "SECTORS", "MACRO", "RECOMMENDATIONS")

    val filteredArticles = if (selectedCategory == null || selectedCategory == "ALL") {
        articles
    } else {
        articles.filter { it.category.equals(selectedCategory, ignoreCase = true) }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(TerminalBackground)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 12.dp)
    ) {
        item {
            Text(
                text = "KNOWLEDGE BASE & HISTORICAL MEMORY",
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                categories.forEach { cat ->
                    val isSelected = (selectedCategory == null && cat == "ALL") || selectedCategory == cat
                    Surface(
                        modifier = Modifier
                            .clickable { selectedCategory = if (cat == "ALL") null else cat },
                        color = if (isSelected) SapphireBlue else TerminalCard,
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) SapphireBlue else TerminalBorder)
                    ) {
                        Text(
                            text = cat,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isSelected) TextContrast else TextSecondary,
                            fontSize = 9.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        items(filteredArticles) { article ->
            KnowledgeArticleCard(article = article)
        }
    }
}

@Composable
private fun KnowledgeArticleCard(article: KnowledgeArticle) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = TerminalCard,
        shape = RoundedCornerShape(18.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, TerminalBorder)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "knowledge/${article.category.lowercase()}/${article.id}.md",
                    style = MaterialTheme.typography.labelSmall,
                    color = SapphireBlue,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = article.updatedAt,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    fontSize = 9.sp
                )
            }

            Text(
                text = article.title,
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )

            Surface(
                color = TerminalCardElevated,
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = article.markdownContent,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimary,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
    }
}
