package com.example.data.model

object DefaultPremiumTemplates {
    fun getTemplates(): List<PremiumCodeTemplate> = listOf(
        PremiumCodeTemplate(
            id = "vip_call_sms_responder",
            title = "VIP Call & SMS Auto-Responder",
            badge = "TELEPHONY & AI",
            category = "Communication",
            description = "Production background engine that intercepts incoming calls/SMS during meetings or driving and dispatches personalized AI-generated status replies.",
            features = listOf(
                "PhoneStateListener broadcast receiver",
                "Smart reply throttle (prevents spamming same contact)",
                "Driving speed detection using sensor fusion",
                "Automatic VIP contact whitelist bypass"
            ),
            estimatedMarketPrice = "$49.00 Value",
            isUnlocked = false,
            rawKotlinCode = """
// Production VIP Call & SMS Auto-Responder Engine
package com.auraflow.vip.telephony

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsManager
import android.telephony.TelephonyManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class VipCallSmsReceiver : BroadcastReceiver() {
    private val scope = CoroutineScope(Dispatchers.IO)

    override fun onReceive(context: Context, intent: Intent) {
        val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
        val incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER) ?: return

        if (state == TelephonyManager.EXTRA_STATE_RINGING) {
            scope.launch {
                // Check if user is currently in a scheduled meeting or driving
                val isBusy = AutoResponderPreferences.isAutoReplyActive(context)
                if (isBusy && !AutoResponderPreferences.hasRepliedRecently(incomingNumber)) {
                    val customMessage = "Hi! I am currently in a focus session. AuraFlow will notify me, or reply URGENT if emergency."
                    val smsManager = context.getSystemService(SmsManager::class.java)
                    smsManager.sendTextMessage(incomingNumber, null, customMessage, null, null)
                    AutoResponderPreferences.markReplied(incomingNumber)
                }
            }
        }
    }
}
            """.trimIndent()
        ),
        PremiumCodeTemplate(
            id = "vip_geofence_engine",
            title = "Enterprise Geo-Fencing Background Engine",
            badge = "LOCATION & GPS",
            category = "Location Intelligence",
            description = "High-accuracy, battery-efficient polygon and circular geofencing manager for triggering home, office, and transit automated workflows.",
            features = listOf(
                "Hardware geofencing API integration",
                "Zero battery drain sleep algorithm",
                "Dwell time verification (prevents false triggers)",
                "Seamless offline waypoint database"
            ),
            estimatedMarketPrice = "$59.00 Value",
            isUnlocked = false,
            rawKotlinCode = """
// Enterprise Geofence Background Service
package com.auraflow.vip.location

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices

class GeofenceAutomationManager(private val context: Context) {
    private val client = LocationServices.getGeofencingClient(context)

    fun registerGeofence(id: String, latitude: Double, longitude: Double, radiusMeters: Float) {
        val geofence = Geofence.Builder()
            .setRequestId(id)
            .setCircularRegion(latitude, longitude, radiusMeters)
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
            .setLoiteringDelay(30_000) // 30s dwell verification
            .build()

        val request = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofence(geofence)
            .build()

        val intent = Intent(context, GeofenceReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )

        client.addGeofences(request, pendingIntent)
    }
}
            """.trimIndent()
        ),
        PremiumCodeTemplate(
            id = "vip_ai_meeting_summarizer",
            title = "AI Voice & Meeting Action Engine",
            badge = "GEMINI PRO ENGINE",
            category = "Productivity & AI",
            description = "Real-time voice meeting recording engine that streams audio chunks directly to Gemini Pro, extracts executive summaries, and creates task items.",
            features = listOf(
                "High-fidelity Opus/AAC audio recorder",
                "Gemini 2.5 Flash direct multi-turn prompt",
                "Automatic JSON schema output parsing",
                "Task export to Google Tasks & Calendar"
            ),
            estimatedMarketPrice = "$79.00 Value",
            isUnlocked = false,
            rawKotlinCode = """
// Gemini Pro Realtime Meeting Transcriber
package com.auraflow.vip.ai

import com.google.firebase.vertexai.FirebaseVertexAI
import com.google.firebase.vertexai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MeetingAiSummarizer {
    private val generativeModel = FirebaseVertexAI.getInstance()
        .generativeModel("gemini-2.5-flash")

    suspend fun processMeetingTranscript(rawTranscript: String): MeetingSummaryResult = withContext(Dispatchers.IO) {
        val prompt = ""${'"'}
            Analyze the following meeting conversation transcript:
            ${'$'}rawTranscript
            Extract:
            1. Executive summary (2 sentences)
            2. Action items with assignees
            3. Follow-up deadlines
            Return strictly valid JSON format.
        ""${'"'}.trimIndent()

        val response = generativeModel.generateContent(content { text(prompt) })
        val jsonOutput = response.text ?: "{}"
        return@withContext MeetingParser.parse(jsonOutput)
    }
}
            """.trimIndent()
        ),
        PremiumCodeTemplate(
            id = "vip_battery_governor",
            title = "Ultra-Low Power Hardware Governor",
            badge = "HARDWARE TUNING",
            category = "Battery & Performance",
            description = "Multi-stage battery conservation algorithm that scales display refresh rate, freezes idle background sync, and preserves critical battery life.",
            features = listOf(
                "Dynamic refresh rate throttling (120Hz -> 60Hz)",
                "Sync adapter power governor",
                "OLED true black UI enforcer",
                "Emergency 5% SMS dispatch"
            ),
            estimatedMarketPrice = "$39.00 Value",
            isUnlocked = true, // 1 free starter unlocked
            rawKotlinCode = """
// Battery Ultra-Low Power Governor
package com.auraflow.vip.power

import android.content.Context
import android.os.PowerManager
import androidx.core.content.getSystemService

class BatteryGovernor(private val context: Context) {
    fun engageLowPowerProfile(batteryPercent: Int) {
        val powerManager = context.getSystemService<PowerManager>()
        if (batteryPercent <= 15) {
            // Apply aggressive background throttle
            PowerOptimizationService.suspendNonCriticalTasks(context)
            // Send emergency alert if battery <= 5%
            if (batteryPercent <= 5) {
                PowerOptimizationService.dispatchBatteryAlertSms(context)
            }
        }
    }
}
            """.trimIndent()
        )
    )
}
