package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ai.orchestrator.SystemContext
import com.example.ui.theme.*
import com.example.ui.viewmodel.ChatMessage
import kotlinx.coroutines.launch

@Composable
fun AssistantScreen(
    chatMessages: List<ChatMessage>,
    isThinking: Boolean,
    systemContext: SystemContext,
    onSendMessage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val presetPrompts = listOf(
        "Scan NIFTY 50 again with 27 Agents",
        "What time is it now & when does market close?",
        "How many buy orders and sell orders do I have?",
        "Explain the 27 agents scanning hierarchy",
        "Top Swing Trade setup right now?"
    )

    LaunchedEffect(chatMessages.size) {
        if (chatMessages.isNotEmpty()) {
            listState.animateScrollToItem(chatMessages.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(TerminalBackground)
    ) {
        // CIO Agent Context Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = TerminalCard,
            border = androidx.compose.foundation.BorderStroke(1.dp, TerminalBorder)
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(SapphireBlue),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "CIO",
                            tint = TextContrast,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "CHIEF AI TRADING ASSISTANT",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Connected to 27 Autonomous Scanning Agents",
                            style = MaterialTheme.typography.labelSmall,
                            color = SapphireBlue
                        )
                    }

                    Surface(
                        modifier = Modifier.clickable {
                            onSendMessage("Scan NIFTY 50 again with 27 Agents")
                        },
                        color = SapphireBlueDark,
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SapphireBlue)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Scan",
                                tint = SapphireBlue,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = "Trigger 27-Scan",
                                style = MaterialTheme.typography.labelSmall,
                                color = SapphireBlue,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // System Hierarchy Banner
                Surface(
                    color = TerminalCardElevated,
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(0.5.dp, TerminalBorder)
                ) {
                    Text(
                        text = "[50 Nifty Stocks] ➔ [27 Agents Scan 24/7] ➔ [Chief AI] ➔ [Your Assistant]",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                // System Context Badges & Live Market Countdown
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ContextBadge(text = "${systemContext.currentTime} IST")
                    ContextBadge(text = systemContext.marketStatus)
                    ContextBadge(text = systemContext.marketTimingCountdown)
                }
            }
        }

        // Chat Messages Stream
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            items(chatMessages) { msg ->
                ChatMessageBubble(message = msg)
            }

            if (isThinking) {
                item {
                    Row(
                        modifier = Modifier.padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = SapphireBlue,
                            strokeWidth = 2.dp
                        )
                        Text(
                            text = "CIO Agent is synthesizing 25-agent intelligence & market context...",
                            style = MaterialTheme.typography.labelSmall,
                            color = SapphireBlue,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // Preset Prompt Chips
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(presetPrompts) { prompt ->
                Surface(
                    modifier = Modifier.clickable {
                        onSendMessage(prompt)
                    },
                    color = TerminalCardElevated,
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, TerminalBorder)
                ) {
                    Text(
                        text = prompt,
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
        }

        // Input Field Bar
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = TerminalCard,
            border = androidx.compose.foundation.BorderStroke(1.dp, TerminalBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = { Text("Ask CIO Agent about Indian stocks, thesis, macro...", color = TextMuted, fontSize = 12.sp) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SapphireBlue,
                        unfocusedBorderColor = TerminalBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedContainerColor = TerminalBackground,
                        unfocusedContainerColor = TerminalBackground
                    ),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("cio_chat_input")
                )

                IconButton(
                    onClick = {
                        if (inputText.isNotBlank()) {
                            val textToSend = inputText
                            inputText = ""
                            onSendMessage(textToSend)
                        }
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(SapphireBlue)
                        .testTag("send_cio_message_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send",
                        tint = TextContrast,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ContextBadge(text: String) {
    Surface(
        color = TerminalCardElevated,
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(0.5.dp, TerminalBorder)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted,
            fontSize = 9.sp,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Composable
private fun ChatMessageBubble(message: ChatMessage) {
    val isUser = message.sender == "USER"

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        Text(
            text = "${if (isUser) "YOU" else "CIO AGENT"} • ${message.timestamp}",
            style = MaterialTheme.typography.labelSmall,
            color = if (isUser) TextMuted else SapphireBlue,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 2.dp)
        )

        Surface(
            color = if (isUser) SapphireBlueDark else TerminalCard,
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isUser) 16.dp else 4.dp,
                bottomEnd = if (isUser) 4.dp else 16.dp
            ),
            border = androidx.compose.foundation.BorderStroke(1.dp, if (isUser) SapphireBlue else TerminalBorder),
            modifier = Modifier.widthIn(max = 320.dp)
        ) {
            Text(
                text = message.text,
                style = MaterialTheme.typography.bodyMedium,
                color = TextPrimary,
                fontSize = 12.sp,
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}
