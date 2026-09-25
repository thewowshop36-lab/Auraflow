package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.ExecutionLogEntity
import com.example.data.local.NotificationEntity
import com.example.data.local.WorkflowEntity
import com.example.data.model.ContextState
import com.example.data.model.NotificationPriority
import com.example.data.model.SmartReplyOption
import com.example.data.repository.AuraFlowRepository
import com.example.data.model.AdEarningsStats
import com.example.data.model.AdMobConfig
import com.example.data.model.DefaultPremiumTemplates
import com.example.data.model.PremiumCodeTemplate
import com.example.data.model.UserSubscriptionState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppTab(val title: String) {
    STUDIO("Studio"),
    BUILDER("AI Builder"),
    NOTIFICATIONS("Alerts"),
    VIP_PREMIUM("VIP Pro"),
    MONETIZATION("Earnings & Ads"),
    WEB_APP("Vercel Web"),
    LOGS("History & Context")
}

class AuraFlowViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AuraFlowRepository(AppDatabase.getInstance(application))

    val workflows: StateFlow<List<WorkflowEntity>> = repository.allWorkflows
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notifications: StateFlow<List<NotificationEntity>> = repository.allNotifications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val logs: StateFlow<List<ExecutionLogEntity>> = repository.allLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _currentTab = MutableStateFlow(AppTab.STUDIO)
    val currentTab: StateFlow<AppTab> = _currentTab.asStateFlow()

    private val _contextState = MutableStateFlow(ContextState())
    val contextState: StateFlow<ContextState> = _contextState.asStateFlow()

    // Simulation Runner State
    private val _isSimulatingRun = MutableStateFlow(false)
    val isSimulatingRun: StateFlow<Boolean> = _isSimulatingRun.asStateFlow()

    private val _simulatedWorkflow = MutableStateFlow<WorkflowEntity?>(null)
    val simulatedWorkflow: StateFlow<WorkflowEntity?> = _simulatedWorkflow.asStateFlow()

    private val _simulatedStep = MutableStateFlow(0)
    val simulatedStep: StateFlow<Int> = _simulatedStep.asStateFlow()

    private val _simulationStepsText = MutableStateFlow<List<String>>(emptyList())
    val simulationStepsText: StateFlow<List<String>> = _simulationStepsText.asStateFlow()

    // Natural Language Builder State
    private val _isGeneratingWorkflow = MutableStateFlow(false)
    val isGeneratingWorkflow: StateFlow<Boolean> = _isGeneratingWorkflow.asStateFlow()

    private val _draftWorkflow = MutableStateFlow<WorkflowEntity?>(null)
    val draftWorkflow: StateFlow<WorkflowEntity?> = _draftWorkflow.asStateFlow()

    // Notification Intelligence State
    private val _isGeneratingDigest = MutableStateFlow(false)
    val isGeneratingDigest: StateFlow<Boolean> = _isGeneratingDigest.asStateFlow()

    private val _notificationDigest = MutableStateFlow<String?>(null)
    val notificationDigest: StateFlow<String?> = _notificationDigest.asStateFlow()

    private val _selectedNotificationForReply = MutableStateFlow<NotificationEntity?>(null)
    val selectedNotificationForReply: StateFlow<NotificationEntity?> = _selectedNotificationForReply.asStateFlow()

    private val _smartReplies = MutableStateFlow<List<SmartReplyOption>>(emptyList())
    val smartReplies: StateFlow<List<SmartReplyOption>> = _smartReplies.asStateFlow()

    private val _isLoadingReplies = MutableStateFlow(false)
    val isLoadingReplies: StateFlow<Boolean> = _isLoadingReplies.asStateFlow()

    // Snack / Feedback message
    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    // Detail View modal
    private val _inspectedWorkflow = MutableStateFlow<WorkflowEntity?>(null)
    val inspectedWorkflow: StateFlow<WorkflowEntity?> = _inspectedWorkflow.asStateFlow()

    // Monetization & Google Ads State
    private val _adMobConfig = MutableStateFlow(AdMobConfig())
    val adMobConfig: StateFlow<AdMobConfig> = _adMobConfig.asStateFlow()

    private val _earningsStats = MutableStateFlow(AdEarningsStats())
    val earningsStats: StateFlow<AdEarningsStats> = _earningsStats.asStateFlow()

    private val _subscriptionState = MutableStateFlow(UserSubscriptionState())
    val subscriptionState: StateFlow<UserSubscriptionState> = _subscriptionState.asStateFlow()

    private val _premiumTemplates = MutableStateFlow(DefaultPremiumTemplates.getTemplates())
    val premiumTemplates: StateFlow<List<PremiumCodeTemplate>> = _premiumTemplates.asStateFlow()

    private val _isShowingInterstitial = MutableStateFlow(false)
    val isShowingInterstitial: StateFlow<Boolean> = _isShowingInterstitial.asStateFlow()

    private val _isShowingRewarded = MutableStateFlow(false)
    val isShowingRewarded: StateFlow<Boolean> = _isShowingRewarded.asStateFlow()

    private val _rewardedAdReason = MutableStateFlow("+5 Free AI Credits")
    val rewardedAdReason: StateFlow<String> = _rewardedAdReason.asStateFlow()

    private var pendingTemplateUnlockId: String? = null

    init {
        viewModelScope.launch {
            repository.initializeDefaultDataIfEmpty()
        }
    }

    fun setTab(tab: AppTab) {
        _currentTab.value = tab
    }

    fun inspectWorkflow(workflow: WorkflowEntity?) {
        _inspectedWorkflow.value = workflow
    }

    fun updateContext(newContext: ContextState) {
        _contextState.value = newContext
        viewModelScope.launch {
            val triggered = repository.checkAndTriggerWorkflowsForContext(newContext, workflows.value)
            if (triggered.isNotEmpty()) {
                val names = triggered.joinToString(", ") { it.title }
                _userMessage.value = "⚡ Triggered context automations: $names"
            }
        }
    }

    fun parseNlWorkflow(prompt: String) {
        if (prompt.isBlank()) return
        viewModelScope.launch {
            _isGeneratingWorkflow.value = true
            try {
                val parsed = repository.parseAndCreateWorkflowFromNl(prompt)
                _draftWorkflow.value = parsed
                _userMessage.value = "✨ AI synthesized new workflow: ${parsed.title}"
            } catch (e: Exception) {
                _userMessage.value = "Could not parse workflow: ${e.message}"
            } finally {
                _isGeneratingWorkflow.value = false
            }
        }
    }

    fun saveDraftWorkflow(workflow: WorkflowEntity) {
        viewModelScope.launch {
            repository.saveWorkflow(workflow)
            _draftWorkflow.value = null
            _userMessage.value = "Saved '${workflow.title}' to Automation Studio"
            _currentTab.value = AppTab.STUDIO
        }
    }

    fun dismissDraftWorkflow() {
        viewModelScope.launch {
            _draftWorkflow.value?.let { repository.deleteWorkflow(it) }
            _draftWorkflow.value = null
        }
    }

    fun toggleWorkflow(workflow: WorkflowEntity, isEnabled: Boolean) {
        viewModelScope.launch {
            repository.toggleWorkflow(workflow.id, isEnabled)
            _userMessage.value = if (isEnabled) "'${workflow.title}' activated" else "'${workflow.title}' paused"
        }
    }

    fun deleteWorkflow(workflow: WorkflowEntity) {
        viewModelScope.launch {
            repository.deleteWorkflow(workflow)
            if (_inspectedWorkflow.value?.id == workflow.id) {
                _inspectedWorkflow.value = null
            }
            _userMessage.value = "Deleted '${workflow.title}'"
        }
    }

    fun runWorkflowSimulation(workflow: WorkflowEntity) {
        viewModelScope.launch {
            _isSimulatingRun.value = true
            _simulatedWorkflow.value = workflow
            _simulatedStep.value = 0
            val steps = mutableListOf<String>()

            steps.add("⚡ Evaluating Trigger: [${workflow.triggerType}] ${workflow.triggerTarget}")
            _simulationStepsText.value = steps.toList()
            _simulatedStep.value = 1
            delay(700)

            val conditions = workflow.getConditions()
            if (conditions.isNotEmpty()) {
                steps.add("🔍 Checking Conditions: ${conditions.joinToString(" AND ") { it.description }} — [PASSED]")
                _simulationStepsText.value = steps.toList()
                _simulatedStep.value = 2
                delay(700)
            } else {
                steps.add("🔍 No blocking conditions — [IMMEDIATE EXECUTE]")
                _simulationStepsText.value = steps.toList()
                _simulatedStep.value = 2
                delay(500)
            }

            val actions = workflow.getActions().filter { it.isEnabled }
            for ((index, action) in actions.withIndex()) {
                val paramStr = if (action.parameter.isNotBlank()) " (${action.parameter})" else ""
                steps.add("🚀 Executing Action ${index + 1}/${actions.size}: ${action.type.displayName}$paramStr")
                _simulationStepsText.value = steps.toList()
                _simulatedStep.value = 3 + index
                delay(600)
            }

            steps.add("✅ Automation Completed Successfully in 1.4s")
            _simulationStepsText.value = steps.toList()
            _simulatedStep.value = 100
            repository.recordExecution(workflow.id)
            repository.logExecution(
                workflowId = workflow.id,
                workflowTitle = workflow.title,
                triggerReason = "Manual Live Test Run",
                actionsExecuted = actions.joinToString(" ➔ ") { it.type.displayName },
                status = "SUCCESS"
            )
            delay(1200)
            _isSimulatingRun.value = false
        }
    }

    fun cancelSimulation() {
        _isSimulatingRun.value = false
        _simulatedWorkflow.value = null
    }

    fun generateNotificationDigest() {
        viewModelScope.launch {
            _isGeneratingDigest.value = true
            try {
                val digest = repository.generateNotificationDigest(notifications.value)
                _notificationDigest.value = digest
            } catch (e: Exception) {
                _userMessage.value = "Failed to create digest: ${e.message}"
            } finally {
                _isGeneratingDigest.value = false
            }
        }
    }

    fun dismissDigest() {
        _notificationDigest.value = null
    }

    fun selectNotificationForReply(notification: NotificationEntity?) {
        _selectedNotificationForReply.value = notification
        if (notification == null) {
            _smartReplies.value = emptyList()
            return
        }
        viewModelScope.launch {
            _isLoadingReplies.value = true
            try {
                val replies = repository.generateSmartReplies(notification.sender, notification.message)
                _smartReplies.value = replies
            } catch (_: Exception) {
                _smartReplies.value = emptyList()
            } finally {
                _isLoadingReplies.value = false
            }
        }
    }

    fun sendSmartReply(notificationId: Long, replyText: String) {
        viewModelScope.launch {
            repository.sendReply(notificationId, replyText)
            _selectedNotificationForReply.value = null
            _smartReplies.value = emptyList()
            _userMessage.value = "Sent smart reply: \"$replyText\""
        }
    }

    fun simulateNewNotification(appName: String, sender: String, message: String, priority: NotificationPriority) {
        viewModelScope.launch {
            repository.simulateIncomingNotification(appName, sender, message, priority)
            _userMessage.value = "Simulated notification from $sender"
        }
    }

    fun clearAllNotifications() {
        viewModelScope.launch {
            repository.clearAllNotifications()
            _notificationDigest.value = null
            _userMessage.value = "Cleared all notifications"
        }
    }

    fun clearLogs() {
        viewModelScope.launch {
            repository.clearLogs()
            _userMessage.value = "Execution history cleared"
        }
    }

    fun dismissMessage() {
        _userMessage.value = null
    }

    // Monetization & Ad Operations
    fun updateAdMobConfig(newConfig: AdMobConfig) {
        _adMobConfig.value = newConfig
        _userMessage.value = "Google AdMob & AdSense configuration saved!"
    }

    fun recordAdImpression(adType: String, isClick: Boolean = false) {
        val current = _earningsStats.value
        val adEarnIncrement = when (adType) {
            "BANNER" -> if (isClick) 0.08 else 0.003
            "INTERSTITIAL" -> if (isClick) 0.15 else 0.025
            "REWARDED" -> if (isClick) 0.35 else 0.05
            else -> 0.002
        }

        _earningsStats.value = current.copy(
            totalImpressions = current.totalImpressions + 1,
            bannerImpressions = if (adType == "BANNER") current.bannerImpressions + 1 else current.bannerImpressions,
            interstitialImpressions = if (adType == "INTERSTITIAL") current.interstitialImpressions + 1 else current.interstitialImpressions,
            rewardedImpressions = if (adType == "REWARDED") current.rewardedImpressions + 1 else current.rewardedImpressions,
            totalClicks = if (isClick) current.totalClicks + 1 else current.totalClicks,
            estimatedAdRevenue = current.estimatedAdRevenue + adEarnIncrement
        )
    }

    fun triggerInterstitialAd() {
        if (!_adMobConfig.value.isAdsEnabled || _subscriptionState.value.isVipPro) return
        _isShowingInterstitial.value = true
        recordAdImpression("INTERSTITIAL", isClick = false)
    }

    fun dismissInterstitialAd() {
        _isShowingInterstitial.value = false
    }

    fun onInterstitialClicked() {
        recordAdImpression("INTERSTITIAL", isClick = true)
        _userMessage.value = "Ad clicked: +$0.15 Publisher revenue recorded"
    }

    fun triggerRewardedAd(reason: String = "+5 Free AI Credits", templateId: String? = null) {
        _rewardedAdReason.value = reason
        pendingTemplateUnlockId = templateId
        _isShowingRewarded.value = true
        recordAdImpression("REWARDED", isClick = false)
    }

    fun dismissRewardedAd() {
        _isShowingRewarded.value = false
        pendingTemplateUnlockId = null
    }

    fun onRewardedGranted() {
        val targetId = pendingTemplateUnlockId
        if (targetId != null) {
            _premiumTemplates.value = _premiumTemplates.value.map {
                if (it.id == targetId) it.copy(isUnlocked = true) else it
            }
            _userMessage.value = "🎉 Reward granted: Template unlocked for 24h!"
        } else {
            _subscriptionState.value = _subscriptionState.value.copy(
                aiCreditsRemaining = _subscriptionState.value.aiCreditsRemaining + 5
            )
            _userMessage.value = "🎉 Reward granted: +5 AI Credits added!"
        }
    }

    fun simulateBatchImpressions(count: Int) {
        val current = _earningsStats.value
        val addedRevenue = count * 0.02
        _earningsStats.value = current.copy(
            totalImpressions = current.totalImpressions + count,
            bannerImpressions = current.bannerImpressions + (count * 0.8).toInt(),
            interstitialImpressions = current.interstitialImpressions + (count * 0.15).toInt(),
            rewardedImpressions = current.rewardedImpressions + (count * 0.05).toInt(),
            totalClicks = current.totalClicks + (count * 0.04).toInt(),
            estimatedAdRevenue = current.estimatedAdRevenue + addedRevenue
        )
        _userMessage.value = "Simulated $count impressions: +$${String.format(java.util.Locale.US, "%.2f", addedRevenue)} revenue"
    }

    fun resetEarningsStats() {
        _earningsStats.value = AdEarningsStats(0, 0, 0, 0, 0, 0.0, 0.0)
        _userMessage.value = "Earnings reset"
    }

    fun subscribePlan(planName: String, price: Double) {
        _subscriptionState.value = UserSubscriptionState(
            isVipPro = true,
            planType = planName,
            aiCreditsRemaining = 9999,
            unlockedTemplatesCount = _premiumTemplates.value.size,
            expiryDate = "Next Billing Cycle (Auto-Renew)"
        )
        val current = _earningsStats.value
        _earningsStats.value = current.copy(
            subscriptionRevenue = current.subscriptionRevenue + price
        )
        _userMessage.value = "🎉 Subscribed to $planName! +$${String.format(java.util.Locale.US, "%.2f", price)} credited to publisher revenue."
    }

    fun redeemLicenseCode(code: String): Boolean {
        val validCodes = setOf("AURA-VIP-2026", "PRO-GOLD-99", "EARN-VIP-PASS", "GUMROAD-VIP")
        if (validCodes.contains(code.trim().uppercase())) {
            _subscriptionState.value = UserSubscriptionState(
                isVipPro = true,
                planType = "Lifetime Founder (Redeemed)",
                aiCreditsRemaining = 9999,
                unlockedTemplatesCount = _premiumTemplates.value.size,
                expiryDate = "Lifetime Active"
            )
            val current = _earningsStats.value
            _earningsStats.value = current.copy(
                subscriptionRevenue = current.subscriptionRevenue + 79.99
            )
            _userMessage.value = "🎉 VIP Code redeemed! All Kotlin modules unlocked."
            return true
        }
        return false
    }

    fun unlockTemplateWithAd(templateId: String) {
        val template = _premiumTemplates.value.find { it.id == templateId }
        val name = template?.title ?: "VIP Template"
        triggerRewardedAd("Unlock '$name'", templateId)
    }
}
