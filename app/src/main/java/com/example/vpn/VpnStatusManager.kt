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
    val downloadSpeedKb: Double = 0.0,
    val uploadSpeedKb: Double = 0.0,
    val pingMs: Long? = null,
    val bandwidthLoadPercent: Int = 0,
    val waveformPoints: List<Float> = List(30) { 0f },
    val location: String = "Location unavailable",
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

    fun updateTelemetry(
        downloadSpeedKb: Double,
        uploadSpeedKb: Double,
        pingMs: Long?,
        bandwidthLoadPercent: Int,
        newWaveformPoint: Float
    ) {
        val oldPoints = _session.value.waveformPoints
        val newPoints = if (oldPoints.size >= 30) {
            oldPoints.drop(1) + newWaveformPoint
        } else {
            oldPoints + newWaveformPoint
        }

        _session.value = _session.value.copy(
            downloadSpeedKb = downloadSpeedKb,
            uploadSpeedKb = uploadSpeedKb,
            pingMs = pingMs,
            bandwidthLoadPercent = bandwidthLoadPercent.coerceIn(0, 100),
            waveformPoints = newPoints
        )
    }

    fun updateLocation(location: String) {
        _session.value = _session.value.copy(location = location)
    }

    /**
     * Auto-reset telemetry when disconnecting (no manual reset button)
     */
    fun resetOnDisconnect() {
        _session.value = VpnSessionInfo(
            status = VpnStatus.DISCONNECTED,
            step = 0,
            statusText = "พร้อมเชื่อมต่อ",
            profileName = _session.value.profileName,
            connectedSeconds = 0,
            bytesIn = 0,
            bytesOut = 0,
            downloadSpeedKb = 0.0,
            uploadSpeedKb = 0.0,
            pingMs = null,
            bandwidthLoadPercent = 0,
            waveformPoints = List(30) { 0f },
            location = "Location unavailable"
        )
    }

    fun reset() {
        _session.value = VpnSessionInfo()
    }
}

