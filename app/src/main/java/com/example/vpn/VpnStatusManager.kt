package com.example.vpn

import com.example.ui.VpnStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class VpnSessionInfo(
    val status: VpnStatus = VpnStatus.DISCONNECTED,
    val step: Int = 0,
    val statusText: String = "พร้อมเชื่อมต่อ",
    val profileName: String = "",
    val connectedHost: String = "",
    val connectedSeconds: Long = 0,
    val bytesIn: Long = 0,
    val bytesOut: Long = 0,
    val logs: List<String> = emptyList()
)

object VpnStatusManager {
    private val _session = MutableStateFlow(VpnSessionInfo())
    val session: StateFlow<VpnSessionInfo> = _session.asStateFlow()

    fun updateStatus(
        status: VpnStatus,
        step: Int,
        statusText: String,
        profileName: String = _session.value.profileName,
        connectedHost: String = _session.value.connectedHost
    ) {
        _session.value = _session.value.copy(
            status = status,
            step = step,
            statusText = statusText,
            profileName = profileName,
            connectedHost = connectedHost
        )
    }

    fun addLog(log: String) {
        val currentLogs = _session.value.logs.takeLast(100).toMutableList()
        currentLogs.add(log)
        _session.value = _session.value.copy(logs = currentLogs)
    }

    fun updateDuration(seconds: Long) {
        _session.value = _session.value.copy(connectedSeconds = seconds)
    }

    fun updateTraffic(bytesIn: Long, bytesOut: Long) {
        _session.value = _session.value.copy(bytesIn = bytesIn, bytesOut = bytesOut)
    }

    fun reset() {
        _session.value = VpnSessionInfo()
    }
}
