package com.example.ui.screens.monetization

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AdsClick
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.ViewCarousel
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AdEarningsStats
import com.example.data.model.AdMobConfig
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MonetizationScreen(
    adMobConfig: AdMobConfig,
    earningsStats: AdEarningsStats,
    onUpdateConfig: (AdMobConfig) -> Unit,
    onTriggerInterstitial: () -> Unit,
    onTriggerRewarded: () -> Unit,
    onSimulateBatchImpressions: (count: Int) -> Unit,
    onResetStats: () -> Unit
) {
    var publisherId by remember(adMobConfig) { mutableStateOf(adMobConfig.publisherId) }
    var adMobAppId by remember(adMobConfig) { mutableStateOf(adMobConfig.adMobAppId) }
    var bannerUnitId by remember(adMobConfig) { mutableStateOf(adMobConfig.bannerAdUnitId) }
    var interstitialUnitId by remember(adMobConfig) { mutableStateOf(adMobConfig.interstitialAdUnitId) }
    var rewardedUnitId by remember(adMobConfig) { mutableStateOf(adMobConfig.rewardedAdUnitId) }
    var isTestMode by remember(adMobConfig) { mutableStateOf(adMobConfig.isTestMode) }
    var isAdsEnabled by remember(adMobConfig) { mutableStateOf(adMobConfig.isAdsEnabled) }

    var estimatedDau by remember { mutableFloatStateOf(5000f) }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
            .testTag("monetization_screen")
    ) {
        // Hero Card: Live Total Revenue & Earnings
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("total_earnings_hero_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFF064E3B), Color(0xFF047857), Color(0xFF10B981))
                        )
                    )
                    .padding(20.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = Color.White.copy(alpha = 0.2f),
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.MonetizationOn,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Google Ads & AdSense Revenue",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "آپ کی لائیو ایپ ارننگ اور گوگل اشتہارات",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.85f),
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color.Black.copy(alpha = 0.25f)
                        ) {
                            Text(
                                text = if (isTestMode) "TEST MODE" else "LIVE PRODUCTION",
                                color = if (isTestMode) Color(0xFFFDE047) else Color(0xFF6EE7B7),
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = "TOTAL ESTIMATED EARNINGS",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.75f),
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.sp
                    )

                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = String.format(Locale.US, "$%.2f", earningsStats.totalEarnings),
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            fontSize = 38.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "USD",
                            style = MaterialTheme.typography.titleSmall,
                            color = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = Color.White.copy(alpha = 0.2f))
                    Spacer(modifier = Modifier.height(12.dp))

                    // Revenue streams breakdown
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Google Ads Earnings",
                                color = Color.White.copy(alpha = 0.75f),
                                fontSize = 10.sp
                            )
                            Text(
                                text = String.format(Locale.US, "$%.2f", earningsStats.estimatedAdRevenue),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                        Column {
                            Text(
                                text = "VIP Subscriptions",
                                color = Color.White.copy(alpha = 0.75f),
                                fontSize = 10.sp
                            )
                            Text(
                                text = String.format(Locale.US, "$%.2f", earningsStats.subscriptionRevenue),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                        Column {
                            Text(
                                text = "Effective eCPM",
                                color = Color.White.copy(alpha = 0.75f),
                                fontSize = 10.sp
                            )
                            Text(
                                text = String.format(Locale.US, "$%.2f", earningsStats.effectiveCpm),
                                color = Color(0xFFFDE047),
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Ad Impressions & Metrics Grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Impressions",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${earningsStats.totalImpressions}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Banner: ${earningsStats.bannerImpressions} · Int: ${earningsStats.interstitialImpressions}",
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 9.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Ad Clicks (CTR)",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${earningsStats.totalClicks} (${String.format(Locale.US, "%.1f", earningsStats.clickThroughRate)}%)",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Rewarded Views: ${earningsStats.rewardedImpressions}",
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 9.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Quick Test Actions: Trigger Ads & Earn
        Text(
            text = "Live Ad Testing & Revenue Simulation",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Test how ads trigger in the app and immediately add to your earnings balance:",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(10.dp))

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onTriggerInterstitial,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier.testTag("btn_trigger_interstitial")
            ) {
                Icon(Icons.Default.Tv, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Show Interstitial Ad")
            }

            Button(
                onClick = onTriggerRewarded,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                modifier = Modifier.testTag("btn_trigger_rewarded")
            ) {
                Icon(Icons.Default.PlayCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Watch Rewarded Video")
            }

            OutlinedButton(
                onClick = { onSimulateBatchImpressions(100) },
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("btn_simulate_100")
            ) {
                Icon(Icons.Default.TrendingUp, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("+100 Impressions (~$2.00)")
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Google AdMob & AdSense Configuration Form
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("admob_configuration_card"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Google AdSense & AdMob IDs",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Connect your Google AdSense/AdMob account",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.AccountBalance,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Ads Master Switch
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Enable Ads in App",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Display banner, interstitial & rewarded ads",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )
                    }
                    Switch(
                        checked = isAdsEnabled,
                        onCheckedChange = { isAdsEnabled = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF10B981))
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Test Mode Switch
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Google Test Ads Mode",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Use Google official test units before going live",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )
                    }
                    Switch(
                        checked = isTestMode,
                        onCheckedChange = { isTestMode = it }
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Publisher ID Input
                OutlinedTextField(
                    value = publisherId,
                    onValueChange = { publisherId = it },
                    label = { Text("Google AdSense / AdMob Publisher ID") },
                    placeholder = { Text("pub-XXXXXXXXXXXXXXXX") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_publisher_id"),
                    supportingText = { Text("Found in AdSense -> Account -> Settings -> Account Information") }
                )

                Spacer(modifier = Modifier.height(10.dp))

                // AdMob App ID
                OutlinedTextField(
                    value = adMobAppId,
                    onValueChange = { adMobAppId = it },
                    label = { Text("AdMob App ID") },
                    placeholder = { Text("ca-app-pub-XXXXXXXXXXXXXXXX~YYYYYYYYYY") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_admob_app_id")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Banner Unit ID
                OutlinedTextField(
                    value = bannerUnitId,
                    onValueChange = { bannerUnitId = it },
                    label = { Text("Banner Ad Unit ID") },
                    placeholder = { Text("ca-app-pub-XXXXXXXXXXXXXXXX/ZZZZZZZZZZ") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_banner_unit_id")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Interstitial Unit ID
                OutlinedTextField(
                    value = interstitialUnitId,
                    onValueChange = { interstitialUnitId = it },
                    label = { Text("Interstitial Ad Unit ID") },
                    placeholder = { Text("ca-app-pub-XXXXXXXXXXXXXXXX/XXXXXXXXXX") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_interstitial_unit_id")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Rewarded Unit ID
                OutlinedTextField(
                    value = rewardedUnitId,
                    onValueChange = { rewardedUnitId = it },
                    label = { Text("Rewarded Video Ad Unit ID") },
                    placeholder = { Text("ca-app-pub-XXXXXXXXXXXXXXXX/WWWWWWWWWW") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_rewarded_unit_id")
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        onUpdateConfig(
                            adMobConfig.copy(
                                publisherId = publisherId.trim(),
                                adMobAppId = adMobAppId.trim(),
                                bannerAdUnitId = bannerUnitId.trim(),
                                interstitialAdUnitId = interstitialUnitId.trim(),
                                rewardedAdUnitId = rewardedUnitId.trim(),
                                isTestMode = isTestMode,
                                isAdsEnabled = isAdsEnabled
                            )
                        )
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("btn_save_admob_config"),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(imageVector = Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Save & Apply Google AdMob Config", fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Step-by-step How to Earn Guide (Bilingual English & Urdu)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.HelpOutline,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "گوگل ایڈسنس اور ایڈموب سے ارننگ کیسے ہوگی؟",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                val steps = listOf(
                    "1. گوگل ایڈموب (admob.google.com) پر جائیں اور اپنا اکاؤنٹ بنا کر اپنے گوگل ایڈسنس (AdSense) کے ساتھ لنک کریں۔",
                    "2. ایڈموب میں نئی ایپ شامل کریں اور بینر، انٹراسٹیشل اور ریوارڈڈ ایڈ یونٹس بنائیں۔",
                    "3. اوپر دی گئی سیٹنگز میں اپنی Publisher ID اور Ad Unit IDs کو کاپی پیسٹ کریں۔",
                    "4. جب لوگ آپ کی ایپ استعمال کریں گے تو گوگل ایڈز چلیں گے، اور گوگل ہر مہینے کی 21 تاریخ کو آپ کی کمائی براہِ راست آپ کے بینک اکاؤنٹ میں بھیجے گا!"
                )

                steps.forEach { step ->
                    Text(
                        text = step,
                        style = MaterialTheme.typography.bodySmall,
                        lineHeight = 20.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(vertical = 3.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Revenue Projection Calculator
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Projected Monthly Revenue Calculator",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "See how much you can earn based on Daily Active Users (DAU):",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Daily Users: ${estimatedDau.toInt()} users", fontWeight = FontWeight.Bold)
                    val monthlyRevenue = (estimatedDau * 3.5 * (earningsStats.effectiveCpm.coerceAtLeast(3.50) / 1000.0) * 30.0)
                    Text(
                        text = String.format(Locale.US, "~$%.2f / month", monthlyRevenue),
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF10B981)
                    )
                }

                Slider(
                    value = estimatedDau,
                    onValueChange = { estimatedDau = it },
                    valueRange = 500f..50000f,
                    steps = 19
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
