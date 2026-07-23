package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun AppLockOverlay(
    onUnlock: (String) -> Boolean,
    modifier: Modifier = Modifier
) {
    var pinInput by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(TerminalBackground)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = TerminalCard,
            shape = RoundedCornerShape(24.dp),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, SapphireBlue)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(SapphireBlueGlow),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "Shield Lock",
                        tint = SapphireBlue,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Text(
                    text = "BHARAT INVEST OS",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Text(
                    text = "TERMINAL SECURITY GUARD",
                    style = MaterialTheme.typography.labelMedium,
                    color = SapphireBlue,
                    letterSpacing = 1.5.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Enter your 6-digit salted PBKDF2 PIN to access institutional portfolio metrics & CIO intelligence.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                OutlinedTextField(
                    value = pinInput,
                    onValueChange = {
                        if (it.length <= 6 && it.all { char -> char.isDigit() }) {
                            pinInput = it
                            errorMessage = null
                        }
                    },
                    placeholder = { Text("6-Digit PIN", color = TextMuted) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SapphireBlue,
                        unfocusedBorderColor = TerminalBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("unlock_pin_input")
                )

                errorMessage?.let { err ->
                    Text(
                        text = err,
                        color = BearishRed,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = {
                        if (pinInput.length == 6) {
                            val success = onUnlock(pinInput)
                            if (!success) {
                                errorMessage = "Incorrect PIN. Security attempt logged."
                            }
                        } else {
                            errorMessage = "Please enter full 6 digits."
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SapphireBlue),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("unlock_app_button")
                ) {
                    Icon(imageVector = Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "UNLOCK TERMINAL",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextContrast
                    )
                }
            }
        }
    }
}
