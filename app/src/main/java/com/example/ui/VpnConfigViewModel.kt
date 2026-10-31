package com.example.ui

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.VpnService
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.VpnConfigRepository
import com.example.model.VpnConfig
import com.example.vpn.CustomVpnService
import com.example.vpn.VpnStatusManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket

enum class VpnStatus {
    DISCONNECTED,
    CONNECTING,
    CONNECTED
}

data class ConfigUiState(
    val profiles: List<VpnConfig> = emptyList(),
    val activeProfileId: String = "",
    val editingConfig: VpnConfig = VpnConfig.createDefault(),
    val originalConfig: VpnConfig = VpnConfig.createDefault(),
    val isTestingConnection: Boolean = false,
    val connectionTestResult: String? = null,
    val isTestSuccess: Boolean? = null,
    val showPassword: Boolean = false,
    val showImportDialog: Boolean = false,
    val showExportDialog: Boolean = false,
    val showNewProfileDialog: Boolean = false,
    val showDeleteConfirmDialog: Boolean = false,
    val showTemplateDialog: Boolean = false,
    val showSniSuggestionsDialog: Boolean = false,
    val selectedTab: Int = 0, // 0: Editor, 1: Live Preview, 2: Diagnostics
    // Connection Animation States
    val showConnectionModal: Boolean = false,
    val vpnStatus: VpnStatus = VpnStatus.DISCONNECTED,
    val connectionStep: Int = 0, // 0: Idle, 1: Proxy/Payload, 2: SSH Handshake, 3: Routing/Tunnel, 4: Connected
    val connectionStatusText: String = "พร้อมเชื่อมต่อ",
    val connectedSeconds: Long = 0,
    val connectionLogs: List<String> = emptyList()
) {
    val hasUnsavedChanges: Boolean
        get() = editingConfig != originalConfig
}

sealed class UiEvent {
    data class ShowToast(val message: String) : UiEvent()
    data class RequestVpnPermission(val intent: Intent) : UiEvent()
}

class VpnConfigViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = VpnConfigRepository(application)

    private val _uiState = MutableStateFlow(ConfigUiState())
    val uiState: StateFlow<ConfigUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<UiEvent>()
    val events: SharedFlow<UiEvent> = _events.asSharedFlow()

    init {
        // Load profiles from storage
        viewModelScope.launch {
            repository.profiles.collect { list ->
                val activeId = repository.activeProfileId.value
                val activeConf = list.find { it.id == activeId } ?: list.firstOrNull() ?: VpnConfig.createDefault()
                _uiState.update { state ->
                    state.copy(
                        profiles = list,
                        activeProfileId = activeConf.id,
                        editingConfig = activeConf,
                        originalConfig = activeConf
                    )
                }
            }
        }

        // Collect real VpnService state
        viewModelScope.launch {
            VpnStatusManager.session.collect { session ->
                _uiState.update { state ->
                    state.copy(
                        vpnStatus = session.status,
                        connectionStep = session.step,
                        connectionStatusText = session.statusText,
                        connectedSeconds = session.connectedSeconds,
                        connectionLogs = session.logs
                    )
                }
            }
        }
    }

    fun selectTab(index: Int) {
        _uiState.update { it.copy(selectedTab = index) }
    }

    fun switchProfile(profileId: String) {
        val target = _uiState.value.profiles.find { it.id == profileId } ?: return
        repository.setActiveProfile(profileId)
        _uiState.update {
            it.copy(
                activeProfileId = profileId,
                editingConfig = target,
                originalConfig = target,
                connectionTestResult = null,
                isTestSuccess = null
            )
        }
        emitMessage("สลับเป็นโปรไฟล์: ${target.name}")
    }

    fun updateConnectionMode(mode: Int) {
        _uiState.update {
            it.copy(editingConfig = it.editingConfig.copy(connectionMode = mode))
        }
    }

    fun updateAutoReplace(enabled: Boolean) {
        _uiState.update {
            it.copy(editingConfig = it.editingConfig.copy(autoReplace = enabled))
        }
    }

    fun updatePayload(text: String) {
        _uiState.update {
            it.copy(editingConfig = it.editingConfig.copy(payload = text))
        }
    }

    fun insertTagToPayload(tag: String) {
        val current = _uiState.value.editingConfig.payload
        val updated = if (current.isEmpty()) tag else "$current$tag"
        updatePayload(updated)
    }

    fun updateProxyIp(ip: String) {
        _uiState.update {
            it.copy(editingConfig = it.editingConfig.copy(proxyIp = ip.trim()))
        }
    }

    fun updateProxyPort(port: String) {
        _uiState.update {
            it.copy(editingConfig = it.editingConfig.copy(proxyPort = port.trim()))
        }
    }

    fun updateSshHost(host: String) {
        _uiState.update {
            it.copy(editingConfig = it.editingConfig.copy(sshHost = host.trim()))
        }
    }

    fun updateSshPort(port: String) {
        _uiState.update {
            it.copy(editingConfig = it.editingConfig.copy(sshPort = port.trim()))
        }
    }

    fun updateSshUsername(username: String) {
        _uiState.update {
            it.copy(editingConfig = it.editingConfig.copy(sshUsername = username))
        }
    }

    fun updateSshPassword(password: String) {
        _uiState.update {
            it.copy(editingConfig = it.editingConfig.copy(sshPassword = password))
        }
    }

    fun updateEnableCompression(enabled: Boolean) {
        _uiState.update {
            it.copy(editingConfig = it.editingConfig.copy(enableCompression = enabled))
        }
    }

    fun updateAuthMethod(method: String) {
        _uiState.update {
            it.copy(editingConfig = it.editingConfig.copy(authMethod = method))
        }
    }

    fun updatePublicKey(key: String) {
        _uiState.update {
            it.copy(editingConfig = it.editingConfig.copy(publicKey = key))
        }
    }

    fun updateSniServerName(sni: String) {
        _uiState.update {
            it.copy(editingConfig = it.editingConfig.copy(sniServerName = sni.trim()))
        }
    }

    fun updateProfileName(name: String) {
        _uiState.update {
            it.copy(editingConfig = it.editingConfig.copy(name = name.trim()))
        }
    }

    fun togglePasswordVisibility() {
        _uiState.update { it.copy(showPassword = !it.showPassword) }
    }

    fun saveChanges() {
        val current = _uiState.value.editingConfig
        repository.saveConfig(current)
        _uiState.update {
            it.copy(originalConfig = current)
        }
        emitMessage("บันทึกการตั้งค่า '${current.name}' เรียบร้อยแล้ว (บันทึกในแอป ไม่ต้องใช้ไฟล์ .ini)")
    }

    fun createNewProfile(name: String) {
        if (name.isBlank()) return
        val newConfig = repository.addNewProfile(name, _uiState.value.editingConfig)
        _uiState.update {
            it.copy(
                activeProfileId = newConfig.id,
                editingConfig = newConfig,
                originalConfig = newConfig,
                showNewProfileDialog = false
            )
        }
        emitMessage("สร้างโปรไฟล์ใหม่: $name")
    }

    fun duplicateCurrentProfile() {
        val current = _uiState.value.editingConfig
        val duplicated = repository.duplicateProfile(current.id)
        if (duplicated != null) {
            _uiState.update {
                it.copy(
                    activeProfileId = duplicated.id,
                    editingConfig = duplicated,
                    originalConfig = duplicated
                )
            }
            emitMessage("คัดลอกโปรไฟล์ '${current.name}' เรียบร้อย")
        }
    }

    fun deleteCurrentProfile() {
        val currentId = _uiState.value.editingConfig.id
        val deleted = repository.deleteProfile(currentId)
        if (deleted) {
            _uiState.update { it.copy(showDeleteConfirmDialog = false) }
            emitMessage("ลบโปรไฟล์เรียบร้อยแล้ว")
        } else {
            emitMessage("ต้องมีโปรไฟล์อย่างน้อย 1 รายการ")
        }
    }

    fun applyPayloadTemplate(templatePayload: String, templateMode: Int? = null) {
        _uiState.update { state ->
            val updated = state.editingConfig.copy(
                payload = templatePayload,
                connectionMode = templateMode ?: state.editingConfig.connectionMode
            )
            state.copy(editingConfig = updated, showTemplateDialog = false)
        }
        emitMessage("นำเพย์โหลดเทมเพลตมาใช้งานแล้ว")
    }

    fun applySniSuggestion(sni: String) {
        _uiState.update { state ->
            val updated = state.editingConfig.copy(
                sniServerName = sni,
                connectionMode = if (state.editingConfig.connectionMode == 0 || state.editingConfig.connectionMode == 1) 2 else state.editingConfig.connectionMode
            )
            state.copy(editingConfig = updated, showSniSuggestionsDialog = false)
        }
        emitMessage("ตั้งค่า SNI เป็น $sni")
    }

    fun importFromIni(iniText: String, profileName: String) {
        try {
            val parsed = VpnConfig.parseIni(iniText, if (profileName.isNotBlank()) profileName else "นำเข้า (${System.currentTimeMillis() % 1000})")
            val saved = repository.addNewProfile(parsed.name, parsed)
            _uiState.update {
                it.copy(
                    activeProfileId = saved.id,
                    editingConfig = saved,
                    originalConfig = saved,
                    showImportDialog = false
                )
            }
            emitMessage("นำเข้าการตั้งค่าเรียบร้อยแล้ว!")
        } catch (e: Exception) {
            emitMessage("เกิดข้อผิดพลาดในการอ่านการตั้งค่า: ${e.localizedMessage}")
        }
    }

    // --- REAL ANDROID VPN SERVICE LIFECYCLE ---
    fun openConnectionModal(context: Context) {
        _uiState.update { it.copy(showConnectionModal = true) }
        if (_uiState.value.vpnStatus == VpnStatus.DISCONNECTED) {
            connectRealVpn(context)
        }
    }

    fun closeConnectionModal() {
        _uiState.update { it.copy(showConnectionModal = false) }
    }

    fun connectRealVpn(context: Context) {
        val config = _uiState.value.editingConfig
        val vpnPrepareIntent = VpnService.prepare(context)
        if (vpnPrepareIntent != null) {
            viewModelScope.launch {
                _events.emit(UiEvent.RequestVpnPermission(vpnPrepareIntent))
            }
        } else {
            // Already has VPN permission, start real service
            startCustomVpn(context)
        }
    }

    fun onVpnPermissionGranted(context: Context) {
        startCustomVpn(context)
    }

    fun onVpnPermissionDenied() {
        _uiState.update {
            it.copy(
                vpnStatus = VpnStatus.DISCONNECTED,
                connectionStatusText = "ผู้ใช้ปฏิเสธการอนุญาตระบบ VPN"
            )
        }
        emitMessage("ต้องอนุญาตการเชื่อมต่อระบบ VPN ใน Android ก่อนใช้งาน")
    }

    private fun startCustomVpn(context: Context) {
        val config = _uiState.value.editingConfig
        CustomVpnService.startVpn(context, config)
        emitMessage("กำลังเริ่มเชื่อมต่อ VPN: ${config.name}")
    }

    fun disconnectVpn(context: Context) {
        CustomVpnService.stopVpn(context)
        _uiState.update {
            it.copy(
                vpnStatus = VpnStatus.DISCONNECTED,
                connectionStep = 0,
                connectionStatusText = "ตัดการเชื่อมต่อแล้ว"
            )
        }
        emitMessage("ตัดการเชื่อมต่อ VPN แล้ว")
    }

    fun testConnection() {
        val config = _uiState.value.editingConfig
        val targetHost = when {
            config.proxyIp.isNotBlank() && (config.connectionMode == 1 || config.connectionMode == 3) -> config.proxyIp
            config.sshHost.isNotBlank() -> config.sshHost
            else -> ""
        }

        val targetPort = when {
            config.proxyIp.isNotBlank() && (config.connectionMode == 1 || config.connectionMode == 3) ->
                config.proxyPort.toIntOrNull() ?: 8080
            config.sshPort.isNotBlank() ->
                config.sshPort.toIntOrNull() ?: 22
            else -> 22
        }

        if (targetHost.isBlank()) {
            _uiState.update {
                it.copy(
                    isTestingConnection = false,
                    connectionTestResult = "กรุณาระบุ SSH Host หรือ Proxy IP ก่อนทำการทดสอบ",
                    isTestSuccess = false
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isTestingConnection = true,
                    connectionTestResult = "กำลังทดสอบเชื่อมต่อไปยัง $targetHost:$targetPort ...",
                    isTestSuccess = null
                )
            }

            val result = withContext(Dispatchers.IO) {
                try {
                    val startResolve = System.currentTimeMillis()
                    val addresses = InetAddress.getAllByName(targetHost)
                    val resolveTime = System.currentTimeMillis() - startResolve
                    val resolvedIp = addresses.firstOrNull()?.hostAddress ?: targetHost

                    val startConnect = System.currentTimeMillis()
                    Socket().use { socket ->
                        socket.connect(InetSocketAddress(targetHost, targetPort), 4000)
                    }
                    val latency = System.currentTimeMillis() - startConnect

                    Pair(
                        true,
                        "เชื่อมต่อสำเร็จ!\n• ปลายทาง: $targetHost ($resolvedIp)\n• พอร์ต: $targetPort\n• ความเร็ว DNS: ${resolveTime}ms\n• TCP Latency: ${latency}ms"
                    )
                } catch (e: Exception) {
                    Pair(
                        false,
                        "ไม่สามารถเชื่อมต่อได้: ${e.message ?: "Timeout/Unreachable"}\nตรวจสอบว่า Host หรือ พอร์ตถูกต้องและเปิดใช้งานอยู่"
                    )
                }
            }

            _uiState.update {
                it.copy(
                    isTestingConnection = false,
                    isTestSuccess = result.first,
                    connectionTestResult = result.second
                )
            }
        }
    }

    fun resetToDefaults() {
        repository.resetToDefaults()
        emitMessage("คืนค่าการตั้งค่าเริ่มต้นแล้ว")
    }

    fun setShowImportDialog(show: Boolean) {
        _uiState.update { it.copy(showImportDialog = show) }
    }

    fun setShowExportDialog(show: Boolean) {
        _uiState.update { it.copy(showExportDialog = show) }
    }

    fun setShowNewProfileDialog(show: Boolean) {
        _uiState.update { it.copy(showNewProfileDialog = show) }
    }

    fun setShowDeleteConfirmDialog(show: Boolean) {
        _uiState.update { it.copy(showDeleteConfirmDialog = show) }
    }

    fun setShowTemplateDialog(show: Boolean) {
        _uiState.update { it.copy(showTemplateDialog = show) }
    }

    fun setShowSniSuggestionsDialog(show: Boolean) {
        _uiState.update { it.copy(showSniSuggestionsDialog = show) }
    }

    private fun emitMessage(msg: String) {
        viewModelScope.launch {
            _events.emit(UiEvent.ShowToast(msg))
        }
    }
}
