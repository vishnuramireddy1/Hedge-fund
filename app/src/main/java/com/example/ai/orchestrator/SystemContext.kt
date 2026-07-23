package com.example.ai.orchestrator

import java.text.SimpleDateFormat
import java.util.*

data class SystemContext(
    val currentDate: String,
    val currentTime: String,
    val dayOfWeek: String,
    val monthName: String,
    val quarter: String,
    val financialYear: String,
    val marketStatus: String, // LIVE OPEN, PRE-OPEN, CLOSED, POST-CLOSE
    val marketSession: String, // Regular Trading (09:15 - 15:30 IST)
    val isTradingDay: Boolean,
    val marketOpensAt: String = "09:15 IST",
    val marketClosesAt: String = "15:30 IST",
    val marketTimingCountdown: String,
    val upcomingHolidays: List<String>,
    val upcomingEarnings: List<String>,
    val upcomingRbiMeetings: List<String>,
    val upcomingBudget: String
) {
    fun toFormattedPromptHeader(): String {
        return """
            === BHARAT INVEST OS SYSTEM CONTEXT ===
            Date: $currentDate ($dayOfWeek, $monthName)
            Current Time (IST): $currentTime
            Market Status: $marketStatus ($marketSession)
            Market Schedule: Opens at $marketOpensAt | Closes at $marketClosesAt
            Live Timing Countdown: $marketTimingCountdown
            Fiscal Period: $quarter | Financial Year: $financialYear
            Trading Day: $isTradingDay
            Upcoming RBI Policy Meeting: ${upcomingRbiMeetings.firstOrNull() ?: "N/A"}
            Upcoming Corporate Earnings: ${upcomingEarnings.joinToString(", ")}
            Upcoming NSE/BSE Holidays: ${upcomingHolidays.joinToString(", ")}
            =========================================
        """.trimIndent()
    }

    companion object {
        fun getCurrentContext(): SystemContext {
            val calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Kolkata"))
            val now = calendar.time

            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
            val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.ENGLISH)
            val dayFormat = SimpleDateFormat("EEEE", Locale.ENGLISH)
            val monthFormat = SimpleDateFormat("MMMM", Locale.ENGLISH)

            dateFormat.timeZone = TimeZone.getTimeZone("Asia/Kolkata")
            timeFormat.timeZone = TimeZone.getTimeZone("Asia/Kolkata")
            dayFormat.timeZone = TimeZone.getTimeZone("Asia/Kolkata")
            monthFormat.timeZone = TimeZone.getTimeZone("Asia/Kolkata")

            val dateStr = dateFormat.format(now)
            val timeStr = timeFormat.format(now)
            val dayOfWeek = dayFormat.format(now)
            val monthName = monthFormat.format(now)

            val monthInt = calendar.get(Calendar.MONTH) + 1 // 1..12
            val yearInt = calendar.get(Calendar.YEAR)

            // Indian Fiscal Year starts April 1st
            val fyYear = if (monthInt >= 4) yearInt else yearInt - 1
            val fyStr = "FY ${fyYear}-${(fyYear + 1) % 100}"

            val quarterStr = when (monthInt) {
                in 4..6 -> "Q1 $fyStr"
                in 7..9 -> "Q2 $fyStr"
                in 10..12 -> "Q3 $fyStr"
                else -> "Q4 $fyStr"
            }

            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)
            val timeInMinutes = hour * 60 + minute

            val isWeekend = calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY ||
                            calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY

            val marketOpenMinutes = 9 * 60 + 15 // 09:15 = 555 mins
            val marketCloseMinutes = 15 * 60 + 30 // 15:30 = 930 mins

            val (status, session, countdown) = when {
                isWeekend -> {
                    Triple("CLOSED", "Weekend Non-Trading", "Market closed for the weekend. Re-opens Monday at 09:15 IST")
                }
                timeInMinutes in 540..554 -> {
                    val minsLeft = 555 - timeInMinutes
                    Triple("PRE-OPEN", "Pre-Open Session (09:00 - 09:15 IST)", "Regular market opens in $minsLeft mins (at 09:15 IST)")
                }
                timeInMinutes in 555..930 -> {
                    val minsLeft = marketCloseMinutes - timeInMinutes
                    val h = minsLeft / 60
                    val m = minsLeft % 60
                    val countStr = if (h > 0) "${h}h ${m}m" else "${m}m"
                    Triple("LIVE OPEN", "Regular NSE/BSE Trading (09:15 - 15:30 IST)", "Market closes in $countStr (at 15:30 IST)")
                }
                timeInMinutes in 931..960 -> {
                    Triple("POST-CLOSE", "Post-Closing Session", "Regular market closed at 15:30 IST. Post-closing session active.")
                }
                timeInMinutes < 540 -> {
                    val minsLeft = 555 - timeInMinutes
                    val h = minsLeft / 60
                    val m = minsLeft % 60
                    Triple("CLOSED", "After Hours Market Closed", "Market opens today in ${h}h ${m}m (at 09:15 IST)")
                }
                else -> {
                    val minsLeft = (24 * 60 - timeInMinutes) + 555
                    val h = minsLeft / 60
                    val m = minsLeft % 60
                    Triple("CLOSED", "After Hours Market Closed", "Market closed for today. Opens tomorrow at 09:15 IST (in ${h}h ${m}m)")
                }
            }

            return SystemContext(
                currentDate = dateStr,
                currentTime = timeStr,
                dayOfWeek = dayOfWeek,
                monthName = monthName,
                quarter = quarterStr,
                financialYear = fyStr,
                marketStatus = status,
                marketSession = session,
                isTradingDay = !isWeekend,
                marketOpensAt = "09:15 IST",
                marketClosesAt = "15:30 IST",
                marketTimingCountdown = countdown,
                upcomingHolidays = listOf("Aug 15 (Independence Day)", "Oct 02 (Gandhi Jayanti)", "Oct 20 (Diwali Laxmi Pujan)"),
                upcomingEarnings = listOf("Reliance Industries Q2", "Tata Motors Q2", "HDFC Bank Q2", "Infosys Q2"),
                upcomingRbiMeetings = listOf("Aug 08 (MPC Rate Announcement)"),
                upcomingBudget = "Union Budget FY27 Consultations"
            )
        }
    }
}
