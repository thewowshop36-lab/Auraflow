package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.NotificationPriority

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val appName: String,
    val sender: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val priority: String = NotificationPriority.WORK.name,
    val isRead: Boolean = false,
    val isReplied: Boolean = false,
    val replySent: String? = null
)
