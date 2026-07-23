package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.DailyJournalEntry
import com.example.data.db.TradeJournalEntry
import com.example.ui.theme.*

@Composable
fun JournalScreen(
    tradeEntries: List<TradeJournalEntry>,
    dailyJournals: List<DailyJournalEntry>,
    onAddTrade: (String, Double, Int, String, Double, Double) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) } // 0: Trade Journal, 1: Daily Journal
    var showRecordDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(TerminalBackground)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 12.dp)
    ) {
        // Tab Switcher
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { selectedTab = 0 },
                    color = if (selectedTab == 0) SapphireBlue else TerminalCard,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (selectedTab == 0) SapphireBlue else TerminalBorder)
                ) {
                    Box(modifier = Modifier.padding(10.dp), contentAlignment = Alignment.Center) {
                        Text(text = "TRADE JOURNAL (${tradeEntries.size})", style = MaterialTheme.typography.labelSmall, color = if (selectedTab == 0) TextContrast else TextSecondary, fontWeight = FontWeight.Bold)
                    }
                }

                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { selectedTab = 1 },
                    color = if (selectedTab == 1) SapphireBlue else TerminalCard,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (selectedTab == 1) SapphireBlue else TerminalBorder)
                ) {
                    Box(modifier = Modifier.padding(10.dp), contentAlignment = Alignment.Center) {
                        Text(text = "DAILY LOGS (${dailyJournals.size})", style = MaterialTheme.typography.labelSmall, color = if (selectedTab == 1) TextContrast else TextSecondary, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        if (selectedTab == 0) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "RECORDED TRADES", style = MaterialTheme.typography.labelSmall, color = TextMuted, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    Button(
                        onClick = { showRecordDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = SapphireBlue),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("record_trade_button")
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "RECORD TRADE", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    }
                }
            }

            items(tradeEntries) { entry ->
                TradeJournalCard(entry = entry)
            }
        } else {
            items(dailyJournals) { journal ->
                DailyJournalCard(journal = journal)
            }
        }
    }

    if (showRecordDialog) {
        RecordTradeDialog(
            onDismiss = { showRecordDialog = false },
            onSave = { symbol, price, qty, reason, target, stop ->
                onAddTrade(symbol, price, qty, reason, target, stop)
                showRecordDialog = false
            }
        )
    }
}

@Composable
private fun TradeJournalCard(entry: TradeJournalEntry) {
    val isClosed = entry.status == "TARGET_HIT" || entry.status == "CLOSED"
    val returnVal = entry.actualReturnPct ?: entry.expectedReturnPct

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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(text = "#${entry.tradeId} ${entry.symbol}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Surface(
                        color = if (isClosed) BullishGreenGlow else SapphireBlueGlow,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = entry.status,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isClosed) BullishGreen else SapphireBlue,
                            fontSize = 8.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Text(
                    text = "${if (returnVal >= 0) "+" else ""}${String.format("%.1f", returnVal)}%",
                    style = MaterialTheme.typography.titleMedium,
                    color = if (returnVal >= 0) BullishGreen else BearishRed,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = "Buy Date: ${entry.buyDate} • Price: ₹${entry.buyPrice} • Qty: ${entry.quantity}",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                fontSize = 11.sp
            )

            Text(
                text = "Target: ₹${entry.target} | Stop Loss: ₹${entry.stopLoss} | AI Score: ${entry.aiPerformanceScore}/100",
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
                fontSize = 10.sp
            )

            Surface(
                color = TerminalCardElevated,
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(text = "REASON: ${entry.reason}", style = MaterialTheme.typography.bodyMedium, color = TextPrimary, fontSize = 11.sp)
                    if (entry.lessonsLearned.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "LESSONS: ${entry.lessonsLearned}", style = MaterialTheme.typography.bodyMedium, color = WarningAmber, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun DailyJournalCard(journal: DailyJournalEntry) {
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
            Text(text = "journal/${journal.date.replace("-", "/")}.md", style = MaterialTheme.typography.labelLarge, color = SapphireBlue)

            Text(text = "MARKET SUMMARY:\n${journal.marketSummary}", style = MaterialTheme.typography.bodyMedium, color = TextPrimary, fontSize = 11.sp)
            Text(text = "RECOMMENDATIONS:\n${journal.recommendations}", style = MaterialTheme.typography.bodyMedium, color = TextSecondary, fontSize = 11.sp)
            Text(text = "LESSONS & MISTAKES:\n${journal.lessons} ${journal.mistakes}", style = MaterialTheme.typography.bodyMedium, color = WarningAmber, fontSize = 11.sp)
        }
    }
}

@Composable
private fun RecordTradeDialog(
    onDismiss: () -> Unit,
    onSave: (String, Double, Int, String, Double, Double) -> Unit
) {
    var symbol by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("1000.0") }
    var qty by remember { mutableStateOf("50") }
    var reason by remember { mutableStateOf("Technical swing breakout above resistance.") }
    var target by remember { mutableStateOf("1200.0") }
    var stop by remember { mutableStateOf("920.0") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = TerminalCard,
        title = { Text("Record Trade in Journal", color = TextPrimary) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = symbol,
                    onValueChange = { symbol = it },
                    label = { Text("Symbol", color = TextMuted) },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SapphireBlue, unfocusedBorderColor = TerminalBorder, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary),
                    modifier = Modifier.fillMaxWidth().testTag("journal_symbol_input")
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = price,
                        onValueChange = { price = it },
                        label = { Text("Buy Price", color = TextMuted) },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SapphireBlue, unfocusedBorderColor = TerminalBorder, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = qty,
                        onValueChange = { qty = it },
                        label = { Text("Qty", color = TextMuted) },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SapphireBlue, unfocusedBorderColor = TerminalBorder, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary),
                        modifier = Modifier.weight(1f)
                    )
                }
                OutlinedTextField(
                    value = reason,
                    onValueChange = { reason = it },
                    label = { Text("Trade Reason", color = TextMuted) },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SapphireBlue, unfocusedBorderColor = TerminalBorder, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (symbol.isNotBlank()) {
                        onSave(
                            symbol,
                            price.toDoubleOrNull() ?: 100.0,
                            qty.toIntOrNull() ?: 10,
                            reason,
                            target.toDoubleOrNull() ?: 120.0,
                            stop.toDoubleOrNull() ?: 90.0
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = SapphireBlue),
                modifier = Modifier.testTag("save_journal_trade_button")
            ) {
                Text("RECORD TRADE", color = TextContrast)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("CANCEL", color = TextMuted) }
        }
    )
}
