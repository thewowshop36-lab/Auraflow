package com.example.data.model

enum class NotificationPriority(val label: String) {
    URGENT("Urgent & VIP"),
    WORK("Work & Tasks"),
    SOCIAL("Social & Chat"),
    UPDATE("System & Updates")
}

enum class ReplyTone(val title: String, val emoji: String) {
    PROFESSIONAL("Professional", "💼"),
    QUICK("Quick & Direct", "⚡"),
    FRIENDLY("Warm & Friendly", "😊"),
    BUSY("In Meeting / Busy", "⏳")
}

data class SmartReplyOption(
    val tone: ReplyTone,
    val replyText: String
)
