package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "execution_logs")
data class ExecutionLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val workflowId: Long,
    val workflowTitle: String,
    val triggerReason: String,
    val actionsExecuted: String,
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "SUCCESS" // SUCCESS, SIMULATED, SKIPPED
)
