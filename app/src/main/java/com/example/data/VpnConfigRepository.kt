package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.VpnConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject

class VpnConfigRepository(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("vpn_settings_prefs", Context.MODE_PRIVATE)

    private val _profiles = MutableStateFlow<List<VpnConfig>>(emptyList())
    val profiles: StateFlow<List<VpnConfig>> = _profiles.asStateFlow()

    private val _activeProfileId = MutableStateFlow<String>("")
    val activeProfileId: StateFlow<String> = _activeProfileId.asStateFlow()

    init {
        loadFromPrefs()
    }

    private fun loadFromPrefs() {
        val jsonString = prefs.getString(KEY_PROFILES_JSON, null)
        val list = mutableListOf<VpnConfig>()

        if (jsonString != null && jsonString.isNotBlank()) {
            try {
                val array = JSONArray(jsonString)
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    list.add(jsonToConfig(obj))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        if (list.isEmpty()) {
            list.add(VpnConfig.createDefault())
            list.add(VpnConfig.createSshDirect())
            list.add(VpnConfig.createSslSni())
            list.add(VpnConfig.createPayloadSni())
        }

        _profiles.value = list

        val savedActiveId = prefs.getString(KEY_ACTIVE_PROFILE_ID, null)
        if (savedActiveId != null && list.any { it.id == savedActiveId }) {
            _activeProfileId.value = savedActiveId
        } else {
            _activeProfileId.value = list.first().id
            prefs.edit().putString(KEY_ACTIVE_PROFILE_ID, _activeProfileId.value).apply()
        }
        persistProfiles(list)
    }

    private fun persistProfiles(list: List<VpnConfig>) {
        val array = JSONArray()
        for (item in list) {
            array.put(configToJson(item))
        }
        prefs.edit().putString(KEY_PROFILES_JSON, array.toString()).apply()
    }

    fun getActiveConfig(): VpnConfig {
        val currentList = _profiles.value
        val currentActiveId = _activeProfileId.value
        return currentList.find { it.id == currentActiveId } ?: currentList.firstOrNull() ?: VpnConfig.createDefault()
    }

    fun setActiveProfile(profileId: String) {
        if (_profiles.value.any { it.id == profileId }) {
            _activeProfileId.value = profileId
            prefs.edit().putString(KEY_ACTIVE_PROFILE_ID, profileId).apply()
        }
    }

    fun saveConfig(updated: VpnConfig) {
        val current = _profiles.value.toMutableList()
        val index = current.indexOfFirst { it.id == updated.id }
        if (index != -1) {
            current[index] = updated.copy(updatedAt = System.currentTimeMillis())
        } else {
            current.add(updated.copy(updatedAt = System.currentTimeMillis()))
        }
        _profiles.value = current
        persistProfiles(current)
    }

    fun addNewProfile(name: String, template: VpnConfig? = null): VpnConfig {
        val base = template ?: VpnConfig.createDefault()
        val newProfile = base.copy(
            id = java.util.UUID.randomUUID().toString(),
            name = name,
            updatedAt = System.currentTimeMillis()
        )
        val current = _profiles.value.toMutableList()
        current.add(newProfile)
        _profiles.value = current
        _activeProfileId.value = newProfile.id
        prefs.edit().putString(KEY_ACTIVE_PROFILE_ID, newProfile.id).apply()
        persistProfiles(current)
        return newProfile
    }

    fun deleteProfile(profileId: String): Boolean {
        val current = _profiles.value.toMutableList()
        if (current.size <= 1) return false // Keep at least one profile

        val index = current.indexOfFirst { it.id == profileId }
        if (index != -1) {
            current.removeAt(index)
            _profiles.value = current
            if (_activeProfileId.value == profileId) {
                _activeProfileId.value = current.first().id
                prefs.edit().putString(KEY_ACTIVE_PROFILE_ID, current.first().id).apply()
            }
            persistProfiles(current)
            return true
        }
        return false
    }

    fun duplicateProfile(profileId: String): VpnConfig? {
        val current = _profiles.value.find { it.id == profileId } ?: return null
        return addNewProfile("${current.name} (คัดลอก)", current)
    }

    fun resetToDefaults() {
        val defaults = listOf(
            VpnConfig.createDefault(),
            VpnConfig.createSshDirect(),
            VpnConfig.createSslSni(),
            VpnConfig.createPayloadSni()
        )
        _profiles.value = defaults
        _activeProfileId.value = defaults.first().id
        prefs.edit()
            .putString(KEY_ACTIVE_PROFILE_ID, defaults.first().id)
            .apply()
        persistProfiles(defaults)
    }

    private fun configToJson(config: VpnConfig): JSONObject {
        return JSONObject().apply {
            put("id", config.id)
            put("name", config.name)
            put("connectionMode", config.connectionMode)
            put("autoReplace", config.autoReplace)
            put("payload", config.payload)
            put("proxyIp", config.proxyIp)
            put("proxyPort", config.proxyPort)
            put("sshHost", config.sshHost)
            put("sshPort", config.sshPort)
            put("sshUsername", config.sshUsername)
            put("sshPassword", config.sshPassword)
            put("enableCompression", config.enableCompression)
            put("authMethod", config.authMethod)
            put("publicKey", config.publicKey)
            put("sniServerName", config.sniServerName)
            put("updatedAt", config.updatedAt)
        }
    }

    private fun jsonToConfig(obj: JSONObject): VpnConfig {
        return VpnConfig(
            id = obj.optString("id", java.util.UUID.randomUUID().toString()),
            name = obj.optString("name", "Profile"),
            connectionMode = obj.optInt("connectionMode", 1),
            autoReplace = obj.optBoolean("autoReplace", true),
            payload = obj.optString("payload", "GET / HTTP/1.1[crlf]Host: [host][crlf]Connection: Upgrade[crlf][crlf]"),
            proxyIp = obj.optString("proxyIp", ""),
            proxyPort = obj.optString("proxyPort", ""),
            sshHost = obj.optString("sshHost", ""),
            sshPort = obj.optString("sshPort", "22"),
            sshUsername = obj.optString("sshUsername", ""),
            sshPassword = obj.optString("sshPassword", ""),
            enableCompression = obj.optBoolean("enableCompression", false),
            authMethod = obj.optString("authMethod", "password"),
            publicKey = obj.optString("publicKey", ""),
            sniServerName = obj.optString("sniServerName", ""),
            updatedAt = obj.optLong("updatedAt", System.currentTimeMillis())
        )
    }

    companion object {
        private const val KEY_PROFILES_JSON = "profiles_json"
        private const val KEY_ACTIVE_PROFILE_ID = "active_profile_id"
    }
}
