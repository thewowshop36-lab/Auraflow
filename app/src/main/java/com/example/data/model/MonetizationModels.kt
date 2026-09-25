package com.example.data.model

data class AdMobConfig(
    val publisherId: String = "pub-9283471029384756",
    val adMobAppId: String = "ca-app-pub-3940256099942544~3347511713",
    val bannerAdUnitId: String = "ca-app-pub-3940256099942544/6300978111",
    val interstitialAdUnitId: String = "ca-app-pub-3940256099942544/1033173712",
    val rewardedAdUnitId: String = "ca-app-pub-3940256099942544/5224354917",
    val isTestMode: Boolean = true,
    val isAdsEnabled: Boolean = true,
    val customWebsiteUrl: String = "https://adsense.google.com"
)

data class AdEarningsStats(
    val totalImpressions: Int = 142,
    val bannerImpressions: Int = 118,
    val interstitialImpressions: Int = 18,
    val rewardedImpressions: Int = 6,
    val totalClicks: Int = 9,
    val estimatedAdRevenue: Double = 2.84,
    val subscriptionRevenue: Double = 39.99,
    val currencySymbol: String = "$"
) {
    val totalEarnings: Double
        get() = estimatedAdRevenue + subscriptionRevenue

    val effectiveCpm: Double
        get() = if (totalImpressions > 0) (estimatedAdRevenue / totalImpressions) * 1000.0 else 0.0

    val clickThroughRate: Double
        get() = if (totalImpressions > 0) (totalClicks.toDouble() / totalImpressions) * 100.0 else 0.0
}

data class UserSubscriptionState(
    val isVipPro: Boolean = false,
    val planType: String = "Free Explorer",
    val aiCreditsRemaining: Int = 15,
    val unlockedTemplatesCount: Int = 1,
    val expiryDate: String = "Never (Free Limit)"
)

data class PremiumCodeTemplate(
    val id: String,
    val title: String,
    val badge: String,
    val category: String,
    val description: String,
    val features: List<String>,
    val estimatedMarketPrice: String,
    val isUnlocked: Boolean,
    val rawKotlinCode: String
)
