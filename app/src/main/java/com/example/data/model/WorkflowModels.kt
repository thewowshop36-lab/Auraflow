package com.example.data.model

enum class TriggerType(val displayName: String, val iconName: String) {
    LOCATION("Location Geofence", "LocationOn"),
    TIME_SCHEDULE("Time & Schedule", "Schedule"),
    BATTERY("Battery Level", "BatteryChargingFull"),
    HEADPHONES("Headphones / Audio", "Headphones"),
    BLUETOOTH("Bluetooth Car Audio", "Bluetooth"),
    NOTIFICATION_RECEIVED("Notification Keyword", "NotificationsActive"),
    APP_OPENED("App Launched", "Apps")
}

enum class ActionType(val displayName: String, val description: String) {
    MUTE_PHONE("Mute Ringer", "Set device ringer to silent"),
    ENABLE_VIBRATE("Vibrate Mode", "Set device ringer to vibrate"),
    TOGGLE_DND("Enable Do Not Disturb", "Silence non-priority interruptions"),
    SEND_AUTO_REPLY("Send Smart Auto-Reply", "Auto-respond via SMS or Messaging"),
    OPEN_APP("Launch Application", "Open a target app (e.g. Spotify, Maps)"),
    SET_BRIGHTNESS("Adjust Brightness", "Change display brightness level"),
    SPEAK_NOTIFICATION("Read Aloud / Speak", "Voice announcement through speaker"),
    ENABLE_BATTERY_SAVER("Ultra Power Saver", "Optimize power consumption"),
    LOG_COMMUTE("Log Activity", "Record timestamp and location log"),
    TOGGLE_WIFI("Toggle Wi-Fi", "Switch Wi-Fi state"),
    SEND_WEBHOOK("Trigger Webhook", "Send HTTP POST automation event")
}

enum class WorkflowCategory(val displayName: String) {
    WORK("Work & Focus"),
    COMMUTE("Commute & Travel"),
    WELLNESS("Wellness & Health"),
    BATTERY("Battery & Power"),
    HOME("Home & Night"),
    MESSAGING("Smart Messaging")
}

data class TriggerConfig(
    val type: TriggerType,
    val targetValue: String, // e.g., "Work Office", "08:30 AM", "15%", "Car Bluetooth"
    val secondaryValue: String? = null // e.g. "Radius 150m", "Weekdays"
)

data class ConditionConfig(
    val description: String,
    val isEnabled: Boolean = true
)

data class ActionConfig(
    val id: String,
    val type: ActionType,
    val parameter: String = "", // e.g. "Heading home soon!", "Spotify", "30%"
    val isEnabled: Boolean = true
)

data class ContextState(
    val locationName: String = "Office / Work",
    val timeOfDay: String = "14:30 (Weekdays)",
    val batteryLevel: Int = 78,
    val isWifiConnected: Boolean = true,
    val isBluetoothCarConnected: Boolean = false,
    val isDndActive: Boolean = false
)
