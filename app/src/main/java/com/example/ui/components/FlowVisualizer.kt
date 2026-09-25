package com.example.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.BatteryAlert
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Brightness6
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DoNotDisturbOn
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.WorkflowEntity
import com.example.data.model.ActionConfig
import com.example.data.model.ActionType
import com.example.data.model.ConditionConfig
import com.example.data.model.TriggerType

@Composable
fun FlowVisualizer(
    workflow: WorkflowEntity,
    modifier: Modifier = Modifier,
    isExpanded: Boolean = false
) {
    val triggerConfig = workflow.toTriggerConfig()
    val conditions = workflow.getConditions()
    val actions = workflow.getActions()

    Column(modifier = modifier.fillMaxWidth()) {
        // Trigger Node
        FlowNodeItem(
            nodeType = "WHEN TRIGGER",
            title = triggerConfig.targetValue,
            subtitle = triggerConfig.type.displayName + (triggerConfig.secondaryValue?.let { " • $it" } ?: ""),
            icon = getTriggerIcon(triggerConfig.type),
            color = MaterialTheme.colorScheme.primary
        )

        // Connector
        FlowConnector()

        // Conditions Node (if present)
        if (conditions.isNotEmpty()) {
            FlowNodeItem(
                nodeType = "IF CONDITION",
                title = conditions.firstOrNull()?.description ?: "Context verified",
                subtitle = if (conditions.size > 1) "+${conditions.size - 1} more conditions" else "All checks pass",
                icon = Icons.Default.FilterAlt,
                color = MaterialTheme.colorScheme.secondary
            )
            FlowConnector()
        }

        // Actions Pipeline
        Text(
            text = "THEN EXECUTE ACTIONS (${actions.size})",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.tertiary,
            letterSpacing = 0.5.sp,
            modifier = Modifier.padding(start = 8.dp, bottom = 6.dp)
        )

        val displayActions = if (isExpanded) actions else actions.take(3)
        displayActions.forEachIndexed { index, action ->
            ActionNodeItem(
                index = index + 1,
                action = action
            )
            if (index < displayActions.size - 1) {
                Spacer(modifier = Modifier.height(6.dp))
            }
        }

        if (!isExpanded && actions.size > 3) {
            Text(
                text = "+ ${actions.size - 3} more automated steps",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 28.dp, top = 4.dp)
            )
        }
    }
}

@Composable
fun FlowNodeItem(
    nodeType: String,
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.12f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.35f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.25f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = nodeType,
                    style = MaterialTheme.typography.labelSmall,
                    color = color,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                if (subtitle.isNotBlank()) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun ActionNodeItem(
    index: Int,
    action: ActionConfig
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$index",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.tertiary,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = getActionIcon(action.type),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = action.type.displayName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                if (action.parameter.isNotBlank()) {
                    Text(
                        text = "\"${action.parameter}\"",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
fun FlowConnector() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .width(2.dp)
                .height(14.dp)
                .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.6f))
        )
    }
}

fun getTriggerIcon(type: TriggerType): ImageVector {
    return when (type) {
        TriggerType.LOCATION -> Icons.Default.LocationOn
        TriggerType.TIME_SCHEDULE -> Icons.Default.Schedule
        TriggerType.BATTERY -> Icons.Default.BatteryAlert
        TriggerType.HEADPHONES -> Icons.Default.Headphones
        TriggerType.BLUETOOTH -> Icons.Default.Bluetooth
        TriggerType.NOTIFICATION_RECEIVED -> Icons.Default.NotificationsActive
        TriggerType.APP_OPENED -> Icons.Default.Apps
    }
}

fun getActionIcon(type: ActionType): ImageVector {
    return when (type) {
        ActionType.MUTE_PHONE -> Icons.AutoMirrored.Filled.VolumeOff
        ActionType.ENABLE_VIBRATE -> Icons.Default.Vibration
        ActionType.TOGGLE_DND -> Icons.Default.DoNotDisturbOn
        ActionType.SEND_AUTO_REPLY -> Icons.AutoMirrored.Filled.Send
        ActionType.OPEN_APP -> Icons.Default.Apps
        ActionType.SET_BRIGHTNESS -> Icons.Default.Brightness6
        ActionType.SPEAK_NOTIFICATION -> Icons.Default.RecordVoiceOver
        ActionType.ENABLE_BATTERY_SAVER -> Icons.Default.BatteryChargingFull
        ActionType.LOG_COMMUTE -> Icons.Default.TrackChanges
        ActionType.TOGGLE_WIFI -> Icons.Default.Wifi
        ActionType.SEND_WEBHOOK -> Icons.AutoMirrored.Filled.ArrowForward
    }
}
