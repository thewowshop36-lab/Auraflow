package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.ActionConfig
import com.example.data.model.ActionType
import com.example.data.model.ConditionConfig
import com.example.data.model.TriggerConfig
import com.example.data.model.TriggerType
import com.example.data.model.WorkflowCategory
import org.json.JSONArray
import org.json.JSONObject

@Entity(tableName = "workflows")
data class WorkflowEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String,
    val naturalLanguagePrompt: String,
    val category: String,
    val triggerType: String,
    val triggerTarget: String,
    val triggerSecondary: String? = null,
    val conditionsJson: String = "[]",
    val actionsJson: String = "[]",
    val isEnabled: Boolean = true,
    val triggerCount: Int = 0,
    val lastTriggeredTime: Long = 0,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun toTriggerConfig(): TriggerConfig {
        val type = try {
            TriggerType.valueOf(triggerType)
        } catch (_: Exception) {
            TriggerType.LOCATION
        }
        return TriggerConfig(type, triggerTarget, triggerSecondary)
    }

    fun getConditions(): List<ConditionConfig> {
        val list = mutableListOf<ConditionConfig>()
        try {
            val array = JSONArray(conditionsJson)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    ConditionConfig(
                        description = obj.optString("desc", ""),
                        isEnabled = obj.optBoolean("enabled", true)
                    )
                )
            }
        } catch (_: Exception) {}
        return list
    }

    fun getActions(): List<ActionConfig> {
        val list = mutableListOf<ActionConfig>()
        try {
            val array = JSONArray(actionsJson)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val type = try {
                    ActionType.valueOf(obj.optString("type", ActionType.MUTE_PHONE.name))
                } catch (_: Exception) {
                    ActionType.MUTE_PHONE
                }
                list.add(
                    ActionConfig(
                        id = obj.optString("id", i.toString()),
                        type = type,
                        parameter = obj.optString("param", ""),
                        isEnabled = obj.optBoolean("enabled", true)
                    )
                )
            }
        } catch (_: Exception) {}
        return list
    }

    companion object {
        fun encodeConditions(conditions: List<ConditionConfig>): String {
            val array = JSONArray()
            for (cond in conditions) {
                val obj = JSONObject()
                obj.put("desc", cond.description)
                obj.put("enabled", cond.isEnabled)
                array.put(obj)
            }
            return array.toString()
        }

        fun encodeActions(actions: List<ActionConfig>): String {
            val array = JSONArray()
            for (act in actions) {
                val obj = JSONObject()
                obj.put("id", act.id)
                obj.put("type", act.type.name)
                obj.put("param", act.parameter)
                obj.put("enabled", act.isEnabled)
                array.put(obj)
            }
            return array.toString()
        }
    }
}
