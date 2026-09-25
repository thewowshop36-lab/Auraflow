package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.ui.components.SimulationDialog
import com.example.ui.components.ads.AdMobBannerView
import com.example.ui.components.ads.InterstitialAdDialog
import com.example.ui.components.ads.RewardedVideoDialog
import com.example.ui.screens.builder.NlBuilderScreen
import com.example.ui.screens.logs.LogsScreen
import com.example.ui.screens.monetization.MonetizationScreen
import com.example.ui.screens.notifications.NotificationsScreen
import com.example.ui.screens.premium.PremiumVipScreen
import com.example.ui.screens.web.WebHubScreen
import com.example.ui.screens.studio.StudioScreen
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: AuraFlowViewModel) {
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val workflows by viewModel.workflows.collectAsStateWithLifecycle()
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()
    val logs by viewModel.logs.collectAsStateWithLifecycle()
    val contextState by viewModel.contextState.collectAsStateWithLifecycle()

    // Simulation state
    val isSimulatingRun by viewModel.isSimulatingRun.collectAsStateWithLifecycle()
    val simulatedWorkflow by viewModel.simulatedWorkflow.collectAsStateWithLifecycle()
    val simulatedStep by viewModel.simulatedStep.collectAsStateWithLifecycle()
    val simulationStepsText by viewModel.simulationStepsText.collectAsStateWithLifecycle()

    // Builder state
    val isGeneratingWorkflow by viewModel.isGeneratingWorkflow.collectAsStateWithLifecycle()
    val draftWorkflow by viewModel.draftWorkflow.collectAsStateWithLifecycle()

    // Notification state
    val isGeneratingDigest by viewModel.isGeneratingDigest.collectAsStateWithLifecycle()
    val notificationDigest by viewModel.notificationDigest.collectAsStateWithLifecycle()
    val selectedForReply by viewModel.selectedNotificationForReply.collectAsStateWithLifecycle()
    val smartReplies by viewModel.smartReplies.collectAsStateWithLifecycle()
    val isLoadingReplies by viewModel.isLoadingReplies.collectAsStateWithLifecycle()

    // Monetization & Ads state
    val adMobConfig by viewModel.adMobConfig.collectAsStateWithLifecycle()
    val earningsStats by viewModel.earningsStats.collectAsStateWithLifecycle()
    val subscriptionState by viewModel.subscriptionState.collectAsStateWithLifecycle()
    val premiumTemplates by viewModel.premiumTemplates.collectAsStateWithLifecycle()
    val isShowingInterstitial by viewModel.isShowingInterstitial.collectAsStateWithLifecycle()
    val isShowingRewarded by viewModel.isShowingRewarded.collectAsStateWithLifecycle()
    val rewardedAdReason by viewModel.rewardedAdReason.collectAsStateWithLifecycle()

    // Snack messages
    val userMessage by viewModel.userMessage.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(userMessage) {
        userMessage?.let { msg ->
            snackbarHostState.showSnackbar(
                message = msg,
                duration = SnackbarDuration.Short
            )
            viewModel.dismissMessage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_auraflow_logo),
                            contentDescription = "AuraFlow Logo",
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "AuraFlow",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = (-0.5).sp
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = "AI STUDIO",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                                        fontSize = 8.sp
                                    )
                                }
                            }
                            Text(
                                text = "Automate & Earn with Google Ads",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 10.sp,
                                maxLines = 1
                            )
                        }
                    }
                },
                actions = {
                    // Earnings Pill (Opens Monetization Screen)
                    Surface(
                        onClick = { viewModel.setTab(AppTab.MONETIZATION) },
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF059669),
                        modifier = Modifier
                            .padding(end = 4.dp)
                            .testTag("topbar_earnings_pill")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.MonetizationOn,
                                contentDescription = "Earnings",
                                tint = Color.White,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = String.format(Locale.US, "$%.2f", earningsStats.totalEarnings),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                fontSize = 11.sp
                            )
                        }
                    }

                    // VIP Pill (Opens Premium Screen)
                    Surface(
                        onClick = { viewModel.setTab(AppTab.VIP_PREMIUM) },
                        shape = RoundedCornerShape(12.dp),
                        color = if (subscriptionState.isVipPro) Color(0xFF7C3AED) else MaterialTheme.colorScheme.surfaceVariant,
                        border = if (subscriptionState.isVipPro) null else androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF59E0B)),
                        modifier = Modifier
                            .padding(end = 4.dp)
                            .testTag("topbar_vip_pill")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "VIP",
                                tint = if (subscriptionState.isVipPro) Color(0xFFFDE047) else Color(0xFFF59E0B),
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = if (subscriptionState.isVipPro) "VIP" else "PRO",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (subscriptionState.isVipPro) Color.White else Color(0xFFF59E0B),
                                fontSize = 11.sp
                            )
                        }
                    }

                    // Vercel Web Pill (Opens Mobile Web Hub)
                    Surface(
                        onClick = { viewModel.setTab(AppTab.WEB_APP) },
                        shape = RoundedCornerShape(12.dp),
                        color = if (currentTab == AppTab.WEB_APP) MaterialTheme.colorScheme.primary else Color(0xFF0F172A),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (currentTab == AppTab.WEB_APP) Color.Transparent else Color(0xFF8B5CF6).copy(alpha = 0.5f)
                        ),
                        modifier = Modifier
                            .padding(end = 4.dp)
                            .testTag("topbar_vercel_web_pill")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "▲",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "Web",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 11.sp
                            )
                        }
                    }

                    // History & Context Button
                    IconButton(
                        onClick = { viewModel.setTab(AppTab.LOGS) },
                        modifier = Modifier
                            .padding(end = 4.dp)
                            .size(32.dp)
                            .testTag("topbar_history_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "History & Context",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp
            ) {
                // Tab 1: Studio
                NavigationBarItem(
                    selected = currentTab == AppTab.STUDIO,
                    onClick = { viewModel.setTab(AppTab.STUDIO) },
                    icon = {
                        Icon(Icons.Default.Tune, contentDescription = "Studio")
                    },
                    label = { Text("Studio", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.testTag("nav_tab_studio")
                )

                // Tab 2: AI Builder
                NavigationBarItem(
                    selected = currentTab == AppTab.BUILDER,
                    onClick = { viewModel.setTab(AppTab.BUILDER) },
                    icon = {
                        Icon(Icons.Default.AutoAwesome, contentDescription = "AI Builder")
                    },
                    label = { Text("Builder", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.testTag("nav_tab_builder")
                )

                // Tab 3: Notifications / Alerts
                val unrepliedCount = notifications.count { !it.isReplied }
                NavigationBarItem(
                    selected = currentTab == AppTab.NOTIFICATIONS,
                    onClick = { viewModel.setTab(AppTab.NOTIFICATIONS) },
                    icon = {
                        if (unrepliedCount > 0) {
                            BadgedBox(
                                badge = {
                                    Badge(
                                        containerColor = MaterialTheme.colorScheme.primary
                                    ) {
                                        Text("$unrepliedCount")
                                    }
                                }
                            ) {
                                Icon(Icons.Default.Notifications, contentDescription = "Alerts")
                            }
                        } else {
                            Icon(Icons.Default.Notifications, contentDescription = "Alerts")
                        }
                    },
                    label = { Text("Alerts", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.testTag("nav_tab_notifications")
                )

                // Tab 4: VIP Pro & Code Templates
                NavigationBarItem(
                    selected = currentTab == AppTab.VIP_PREMIUM,
                    onClick = { viewModel.setTab(AppTab.VIP_PREMIUM) },
                    icon = {
                        Icon(Icons.Default.WorkspacePremium, contentDescription = "VIP Pro")
                    },
                    label = { Text("VIP Pro", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF7C3AED),
                        selectedTextColor = Color(0xFF7C3AED),
                        indicatorColor = Color(0xFF7C3AED).copy(alpha = 0.2f)
                    ),
                    modifier = Modifier.testTag("nav_tab_vip_premium")
                )

                // Tab 5: Google Ads & Monetization
                NavigationBarItem(
                    selected = currentTab == AppTab.MONETIZATION,
                    onClick = { viewModel.setTab(AppTab.MONETIZATION) },
                    icon = {
                        Icon(Icons.Default.MonetizationOn, contentDescription = "Earnings & Ads")
                    },
                    label = { Text("Earn $$$", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF059669),
                        selectedTextColor = Color(0xFF059669),
                        indicatorColor = Color(0xFF059669).copy(alpha = 0.2f)
                    ),
                    modifier = Modifier.testTag("nav_tab_monetization")
                )
            }
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Main Screen Body
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                when (currentTab) {
                    AppTab.STUDIO -> {
                        StudioScreen(
                            workflows = workflows,
                            contextState = contextState,
                            onContextChanged = { viewModel.updateContext(it) },
                            onToggleWorkflow = { wf, enabled -> viewModel.toggleWorkflow(wf, enabled) },
                            onRunSimulation = { wf ->
                                viewModel.runWorkflowSimulation(wf)
                                // Trigger interstitial ad after run
                                viewModel.triggerInterstitialAd()
                            },
                            onDeleteWorkflow = { wf -> viewModel.deleteWorkflow(wf) },
                            onInspectWorkflow = { wf -> viewModel.inspectWorkflow(wf) },
                            onOpenNlBuilder = { viewModel.setTab(AppTab.BUILDER) }
                        )
                    }

                    AppTab.BUILDER -> {
                        NlBuilderScreen(
                            isGenerating = isGeneratingWorkflow,
                            draftWorkflow = draftWorkflow,
                            onGenerate = { prompt -> viewModel.parseNlWorkflow(prompt) },
                            onSaveDraft = { wf ->
                                viewModel.saveDraftWorkflow(wf)
                                viewModel.triggerInterstitialAd()
                            },
                            onDiscardDraft = { viewModel.dismissDraftWorkflow() }
                        )
                    }

                    AppTab.NOTIFICATIONS -> {
                        NotificationsScreen(
                            notifications = notifications,
                            isGeneratingDigest = isGeneratingDigest,
                            notificationDigest = notificationDigest,
                            selectedForReply = selectedForReply,
                            smartReplies = smartReplies,
                            isLoadingReplies = isLoadingReplies,
                            onGenerateDigest = { viewModel.generateNotificationDigest() },
                            onDismissDigest = { viewModel.dismissDigest() },
                            onSelectForReply = { wf -> viewModel.selectNotificationForReply(wf) },
                            onSendReply = { id, reply -> viewModel.sendSmartReply(id, reply) },
                            onSimulateNotification = { app, sender, msg, priority ->
                                viewModel.simulateNewNotification(app, sender, msg, priority)
                            },
                            onClearNotifications = { viewModel.clearAllNotifications() }
                        )
                    }

                    AppTab.VIP_PREMIUM -> {
                        PremiumVipScreen(
                            subscriptionState = subscriptionState,
                            templates = premiumTemplates,
                            onSubscribePlan = { plan, price -> viewModel.subscribePlan(plan, price) },
                            onRedeemCode = { code -> viewModel.redeemLicenseCode(code) },
                            onUnlockTemplateWithAd = { templateId -> viewModel.unlockTemplateWithAd(templateId) }
                        )
                    }

                    AppTab.MONETIZATION -> {
                        MonetizationScreen(
                            adMobConfig = adMobConfig,
                            earningsStats = earningsStats,
                            onUpdateConfig = { newConfig -> viewModel.updateAdMobConfig(newConfig) },
                            onTriggerInterstitial = { viewModel.triggerInterstitialAd() },
                            onTriggerRewarded = { viewModel.triggerRewardedAd("+5 Free AI Credits") },
                            onSimulateBatchImpressions = { count -> viewModel.simulateBatchImpressions(count) },
                            onResetStats = { viewModel.resetEarningsStats() }
                        )
                    }

                    AppTab.WEB_APP -> {
                        WebHubScreen(
                            onNavigateBack = { viewModel.setTab(AppTab.STUDIO) }
                        )
                    }

                    AppTab.LOGS -> {
                        LogsScreen(
                            logs = logs,
                            contextState = contextState,
                            onContextChanged = { viewModel.updateContext(it) },
                            onClearLogs = { viewModel.clearLogs() }
                        )
                    }
                }

                // Live Simulation Dialog
                if (isSimulatingRun) {
                    SimulationDialog(
                        workflow = simulatedWorkflow,
                        steps = simulationStepsText,
                        currentStep = simulatedStep,
                        onDismiss = { viewModel.cancelSimulation() }
                    )
                }

                // Interstitial Ad Dialog
                if (isShowingInterstitial) {
                    InterstitialAdDialog(
                        adMobConfig = adMobConfig,
                        onDismiss = { viewModel.dismissInterstitialAd() },
                        onAdClick = { viewModel.onInterstitialClicked() }
                    )
                }

                // Rewarded Video Ad Dialog
                if (isShowingRewarded) {
                    RewardedVideoDialog(
                        adMobConfig = adMobConfig,
                        rewardDescription = rewardedAdReason,
                        onDismiss = { viewModel.dismissRewardedAd() },
                        onRewardEarned = { viewModel.onRewardedGranted() }
                    )
                }
            }

            // Google AdMob / AdSense Banner View (Appears on non-VIP mode)
            AdMobBannerView(
                adMobConfig = adMobConfig,
                isVipUser = subscriptionState.isVipPro,
                onAdImpression = { viewModel.recordAdImpression("BANNER") },
                onAdClick = { viewModel.recordAdImpression("BANNER", isClick = true) },
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
            )
        }
    }
}
