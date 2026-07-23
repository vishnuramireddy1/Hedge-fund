package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.StockQuote
import com.example.ui.theme.*

@Composable
fun MarketTickerBar(
    stockQuotes: List<StockQuote>,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .background(TerminalBackground),
        color = TerminalCard,
        border = androidx.compose.foundation.BorderStroke(1.dp, TerminalBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "INDICES:",
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(end = 8.dp)
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Core Market Indices
                item {
                    TickerItem(name = "NIFTY 50", price = "24,141.95", changePct = 0.42)
                }
                item {
                    TickerItem(name = "BANK NIFTY", price = "51,200.40", changePct = -0.28)
                }
                item {
                    TickerItem(name = "SENSEX", price = "79,420.10", changePct = 0.38)
                }
                item {
                    TickerItem(name = "NIFTY MIDCAP", price = "56,810.25", changePct = 1.15)
                }

                // Featured Stocks
                items(stockQuotes) { quote ->
                    TickerItem(
                        name = quote.symbol,
                        price = "₹${String.format("%.2f", quote.price)}",
                        changePct = quote.changePct
                    )
                }
            }
        }
    }
}

@Composable
private fun TickerItem(
    name: String,
    price: String,
    changePct: Double
) {
    val isPositive = changePct >= 0
    val color = if (isPositive) BullishGreen else BearishRed
    val sign = if (isPositive) "+" else ""

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = price,
            style = MaterialTheme.typography.labelSmall,
            color = TextPrimary,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "$sign${String.format("%.2f", changePct)}%",
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Bold
        )
    }
}
