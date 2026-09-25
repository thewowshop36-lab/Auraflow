package com.example.ui.components.ads

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AdMobConfig
import kotlinx.coroutines.delay

private data class AdCreative(
    val title: String,
    val description: String,
    val sponsor: String,
    val ctaText: String,
    val rating: String,
    val accentColor: Color
)

private val SAMPLE_CREATIVES = listOf(
    AdCreative(
        title = "Google Cloud Vertex AI",
        description = "Build and scale generative AI apps with cutting-edge Gemini models.",
        sponsor = "cloud.google.com",
        ctaText = "Start Free",
        rating = "4.9 ★",
        accentColor = Color(0xFF4285F4)
    ),
    AdCreative(
        title = "Firebase App Hosting",
        description = "Deploy next-gen web & mobile backends with zero-config serverless architecture.",
        sponsor = "firebase.google.com",
        ctaText = "Deploy Now",
        rating = "4.8 ★",
        accentColor = Color(0xFFFF8F00)
    ),
    AdCreative(
        title = "Flutter & Dart Pro",
        description = "Craft multi-platform native experiences with world-class velocity.",
        sponsor = "flutter.dev",
        ctaText = "Explore",
        rating = "4.9 ★",
        accentColor = Color(0xFF02569B)
    ),
    AdCreative(
        title = "Android Studio Ladybug",
        description = "Intelligent Android development with deep Gemini code assist integration.",
        sponsor = "developer.android.com",
        ctaText = "Download",
        rating = "4.9 ★",
        accentColor = Color(0xFF3DDC84)
    )
)

@Composable
fun AdMobBannerView(
    adMobConfig: AdMobConfig,
    isVipUser: Boolean,
    onAdImpression: () -> Unit,
    onAdClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!adMobConfig.isAdsEnabled || isVipUser) return

    var currentCreativeIndex by remember { mutableIntStateOf(0) }
    var showInfoDialog by remember { mutableStateOf(false) }

    // Register impression on load and rotate creatives periodically
    LaunchedEffect(Unit) {
        onAdImpression()
        while (true) {
            delay(25000) // Rotate creative every 25s
            currentCreativeIndex = (currentCreativeIndex + 1) % SAMPLE_CREATIVES.size
            onAdImpression()
        }
    }

    val creative = SAMPLE_CREATIVES[currentCreativeIndex]

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("admob_banner_view"),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            // Header bar: Google AdMob attribution
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFFFBBC05).copy(alpha = 0.2f),
                        border = androidx.compose.foundation.BorderStroke(0.5.dp, Color(0xFFFBBC05))
                    ) {
                        Text(
                            text = "Ad · Google AdSense",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = creative.sponsor,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 10.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (adMobConfig.isTestMode) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = MaterialTheme.colorScheme.tertiaryContainer
                        ) {
                            Text(
                                text = "TEST AD",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onTertiaryContainer,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }
                    IconButton(
                        onClick = { showInfoDialog = !showInfoDialog },
                        modifier = Modifier.size(22.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Ad Info",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            AnimatedVisibility(visible = showInfoDialog) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Column(modifier = Modifier.padding(6.dp)) {
                        Text(
                            text = "Google AdMob / AdSense Unit",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Unit ID: ${adMobConfig.bannerAdUnitId}",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 9.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Pub ID: ${adMobConfig.publisherId}",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 9.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Body: Creative Content + CTA
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onAdClick() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Creative App Icon
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(creative.accentColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = creative.title.take(1),
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Titles
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = creative.title,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = creative.rating,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFFF59E0B),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 10.sp
                        )
                    }
                    Text(
                        text = creative.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // CTA Button
                Button(
                    onClick = onAdClick,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = creative.accentColor
                    ),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier
                        .height(34.dp)
                        .testTag("admob_banner_cta_button")
                ) {
                    Text(
                        text = creative.ctaText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Icon(
                        imageVector = Icons.Default.OpenInNew,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }
    }
}
