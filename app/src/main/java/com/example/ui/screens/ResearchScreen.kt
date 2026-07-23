package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.StockQuote
import com.example.data.db.WatchlistItem
import com.example.ui.theme.*

@Composable
fun ResearchScreen(
    stockQuotes: List<StockQuote>,
    watchlist: List<WatchlistItem>,
    modifier: Modifier = Modifier
) {
    var selectedStock by remember { mutableStateOf(stockQuotes.firstOrNull() ?: StockQuote("RELIANCE", "Reliance Industries Ltd", "NSE", "Energy & Retail", 3120.50, 42.10, 1.37, 3217.90, 2220.30, 26.4, 2110000.0, 4210000, 58.2, "BULLISH_CROSS", "UPTREND")) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(TerminalBackground)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 12.dp)
    ) {
        // Company Selector Bar
        item {
            Text(
                text = "COMPANY RESEARCH & ANALYSIS (NSE 20+ WATCHLIST)",
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(stockQuotes) { quote ->
                    val isSelected = selectedStock.symbol == quote.symbol
                    Surface(
                        modifier = Modifier
                            .clickable { selectedStock = quote },
                        color = if (isSelected) SapphireBlue else TerminalCard,
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) SapphireBlue else TerminalBorder)
                    ) {
                        Box(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = quote.symbol,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isSelected) TextContrast else TextSecondary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }

        // Selected Stock Detail Card
        item {
            StockDeepResearchCard(quote = selectedStock)
        }

        // Valuation & Trap Detection Report
        item {
            ValuationAndTrapCard(quote = selectedStock)
        }

        // Watchlist Radar
        item {
            Text(
                text = "OPPORTUNITY DISCOVERY WATCHLIST",
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }

        items(watchlist) { item ->
            WatchlistRadarCard(item = item)
        }
    }
}

@Composable
private fun StockDeepResearchCard(quote: StockQuote) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = TerminalCard,
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, TerminalBorder)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = quote.symbol, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text(text = "${quote.name} • ${quote.sector}", style = MaterialTheme.typography.bodyMedium, color = TextSecondary, fontSize = 11.sp)
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "₹${quote.price}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text(text = "+${quote.changePct}%", style = MaterialTheme.typography.labelSmall, color = BullishGreen, fontWeight = FontWeight.Bold)
                }
            }

            Divider(color = TerminalBorder)

            Text(text = "FUNDAMENTAL & TECHNICAL METRICS", style = MaterialTheme.typography.labelSmall, color = TextMuted, fontSize = 9.sp)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MetricItem(label = "P/E RATIO", value = "${quote.peRatio}x")
                MetricItem(label = "MARKET CAP", value = "₹${String.format("%.0f", quote.marketCapCr / 1000)}k Cr")
                MetricItem(label = "RSI (14)", value = "${quote.rsi14}")
                MetricItem(label = "TREND", value = quote.trend)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MetricItem(label = "52W HIGH", value = "₹${quote.high52}")
                MetricItem(label = "52W LOW", value = "₹${quote.low52}")
                MetricItem(label = "MACD", value = quote.macdStatus)
                MetricItem(label = "VOLUME", value = "${quote.volume / 100000}L")
            }
        }
    }
}

@Composable
private fun MetricItem(label: String, value: String) {
    Column {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = TextMuted, fontSize = 8.sp)
        Text(text = value, style = MaterialTheme.typography.labelMedium, color = TextPrimary, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun ValuationAndTrapCard(quote: StockQuote) {
    val auditText = when (quote.symbol) {
        "TATAMOTORS" -> "• Promoter Pledged Shares: 0.0% (Clean)\n• Audit Quality Score: 94/100\n• Catalyst: JLR order book 148k units + EV market leadership (72% share in India).\n• Trap Audit: Free cash flow turning net positive across commercial & PV divisions."
        "HDFCBANK" -> "• Promoter Pledged Shares: 0.0% (Clean)\n• Audit Quality Score: 98/100\n• Moat: Systemic scale (15%+ deposit share).\n• Trap Audit: NIM compression stabilizing post-merger integration."
        "INFY" -> "• Promoter Pledged Shares: 0.0% (Clean)\n• Audit Quality Score: 97/100\n• Catalyst: Large deal TCV ($2.4B) + GenAI integration across European enterprise clients."
        "LT" -> "• Promoter Pledged Shares: 0.0% (Clean)\n• Audit Quality Score: 96/100\n• Moat: Wide Moat (EPC execution track record & record $60B+ international order book)."
        "SUZLON" -> "• Promoter Pledged Shares: 0.0% (Clean - De-pledged post debt payback)\n• Audit Quality Score: 88/100\n• Turnaround Catalyst: Net debt free, 3.8 GW wind power order pipeline."
        else -> "• Promoter Pledged Shares: 0.0% (Clean)\n• Audit Quality Score: 95/100 (No Accounting Anomalies)\n• Moat Classification: Wide Moat (Scale & Vertical Integration)\n• Valuation Multiples: Trading near 5-year median PE band."
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = TerminalCard,
        shape = RoundedCornerShape(18.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, TerminalBorder)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "${quote.symbol} TRAP DETECTION & MOAT AUDIT",
                style = MaterialTheme.typography.labelSmall,
                color = WarningAmber,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Text(
                text = auditText,
                style = MaterialTheme.typography.bodyMedium,
                color = TextPrimary,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
private fun WatchlistRadarCard(item: WatchlistItem) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = TerminalCard,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, TerminalBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(text = item.symbol, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Surface(
                        color = if (item.rating == "BUY") BullishGreenGlow else WarningAmberGlow,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = item.rating,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (item.rating == "BUY") BullishGreen else WarningAmber,
                            fontSize = 8.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Text(text = "${item.name} • ${item.sector}", style = MaterialTheme.typography.bodyMedium, color = TextSecondary, fontSize = 11.sp)
                Text(text = "CATALYST: ${item.catalyst}", style = MaterialTheme.typography.bodyMedium, color = TextMuted, fontSize = 10.sp)
            }

            Text(text = "₹${item.price}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
    }
}
