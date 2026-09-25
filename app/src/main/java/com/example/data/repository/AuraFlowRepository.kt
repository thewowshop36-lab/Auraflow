package com.example.data.repository

import com.example.data.api.GeminiAutomationService
import com.example.data.local.AppDatabase
import com.example.data.local.ExecutionLogEntity
import com.example.data.local.NotificationEntity
import com.example.data.local.WorkflowEntity
import com.example.data.model.ActionConfig
import com.example.data.model.ActionType
import com.example.data.model.ConditionConfig
import com.example.data.model.ContextState
import com.example.data.model.NotificationPriority
import com.example.data.model.SmartReplyOption
import com.example.data.model.TriggerType
import com.example.data.model.WorkflowCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import org.json.JSONArray
import org.json.JSONObject

class AuraFlowRepository(
    private val database: AppDatabase,
    private val geminiService: GeminiAutomationService = GeminiAutomationService()
) {
    private val workflowDao = database.workflowDao()
    private val notificationDao = database.notificationDao()
    private val logDao = database.executionLogDao()

    val allWorkflows: Flow<List<WorkflowEntity>> = workflowDao.getAllWorkflows()
    val allNotifications: Flow<List<NotificationEntity>> = notificationDao.getAllNotifications()
    val allLogs: Flow<List<ExecutionLogEntity>> = logDao.getAllLogs()

    suspend fun initializeDefaultDataIfEmpty() {
        if (workflowDao.count() == 0) {
            val defaults = listOf(
                WorkflowEntity(
                    title = "Office Deep Focus",
                    description = "Silences incoming chatter, enables DND for VIP contacts only, and sets focus ringer mode when entering the office.",
                    naturalLanguagePrompt = "When I get to the office on weekdays, mute phone, enable DND for VIPs, and log my arrival",
                    category = WorkflowCategory.WORK.name,
                    triggerType = TriggerType.LOCATION.name,
                    triggerTarget = "Office / Tech Park",
                    triggerSecondary = "Geofence 150m",
                    conditionsJson = WorkflowEntity.encodeConditions(listOf(
                        ConditionConfig("Weekdays between 09:00 AM - 05:00 PM", true),
                        ConditionConfig("Wi-Fi connected to 'Corp-Office-5G'", true)
                    )),
                    actionsJson = WorkflowEntity.encodeActions(listOf(
                        ActionConfig("1", ActionType.MUTE_PHONE, "", true),
                        ActionConfig("2", ActionType.TOGGLE_DND, "Starred Contacts Only", true),
                        ActionConfig("3", ActionType.LOG_COMMUTE, "Work Arrival Recorded", true)
                    )),
                    isEnabled = true,
                    triggerCount = 18,
                    lastTriggeredTime = System.currentTimeMillis() - 86400000L
                ),
                WorkflowEntity(
                    title = "Gym Power Session",
                    description = "Mutes incoming calls, fires up Spotify high-energy workout mix, and auto-replies to incoming chats that you're working out.",
                    naturalLanguagePrompt = "When I enter the gym, mute all notifications, launch Spotify, and auto-reply to messages",
                    category = WorkflowCategory.WELLNESS.name,
                    triggerType = TriggerType.LOCATION.name,
                    triggerTarget = "Fitness Center / Gym",
                    triggerSecondary = "Radius 100m",
                    conditionsJson = WorkflowEntity.encodeConditions(listOf(
                        ConditionConfig("Only between 06:00 AM - 09:00 PM", true)
                    )),
                    actionsJson = WorkflowEntity.encodeActions(listOf(
                        ActionConfig("1", ActionType.MUTE_PHONE, "", true),
                        ActionConfig("2", ActionType.OPEN_APP, "Spotify", true),
                        ActionConfig("3", ActionType.SEND_AUTO_REPLY, "Crushing a workout right now! Will reply when finished.", true)
                    )),
                    isEnabled = true,
                    triggerCount = 8,
                    lastTriggeredTime = System.currentTimeMillis() - 172800000L
                ),
                WorkflowEntity(
                    title = "Late Night Battery Preserver",
                    description = "Dimmers screen to 15%, turns on ultra power saver mode, and mutes non-emergency phone calls when battery drops below 20%.",
                    naturalLanguagePrompt = "When battery drops below 20% after 9 PM, enable battery saver, dim screen to 15%, and turn on DND",
                    category = WorkflowCategory.BATTERY.name,
                    triggerType = TriggerType.BATTERY.name,
                    triggerTarget = "Battery < 20%",
                    triggerSecondary = "Unplugged",
                    conditionsJson = WorkflowEntity.encodeConditions(listOf(
                        ConditionConfig("After 09:00 PM", true),
                        ConditionConfig("Device is not actively charging", true)
                    )),
                    actionsJson = WorkflowEntity.encodeActions(listOf(
                        ActionConfig("1", ActionType.ENABLE_BATTERY_SAVER, "Level 2 Extreme", true),
                        ActionConfig("2", ActionType.SET_BRIGHTNESS, "15%", true),
                        ActionConfig("3", ActionType.TOGGLE_DND, "Priority Only", true)
                    )),
                    isEnabled = true,
                    triggerCount = 5,
                    lastTriggeredTime = System.currentTimeMillis() - 259200000L
                ),
                WorkflowEntity(
                    title = "Hands-Free Commute Mode",
                    description = "Launches Google Maps, announces incoming callers and urgent messages out loud, and sends auto-status ETA to family.",
                    naturalLanguagePrompt = "When connected to car bluetooth, open Google Maps, read notifications aloud, and text ETA",
                    category = WorkflowCategory.COMMUTE.name,
                    triggerType = TriggerType.BLUETOOTH.name,
                    triggerTarget = "Car Audio Bluetooth",
                    triggerSecondary = "Automotive Profile",
                    conditionsJson = WorkflowEntity.encodeConditions(listOf(
                        ConditionConfig("Moving faster than 15 mph or Car BT active", true)
                    )),
                    actionsJson = WorkflowEntity.encodeActions(listOf(
                        ActionConfig("1", ActionType.OPEN_APP, "Google Maps", true),
                        ActionConfig("2", ActionType.SPEAK_NOTIFICATION, "Voice Readout Active", true),
                        ActionConfig("3", ActionType.SEND_AUTO_REPLY, "Driving right now. ETA ~25 minutes. Talk soon!", true)
                    )),
                    isEnabled = true,
                    triggerCount = 14,
                    lastTriggeredTime = System.currentTimeMillis() - 43200000L
                ),
                WorkflowEntity(
                    title = "Aura Morning Briefing",
                    description = "Exits night-time DND, adjusts brightness for daybreak, and reads aloud the daily schedule and pending summaries.",
                    naturalLanguagePrompt = "Every weekday at 7:30 AM, turn off DND, set brightness to 65%, and speak my morning briefing",
                    category = WorkflowCategory.HOME.name,
                    triggerType = TriggerType.TIME_SCHEDULE.name,
                    triggerTarget = "07:30 AM Weekdays",
                    triggerSecondary = "Mon - Fri",
                    conditionsJson = WorkflowEntity.encodeConditions(listOf(
                        ConditionConfig("Device unplugged or alarm dismissed", true)
                    )),
                    actionsJson = WorkflowEntity.encodeActions(listOf(
                        ActionConfig("1", ActionType.TOGGLE_DND, "Standard Mode", true),
                        ActionConfig("2", ActionType.SET_BRIGHTNESS, "65%", true),
                        ActionConfig("3", ActionType.SPEAK_NOTIFICATION, "Good morning! You have 3 priority meetings today.", true)
                    )),
                    isEnabled = true,
                    triggerCount = 27,
                    lastTriggeredTime = System.currentTimeMillis() - 18000000L
                )
            )
            workflowDao.insertAll(defaults)
        }

        if (notificationDao.count() == 0) {
            val defaultNotifications = listOf(
                NotificationEntity(
                    appName = "Slack",
                    sender = "Jordan (VP Engineering)",
                    message = "Could you review the Q3 architecture RFC before our 2:00 PM leadership sync?",
                    timestamp = System.currentTimeMillis() - 1000 * 60 * 12,
                    priority = NotificationPriority.URGENT.name,
                    isRead = false,
                    isReplied = false
                ),
                NotificationEntity(
                    appName = "Delta Airlines",
                    sender = "Flight Status",
                    message = "Gate update: Flight DL-842 to SFO now boarding at Gate B24. Departs 18:45.",
                    timestamp = System.currentTimeMillis() - 1000 * 60 * 35,
                    priority = NotificationPriority.URGENT.name,
                    isRead = false,
                    isReplied = false
                ),
                NotificationEntity(
                    appName = "WhatsApp",
                    sender = "Mom",
                    message = "Hey sweetie! Are you still free for family dinner this Sunday at 7 PM?",
                    timestamp = System.currentTimeMillis() - 1000 * 60 * 75,
                    priority = NotificationPriority.SOCIAL.name,
                    isRead = false,
                    isReplied = false
                ),
                NotificationEntity(
                    appName = "GitHub",
                    sender = "Bot / Reviewer",
                    message = "[Merged] PR #340: Implement Gemini 3.5 Flash streaming pipeline to main branch.",
                    timestamp = System.currentTimeMillis() - 1000 * 60 * 140,
                    priority = NotificationPriority.WORK.name,
                    isRead = true,
                    isReplied = false
                ),
                NotificationEntity(
                    appName = "Chase Bank",
                    sender = "Security Alerts",
                    message = "Card ending in 4102 authorized $48.20 at Green Grocers. Reply NO if not recognized.",
                    timestamp = System.currentTimeMillis() - 1000 * 60 * 220,
                    priority = NotificationPriority.UPDATE.name,
                    isRead = true,
                    isReplied = false
                )
            )
            notificationDao.insertAll(defaultNotifications)
        }
    }

    suspend fun parseAndCreateWorkflowFromNl(prompt: String): WorkflowEntity {
        val workflow = geminiService.parseNaturalLanguageWorkflow(prompt)
        val id = workflowDao.insertWorkflow(workflow)
        return workflow.copy(id = id)
    }

    suspend fun saveWorkflow(workflow: WorkflowEntity): Long {
        return if (workflow.id == 0L) {
            workflowDao.insertWorkflow(workflow)
        } else {
            workflowDao.updateWorkflow(workflow)
            workflow.id
        }
    }

    suspend fun deleteWorkflow(workflow: WorkflowEntity) {
        workflowDao.deleteWorkflow(workflow)
    }

    suspend fun toggleWorkflow(id: Long, isEnabled: Boolean) {
        workflowDao.setWorkflowEnabled(id, isEnabled)
    }

    suspend fun recordExecution(id: Long) {
        workflowDao.recordExecution(id, System.currentTimeMillis())
    }

    suspend fun logExecution(
        workflowId: Long,
        workflowTitle: String,
        triggerReason: String,
        actionsExecuted: String,
        status: String = "SUCCESS"
    ) {
        logDao.insertLog(
            ExecutionLogEntity(
                workflowId = workflowId,
                workflowTitle = workflowTitle,
                triggerReason = triggerReason,
                actionsExecuted = actionsExecuted,
                timestamp = System.currentTimeMillis(),
                status = status
            )
        )
    }

    suspend fun clearLogs() {
        logDao.clearLogs()
    }

    suspend fun generateNotificationDigest(notifications: List<NotificationEntity>): String {
        val jsonArray = JSONArray()
        for (n in notifications) {
            val obj = JSONObject()
            obj.put("app", n.appName)
            obj.put("sender", n.sender)
            obj.put("message", n.message)
            obj.put("priority", n.priority)
            jsonArray.put(obj)
        }
        return geminiService.generateNotificationDigest(jsonArray.toString())
    }

    suspend fun generateSmartReplies(sender: String, message: String): List<SmartReplyOption> {
        return geminiService.generateSmartReplies(sender, message)
    }

    suspend fun sendReply(notificationId: Long, replyText: String) {
        notificationDao.markReplied(notificationId, replyText)
        logExecution(
            workflowId = 0L,
            workflowTitle = "Smart Reply Assistant",
            triggerReason = "One-tap AI Smart Reply sent",
            actionsExecuted = "Replied: \"$replyText\"",
            status = "SUCCESS"
        )
    }

    suspend fun simulateIncomingNotification(
        appName: String,
        sender: String,
        message: String,
        priority: NotificationPriority
    ) {
        notificationDao.insertNotification(
            NotificationEntity(
                appName = appName,
                sender = sender,
                message = message,
                priority = priority.name,
                timestamp = System.currentTimeMillis(),
                isRead = false,
                isReplied = false
            )
        )
    }

    suspend fun clearAllNotifications() {
        notificationDao.clearAllNotifications()
    }

    suspend fun checkAndTriggerWorkflowsForContext(
        context: ContextState,
        workflows: List<WorkflowEntity>
    ): List<WorkflowEntity> {
        val triggered = mutableListOf<WorkflowEntity>()
        for (wf in workflows) {
            if (!wf.isEnabled) continue
            val trigger = wf.toTriggerConfig()
            var matched = false

            when (trigger.type) {
                TriggerType.LOCATION -> {
                    if (context.locationName.contains(trigger.targetValue, ignoreCase = true) ||
                        trigger.targetValue.contains(context.locationName, ignoreCase = true)
                    ) {
                        matched = true
                    }
                }
                TriggerType.BATTERY -> {
                    if (context.batteryLevel <= 20 && trigger.targetValue.contains("20", ignoreCase = true)) {
                        matched = true
                    }
                }
                TriggerType.BLUETOOTH -> {
                    if (context.isBluetoothCarConnected && trigger.targetValue.contains("car", ignoreCase = true)) {
                        matched = true
                    }
                }
                TriggerType.TIME_SCHEDULE -> {
                    if (context.timeOfDay.contains(trigger.targetValue.take(5), ignoreCase = true)) {
                        matched = true
                    }
                }
                else -> {}
            }

            if (matched) {
                recordExecution(wf.id)
                val actionList = wf.getActions().filter { it.isEnabled }
                val actionsSummary = actionList.joinToString(" ➔ ") { it.type.displayName }
                logExecution(
                    workflowId = wf.id,
                    workflowTitle = wf.title,
                    triggerReason = "Context matched: ${context.locationName} | ${context.batteryLevel}% battery",
                    actionsExecuted = actionsSummary,
                    status = "SUCCESS"
                )
                triggered.add(wf)
            }
        }
        return triggered
    }
}
