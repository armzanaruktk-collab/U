package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.VpnStatus
import java.util.Locale

val NeonGreen = Color(0xFF00E676)
val NeonGreenDark = Color(0xFF00B0FF)
val ModalBgDark = Color(0xFF0D1424)

@Composable
fun ConnectionModal(
    status: VpnStatus,
    step: Int,
    statusText: String,
    connectedSeconds: Long,
    host: String,
    port: String,
    logs: List<String>,
    onCancel: () -> Unit,
    onDisconnect: () -> Unit,
    onCloseModal: () -> Unit
) {
    var showLogs by remember { mutableStateOf(false) }

    // Pulsing Radar Animations
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_transition")

    val pulseScale1 by infiniteTransition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.14f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale_pulse_1"
    )

    val pulseScale2 by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "scale_pulse_2"
    )

    val pulseAlpha2 by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 0.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "alpha_pulse_2"
    )

    val isGreen = status == VpnStatus.CONNECTING || status == VpnStatus.CONNECTED
    val ringColor = if (isGreen) NeonGreen else Color.Gray

    Dialog(
        onDismissRequest = onCloseModal,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .testTag("connection_dialog"),
            shape = RoundedCornerShape(24.dp),
            color = ModalBgDark,
            border = BorderStroke(1.dp, ringColor.copy(alpha = 0.35f)),
            shadowElevation = 24.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top close icon
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(
                        onClick = onCloseModal,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White.copy(alpha = 0.6f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Pulsing Shield Radar Animation
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(190.dp)
                ) {
                    // Outer ripple ring
                    if (isGreen) {
                        Surface(
                            modifier = Modifier
                                .size(175.dp)
                                .scale(pulseScale2),
                            shape = CircleShape,
                            color = Color.Transparent,
                            border = BorderStroke(2.dp, ringColor.copy(alpha = pulseAlpha2))
                        ) {}
                    }

                    // Inner glowing ring
                    Surface(
                        modifier = Modifier
                            .size(148.dp)
                            .scale(if (isGreen) pulseScale1 else 1f),
                        shape = CircleShape,
                        color = ringColor.copy(alpha = 0.08f),
                        border = BorderStroke(3.5.dp, ringColor)
                    ) {}

                    // Second inner ring
                    Surface(
                        modifier = Modifier.size(122.dp),
                        shape = CircleShape,
                        color = Color.Transparent,
                        border = BorderStroke(2.dp, ringColor.copy(alpha = 0.6f))
                    ) {}

                    // Center Shield Body
                    Surface(
                        modifier = Modifier.size(92.dp),
                        shape = CircleShape,
                        color = ModalBgDark,
                        border = BorderStroke(2.dp, ringColor.copy(alpha = 0.8f))
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        ringColor.copy(alpha = 0.3f),
                                        Color.Transparent
                                    )
                                )
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Lock",
                                tint = Color.White,
                                modifier = Modifier.size(38.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Status text (Title)
                Text(
                    text = statusText,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 21.sp
                )

                if (status == VpnStatus.CONNECTED) {
                    val minutes = connectedSeconds / 60
                    val seconds = connectedSeconds % 60
                    val timeStr = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "เวลาที่เชื่อมต่อ: $timeStr • $host:$port",
                        style = MaterialTheme.typography.bodyMedium,
                        color = ringColor,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Checklist Section (As shown in user's image)
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFF141C30).copy(alpha = 0.8f),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        // Step 1: Proxy Check
                        ChecklistRow(
                            title = "เชื่อมต่อ Proxy สำเร็จ",
                            isChecked = step >= 1,
                            isActive = step == 0 && status == VpnStatus.CONNECTING
                        )

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 10.dp),
                            color = Color.White.copy(alpha = 0.06f)
                        )

                        // Step 2: SSH Server Connection
                        ChecklistRow(
                            title = if (step >= 2) "เชื่อมต่อ SSH Server สำเร็จ" else "กำลังเชื่อมต่อ SSH Server",
                            isChecked = step >= 2,
                            isActive = step == 1 && status == VpnStatus.CONNECTING
                        )

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 10.dp),
                            color = Color.White.copy(alpha = 0.06f)
                        )

                        // Step 3: Network Routing
                        ChecklistRow(
                            title = if (step >= 3) "ตั้งค่าเส้นทางเครือข่ายสำเร็จ" else "กำลังตั้งค่าเส้นทางเครือข่าย",
                            isChecked = step >= 3,
                            isActive = step == 2 && status == VpnStatus.CONNECTING
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Expandable Logs Toggle
                Row(
                    modifier = Modifier
                        .clickable { showLogs = !showLogs }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (showLogs) "ซ่อนบันทึกการทำงาน" else "ดูบันทึกการทำงาน (Live Logs)",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = if (showLogs) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.size(16.dp)
                    )
                }

                AnimatedVisibility(visible = showLogs) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .padding(top = 8.dp),
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFF080D18),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
                    ) {
                        LazyColumn(modifier = Modifier.padding(8.dp)) {
                            items(logs) { line ->
                                Text(
                                    text = line,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 10.5.sp,
                                        lineHeight = 15.sp
                                    ),
                                    color = if (line.contains("200 OK") || line.contains("Succeeded") || line.contains("successfully"))
                                        NeonGreen
                                    else
                                        Color.White.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Action Button (ยกเลิก / ตัดการเชื่อมต่อ)
                val buttonText = when (status) {
                    VpnStatus.CONNECTED -> "ตัดการเชื่อมต่อ"
                    else -> "ยกเลิก"
                }

                Button(
                    onClick = {
                        if (status == VpnStatus.CONNECTED) {
                            onDisconnect()
                        } else {
                            onCancel()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("connection_action_button"),
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (status == VpnStatus.CONNECTED) Color(0xFF27354A) else Color(0xFF334A68)
                    ),
                    border = BorderStroke(
                        1.dp,
                        if (status == VpnStatus.CONNECTED) Color.White.copy(alpha = 0.2f) else Color(0xFF4C6B92)
                    )
                ) {
                    Text(
                        text = buttonText,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
fun ChecklistRow(
    title: String,
    isChecked: Boolean,
    isActive: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isChecked) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Checked",
                tint = NeonGreen,
                modifier = Modifier.size(24.dp)
            )
        } else if (isActive) {
            CircularProgressIndicator(
                modifier = Modifier.size(22.dp),
                color = NeonGreen,
                strokeWidth = 2.dp
            )
        } else {
            Icon(
                imageVector = Icons.Default.RadioButtonUnchecked,
                contentDescription = "Unchecked",
                tint = Color.White.copy(alpha = 0.4f),
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = if (isChecked || isActive) Color.White else Color.White.copy(alpha = 0.5f),
            fontWeight = if (isChecked || isActive) FontWeight.SemiBold else FontWeight.Normal,
            fontSize = 14.5.sp
        )
    }
}
