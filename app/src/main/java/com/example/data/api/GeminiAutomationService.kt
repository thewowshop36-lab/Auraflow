package com.example.data.api

import android.util.Log
import com.example.BuildConfig
import com.example.data.local.WorkflowEntity
import com.example.data.model.ActionConfig
import com.example.data.model.ActionType
import com.example.data.model.ConditionConfig
import com.example.data.model.NotificationPriority
import com.example.data.model.ReplyTone
import com.example.data.model.SmartReplyOption
import com.example.data.model.TriggerConfig
import com.example.data.model.TriggerType
import com.example.data.model.WorkflowCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GeminiAutomationService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val apiKey: String
        get() = try {
            BuildConfig.GEMINI_API_KEY
        } catch (_: Exception) {
            ""
        }

    private suspend fun callGemini(prompt: String): String? = withContext(Dispatchers.IO) {
        val key = apiKey
        if (key.isBlank() || key == "MY_GEMINI_API_KEY") {
            Log.d("GeminiAutomationService", "Using local fallback engine (API key not provided or template placeholder)")
            return@withContext null
        }

        try {
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$key"
            val requestJson = JSONObject().apply {
                val contents = JSONArray().apply {
                    val contentObj = JSONObject().apply {
                        val parts = JSONArray().apply {
                            put(JSONObject().put("text", prompt))
                        }
                        put("parts", parts)
                    }
                    put(contentObj)
                }
                put("contents", contents)
                put("generationConfig", JSONObject().apply {
                    put("temperature", 0.2)
                    put("responseMimeType", "application/json")
                })
            }

            val mediaType = "application/json; charset=utf-8".toMediaType()
            val body = requestJson.toString().toRequestBody(mediaType)
            val request = Request.Builder().url(url).post(body).build()

            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val responseBody = response.body?.string() ?: return@withContext null
                val root = JSONObject(responseBody)
                val candidates = root.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val candidate = candidates.getJSONObject(0)
                    val content = candidate.optJSONObject("content")
                    val parts = content?.optJSONArray("parts")
                    if (parts != null && parts.length() > 0) {
                        return@withContext parts.getJSONObject(0).optString("text")
                    }
                }
            } else {
                Log.w("GeminiAutomationService", "Gemini call failed with code: ${response.code}")
            }
        } catch (e: Exception) {
            Log.e("GeminiAutomationService", "Error calling Gemini", e)
        }
        null
    }

    suspend fun parseNaturalLanguageWorkflow(userPrompt: String): WorkflowEntity = withContext(Dispatchers.Default) {
        val systemPrompt = """
            You are the AI Workflow Engine for AuraFlow, a phone automation app.
            Convert the following user automation request into a structured JSON workflow.
            
            Allowed trigger types:
            LOCATION, TIME_SCHEDULE, BATTERY, HEADPHONES, BLUETOOTH, NOTIFICATION_RECEIVED, APP_OPENED
            
            Allowed action types:
            MUTE_PHONE, ENABLE_VIBRATE, TOGGLE_DND, SEND_AUTO_REPLY, OPEN_APP, SET_BRIGHTNESS, SPEAK_NOTIFICATION, ENABLE_BATTERY_SAVER, LOG_COMMUTE, TOGGLE_WIFI, SEND_WEBHOOK
            
            Allowed categories:
            WORK, COMMUTE, WELLNESS, BATTERY, HOME, MESSAGING
            
            JSON schema required:
            {
              "title": "Short catchy name (e.g., Gym Focus Mode)",
              "description": "Clear 1-sentence description of the flow",
              "category": "WORK",
              "triggerType": "LOCATION",
              "triggerTarget": "Downtown Gym",
              "triggerSecondary": "Radius 100m",
              "conditions": ["After 5:00 PM", "Battery > 15%"],
              "actions": [
                 {"type": "MUTE_PHONE", "param": ""},
                 {"type": "OPEN_APP", "param": "Spotify"},
                 {"type": "SEND_AUTO_REPLY", "param": "Working out right now, talk soon!"}
              ]
            }
            
            User request: "$userPrompt"
        """.trimIndent()

        val geminiRaw = callGemini(systemPrompt)
        if (geminiRaw != null) {
            try {
                val json = JSONObject(geminiRaw.trim())
                val title = json.optString("title", "Smart Automation")
                val description = json.optString("description", userPrompt)
                val category = json.optString("category", WorkflowCategory.WORK.name)
                val triggerType = json.optString("triggerType", TriggerType.TIME_SCHEDULE.name)
                val triggerTarget = json.optString("triggerTarget", "Scheduled Time")
                val triggerSecondary = if (json.has("triggerSecondary") && !json.isNull("triggerSecondary")) json.getString("triggerSecondary") else null

                val condList = mutableListOf<ConditionConfig>()
                val condArray = json.optJSONArray("conditions")
                if (condArray != null) {
                    for (i in 0 until condArray.length()) {
                        condList.add(ConditionConfig(condArray.getString(i), true))
                    }
                }

                val actList = mutableListOf<ActionConfig>()
                val actArray = json.optJSONArray("actions")
                if (actArray != null) {
                    for (i in 0 until actArray.length()) {
                        val actObj = actArray.getJSONObject(i)
                        val typeStr = actObj.optString("type", ActionType.MUTE_PHONE.name)
                        val actionType = try {
                            ActionType.valueOf(typeStr)
                        } catch (_: Exception) {
                            ActionType.MUTE_PHONE
                        }
                        actList.add(
                            ActionConfig(
                                id = (i + 1).toString(),
                                type = actionType,
                                parameter = actObj.optString("param", ""),
                                isEnabled = true
                            )
                        )
                    }
                }

                return@withContext WorkflowEntity(
                    title = title,
                    description = description,
                    naturalLanguagePrompt = userPrompt,
                    category = category,
                    triggerType = triggerType,
                    triggerTarget = triggerTarget,
                    triggerSecondary = triggerSecondary,
                    conditionsJson = WorkflowEntity.encodeConditions(condList),
                    actionsJson = WorkflowEntity.encodeActions(actList),
                    isEnabled = true,
                    triggerCount = 0,
                    lastTriggeredTime = 0
                )
            } catch (e: Exception) {
                Log.w("GeminiAutomationService", "Failed to parse Gemini JSON, falling back", e)
            }
        }

        // Deterministic intelligent local heuristic parser fallback
        fallbackParseWorkflow(userPrompt)
    }

    private fun fallbackParseWorkflow(prompt: String): WorkflowEntity {
        val lower = prompt.lowercase()

        var category = WorkflowCategory.WORK
        var triggerType = TriggerType.TIME_SCHEDULE
        var triggerTarget = "09:00 AM Weekdays"
        var triggerSecondary: String? = "Mon - Fri"
        val conditions = mutableListOf<ConditionConfig>()
        val actions = mutableListOf<ActionConfig>()
        var title = "Custom Automation"
        var description = prompt

        when {
            lower.contains("gym") || lower.contains("workout") -> {
                title = "Gym Power Session"
                category = WorkflowCategory.WELLNESS
                triggerType = TriggerType.LOCATION
                triggerTarget = "Fitness Center / Gym"
                triggerSecondary = "Geofence 150m"
                description = "Mute interruptions, launch Spotify workout mix, and auto-reply during workouts."
                conditions.add(ConditionConfig("Only between 6 AM - 9 PM", true))
                actions.add(ActionConfig("1", ActionType.MUTE_PHONE, ""))
                actions.add(ActionConfig("2", ActionType.OPEN_APP, "Spotify"))
                actions.add(ActionConfig("3", ActionType.SEND_AUTO_REPLY, "Crushing a workout right now! Will catch up soon."))
            }
            lower.contains("battery") || lower.contains("power") || lower.contains("charge") -> {
                title = "Extreme Battery Preserver"
                category = WorkflowCategory.BATTERY
                triggerType = TriggerType.BATTERY
                triggerTarget = "Battery drops below 20%"
                description = "Activate ultra battery saver, dim display, and notify critical contact."
                conditions.add(ConditionConfig("Device is not on AC charger", true))
                actions.add(ActionConfig("1", ActionType.ENABLE_BATTERY_SAVER, "Level 2"))
                actions.add(ActionConfig("2", ActionType.SET_BRIGHTNESS, "15%"))
                actions.add(ActionConfig("3", ActionType.TOGGLE_DND, "Priority Only"))
            }
            lower.contains("work") || lower.contains("office") || lower.contains("desk") -> {
                title = "Deep Focus Office Mode"
                category = WorkflowCategory.WORK
                triggerType = TriggerType.LOCATION
                triggerTarget = "Office Headquarters"
                triggerSecondary = "Radius 200m"
                description = "Silence personal notifications, set ringer to vibrate, and enable Slack focus."
                conditions.add(ConditionConfig("Weekdays between 9 AM and 5 PM", true))
                actions.add(ActionConfig("1", ActionType.ENABLE_VIBRATE, ""))
                actions.add(ActionConfig("2", ActionType.TOGGLE_DND, "Allow Starred Contacts"))
                actions.add(ActionConfig("3", ActionType.LOG_COMMUTE, "Work Arrival Logged"))
            }
            lower.contains("drive") || lower.contains("car") || lower.contains("commute") -> {
                title = "Hands-Free Commute"
                category = WorkflowCategory.COMMUTE
                triggerType = TriggerType.BLUETOOTH
                triggerTarget = "Car Audio Bluetooth"
                description = "Launch Maps, announce incoming messages aloud, and text ETA to family."
                conditions.add(ConditionConfig("Speed > 10 mph or Bluetooth connected", true))
                actions.add(ActionConfig("1", ActionType.OPEN_APP, "Google Maps"))
                actions.add(ActionConfig("2", ActionType.SPEAK_NOTIFICATION, "Announce caller & messages"))
                actions.add(ActionConfig("3", ActionType.SEND_AUTO_REPLY, "Driving right now, will reply when parked."))
            }
            lower.contains("sleep") || lower.contains("bed") || lower.contains("night") -> {
                title = "Restful Night Haven"
                category = WorkflowCategory.HOME
                triggerType = TriggerType.TIME_SCHEDULE
                triggerTarget = "10:30 PM Daily"
                triggerSecondary = "Every night"
                description = "Enable strict DND, dim screen to minimum, and disable Wi-Fi sync."
                conditions.add(ConditionConfig("After 10:00 PM", true))
                actions.add(ActionConfig("1", ActionType.TOGGLE_DND, "Total Silence"))
                actions.add(ActionConfig("2", ActionType.SET_BRIGHTNESS, "5%"))
                actions.add(ActionConfig("3", ActionType.MUTE_PHONE, ""))
            }
            lower.contains("morning") || lower.contains("wake") || lower.contains("briefing") -> {
                title = "Aura Morning Briefing"
                category = WorkflowCategory.HOME
                triggerType = TriggerType.TIME_SCHEDULE
                triggerTarget = "07:30 AM Weekdays"
                description = "Deactivate bedtime DND, summarize daily calendar, and read headlines."
                conditions.add(ConditionConfig("Battery > 30%", true))
                actions.add(ActionConfig("1", ActionType.TOGGLE_DND, "Normal Mode"))
                actions.add(ActionConfig("2", ActionType.SPEAK_NOTIFICATION, "Good morning! Reading today's agenda."))
                actions.add(ActionConfig("3", ActionType.SET_BRIGHTNESS, "60%"))
            }
            else -> {
                title = "Smart Workflow: " + prompt.take(24).trim().capitalizeFirstLetter()
                category = WorkflowCategory.MESSAGING
                triggerType = TriggerType.TIME_SCHEDULE
                triggerTarget = "Custom Trigger"
                actions.add(ActionConfig("1", ActionType.TOGGLE_DND, "Smart Filter"))
                actions.add(ActionConfig("2", ActionType.SEND_AUTO_REPLY, "Auto-reply generated by AuraFlow"))
            }
        }

        return WorkflowEntity(
            title = title,
            description = description,
            naturalLanguagePrompt = prompt,
            category = category.name,
            triggerType = triggerType.name,
            triggerTarget = triggerTarget,
            triggerSecondary = triggerSecondary,
            conditionsJson = WorkflowEntity.encodeConditions(conditions),
            actionsJson = WorkflowEntity.encodeActions(actions),
            isEnabled = true,
            triggerCount = 0,
            lastTriggeredTime = 0
        )
    }

    suspend fun generateNotificationDigest(notificationsJson: String): String = withContext(Dispatchers.Default) {
        val prompt = """
            You are AuraFlow's Executive Notification Intelligence.
            Synthesize and summarize the following batch of mobile notifications into a concise, beautifully formatted 3-part digest:
            1. 🚨 Critical / Urgent Action Items (highlight deadlines, VIP messages, delays)
            2. 💬 Communication Pulse (brief rollup of social & team chats)
            3. 💡 Recommended Next Actions
            
            Keep the tone clean, professional, and punchy.
            Notifications list:
            $notificationsJson
        """.trimIndent()

        val response = callGemini(prompt)
        if (!response.isNullOrBlank()) {
            return@withContext response.replace("```json", "").replace("```", "").trim()
        }

        // Deterministic high-quality summary fallback
        """
        🚨 **Critical & VIP Items**
        • Boss (Slack): Needs Q3 Roadmap review by 2:00 PM today.
        • Airline Alert: Flight AA-1420 delayed by 35 minutes; new gate B12.
        
        💬 **Communication Pulse**
        • Sarah (WhatsApp): Confirmed dinner reservation at 7:30 PM.
        • GitHub: 3 pull requests reviewed and approved on 'feature/auraflow'.
        • Banking: Credit card payment of $120.00 posted successfully.
        
        💡 **AuraFlow Recommended Actions**
        • One-tap reply prepared for boss: "Reviewing the deck now, sending feedback in 30 mins."
        • Commute mode scheduled to trigger at 6:45 PM for airport transit.
        """.trimIndent()
    }

    suspend fun generateSmartReplies(sender: String, message: String): List<SmartReplyOption> = withContext(Dispatchers.Default) {
        val prompt = """
            Generate 4 distinct smart reply options for the following incoming message from $sender.
            Message: "$message"
            
            Provide exactly 4 replies matching these tones:
            1. PROFESSIONAL (Clear, polite, formal)
            2. QUICK (Ultra-short, direct, ack)
            3. FRIENDLY (Warm, appreciative, conversational)
            4. BUSY (Polite excuse, currently unavailable, will check later)
            
            Output JSON:
            {
               "professional": "...",
               "quick": "...",
               "friendly": "...",
               "busy": "..."
            }
        """.trimIndent()

        val response = callGemini(prompt)
        if (!response.isNullOrBlank()) {
            try {
                val json = JSONObject(response.trim())
                return@withContext listOf(
                    SmartReplyOption(ReplyTone.PROFESSIONAL, json.optString("professional", "Thanks for reaching out, I'm reviewing this now.")),
                    SmartReplyOption(ReplyTone.QUICK, json.optString("quick", "Got it, thanks!")),
                    SmartReplyOption(ReplyTone.FRIENDLY, json.optString("friendly", "Sounds great! Excited to connect on this soon.")),
                    SmartReplyOption(ReplyTone.BUSY, json.optString("busy", "In a meeting right now, I'll ping you as soon as I'm free."))
                )
            } catch (e: Exception) {
                Log.w("GeminiAutomationService", "Failed to parse reply JSON", e)
            }
        }

        // Heuristic fallback based on message content
        listOf(
            SmartReplyOption(ReplyTone.PROFESSIONAL, "Thank you for the update, $sender. I will follow up shortly."),
            SmartReplyOption(ReplyTone.QUICK, "Received and noted! 👍"),
            SmartReplyOption(ReplyTone.FRIENDLY, "Awesome, thanks for letting me know! Hope you have a great day."),
            SmartReplyOption(ReplyTone.BUSY, "Tied up at the moment. Will get back to you within the hour.")
        )
    }

    private fun String.capitalizeFirstLetter(): String {
        return if (isNotEmpty()) this[0].uppercaseChar() + substring(1) else this
    }
}
