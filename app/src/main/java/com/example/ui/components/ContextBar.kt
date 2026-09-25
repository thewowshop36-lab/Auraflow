package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ContextState

@Composable
fun ContextBar(
    contextState: ContextState,
    onContextChanged: (ContextState) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("context_bar_surface"),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.tertiary)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "LIVE DEVICE CONTEXT",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.BatteryChargingFull,
                        contentDescription = "Battery",
                        modifier = Modifier.size(14.dp),
                        tint = if (contextState.batteryLevel <= 20) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${contextState.batteryLevel}%",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = "Time",
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = contextState.timeOfDay.take(5),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.padding(top = 8.dp))

            // Context Presets Carousel
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Simulate:",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Preset: Office / Work
                val isOffice = contextState.locationName.contains("Office", ignoreCase = true)
                FilterChip(
                    selected = isOffice,
                    onClick = {
                        onContextChanged(
                            contextState.copy(
                                locationName = "Office / Tech Park",
                                timeOfDay = "10:30 AM (Weekdays)",
                                batteryLevel = 84,
                                isWifiConnected = true,
                                isBluetoothCarConnected = false
                            )
                        )
                    },
                    label = { Text("🏢 Work") },
                    leadingIcon = { Icon(Icons.Default.Work, contentDescription = null, modifier = Modifier.size(16.dp)) },
                    colors = FilterChipDefaults.filterChipColors(),
                    modifier = Modifier.testTag("context_chip_office")
                )

                // Preset: Gym
                val isGym = contextState.locationName.contains("Gym", ignoreCase = true)
                FilterChip(
                    selected = isGym,
                    onClick = {
                        onContextChanged(
                            contextState.copy(
                                locationName = "Fitness Center / Gym",
                                timeOfDay = "18:15 PM (Evening)",
                                batteryLevel = 62,
                                isWifiConnected = true,
                                isBluetoothCarConnected = false
                            )
                        )
                    },
                    label = { Text("🏋️ Gym") },
                    leadingIcon = { Icon(Icons.Default.FitnessCenter, contentDescription = null, modifier = Modifier.size(16.dp)) },
                    modifier = Modifier.testTag("context_chip_gym")
                )

                // Preset: Car Commute
                val isCommute = contextState.isBluetoothCarConnected
                FilterChip(
                    selected = isCommute,
                    onClick = {
                        onContextChanged(
                            contextState.copy(
                                locationName = "In Transit (Highway 101)",
                                timeOfDay = "18:45 PM",
                                batteryLevel = 55,
                                isWifiConnected = false,
                                isBluetoothCarConnected = true
                            )
                        )
                    },
                    label = { Text("🚗 Car Audio") },
                    leadingIcon = { Icon(Icons.Default.DirectionsCar, contentDescription = null, modifier = Modifier.size(16.dp)) },
                    modifier = Modifier.testTag("context_chip_commute")
                )

                // Preset: Home / Night
                val isHome = contextState.locationName.contains("Home", ignoreCase = true)
                FilterChip(
                    selected = isHome,
                    onClick = {
                        onContextChanged(
                            contextState.copy(
                                locationName = "Home",
                                timeOfDay = "22:45 PM (Night)",
                                batteryLevel = 42,
                                isWifiConnected = true,
                                isBluetoothCarConnected = false,
                                isDndActive = true
                            )
                        )
                    },
                    label = { Text("🌙 Home Night") },
                    leadingIcon = { Icon(Icons.Default.Home, contentDescription = null, modifier = Modifier.size(16.dp)) },
                    modifier = Modifier.testTag("context_chip_home")
                )

                // Preset: Low Battery (15%)
                val isLowBattery = contextState.batteryLevel <= 20
                FilterChip(
                    selected = isLowBattery,
                    onClick = {
                        onContextChanged(
                            contextState.copy(
                                batteryLevel = 15,
                                timeOfDay = "21:30 PM",
                                isWifiConnected = true
                            )
                        )
                    },
                    label = { Text("🪫 Low Battery (15%)") },
                    leadingIcon = { Icon(Icons.Default.BatteryChargingFull, contentDescription = null, modifier = Modifier.size(16.dp)) },
                    modifier = Modifier.testTag("context_chip_battery")
                )
            }
        }
    }
}
