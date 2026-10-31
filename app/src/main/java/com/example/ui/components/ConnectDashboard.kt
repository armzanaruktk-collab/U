package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.VpnStatus
import java.util.Locale

@Composable
fun ConnectDashboard(
    status: VpnStatus,
    location: String,
    pingMs: Long?,
    bytesIn: Long,
    bytesOut: Long,
    downloadSpeedKb: Double,
    uploadSpeedKb: Double,
    bandwidthLoadPercent: Int,
    waveformPoints: List<Float>,
    onToggleConnect: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isConnected = status == VpnStatus.CONNECTED
    val isConnecting = status == VpnStatus.CONNECTING

    // Infinite animation for glowing power ring when connecting / connected
    val infiniteTransition = rememberInfiniteTransition(label = "power_pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- CARD 1: HERO CONNECT & POWER BUTTON ---
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("hero_connect_card"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF0F1A2E)
            ),
            border = BorderStroke(1.dp, Color(0xFF1E2D4A))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 28.dp, horizontal = 18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Circular Power Button with Outer Glow Ring
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(140.dp)
                        .testTag("power_connect_button")
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) {
                            onToggleConnect()
                        }
                ) {
                    // Outer Ring
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val ringColor = when {
                            isConnected -> Color(0xFF00E676).copy(alpha = pulseAlpha)
                            isConnecting -> Color(0xFF00E5FF).copy(alpha = pulseAlpha)
                            else -> Color(0xFF1E293B)
                        }
                        drawCircle(
                            color = ringColor,
                            radius = size.minDimension / 2f - 4.dp.toPx(),
                            style = Stroke(width = 3.dp.toPx())
                        )
                    }

                    // Inner Button Surface
                    Surface(
                        modifier = Modifier.size(96.dp),
                        shape = CircleShape,
                        color = when {
                            isConnected -> Color(0xFF00E676).copy(alpha = 0.15f)
                            isConnecting -> Color(0xFF00E5FF).copy(alpha = 0.15f)
                            else -> Color(0xFF142036)
                        },
                        border = BorderStroke(
                            2.dp,
                            when {
                                isConnected -> Color(0xFF00E676)
                                isConnecting -> Color(0xFF00E5FF)
                                else -> Color(0xFF334155)
                            }
                        )
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.PowerSettingsNew,
                                contentDescription = if (isConnected) "Disconnect" else "Connect",
                                tint = when {
                                    isConnected -> Color(0xFF00E676)
                                    isConnecting -> Color(0xFF00E5FF)
                                    else -> Color.White
                                },
                                modifier = Modifier.size(46.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(22.dp))

                // Location Header & Value
                Text(
                    text = "YOUR LOCATION",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF64748B),
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp,
                    fontSize = 11.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (isConnected) location else "Location unavailable",
                    style = MaterialTheme.typography.titleMedium,
                    color = if (isConnected) Color(0xFFE2E8F0) else Color(0xFF94A3B8),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Sub-Cards: Ping and Traffic
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Ping Sub-Card
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .testTag("ping_metric_card"),
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFF142036),
                        border = BorderStroke(1.dp, Color(0xFF1E2D4A))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 14.dp, horizontal = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Ping",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF64748B),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = if (pingMs != null) "$pingMs ms" else "--",
                                style = MaterialTheme.typography.titleMedium,
                                color = if (pingMs != null) Color(0xFF00E5FF) else Color(0xFF94A3B8),
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp
                            )
                        }
                    }

                    // Traffic Sub-Card
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .testTag("traffic_metric_card"),
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFF142036),
                        border = BorderStroke(1.dp, Color(0xFF1E2D4A))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 14.dp, horizontal = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Traffic",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF64748B),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = if (isConnected || bytesIn > 0 || bytesOut > 0) {
                                    "↑ ${formatTraffic(bytesOut)}   ↓ ${formatTraffic(bytesIn)}"
                                } else {
                                    "↑ --   ↓ --"
                                },
                                style = MaterialTheme.typography.titleMedium,
                                color = if (isConnected) Color(0xFFE2E8F0) else Color(0xFF94A3B8),
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }

        // --- CARD 2: NETWORK TELEMETRY MONITOR ---
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("network_telemetry_monitor_card"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF0F1A2E)
            ),
            border = BorderStroke(1.dp, Color(0xFF1E2D4A))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                // Header: Speedometer Icon + Title + Standby/Online Badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Speed,
                            contentDescription = null,
                            tint = Color(0xFF00E5FF),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "NETWORK TELEMETRY MONITOR",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color(0xFF00E5FF),
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp,
                            fontSize = 12.sp
                        )
                    }

                    Text(
                        text = when {
                            isConnected -> "ONLINE"
                            isConnecting -> "CONNECTING"
                            else -> "STANDBY"
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = when {
                            isConnected -> Color(0xFF00E676)
                            isConnecting -> Color(0xFF00E5FF)
                            else -> Color(0xFF64748B)
                        },
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        fontSize = 11.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Speeds Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Download Speed Card
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .testTag("download_speed_card"),
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFF131D31),
                        border = BorderStroke(1.dp, Color(0xFF1E2D4A))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp)
                        ) {
                            Text(
                                text = "Download Speed",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF94A3B8),
                                fontSize = 12.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = String.format(Locale.US, "%.1f KB/s", downloadSpeedKb),
                                style = MaterialTheme.typography.titleLarge,
                                color = Color(0xFF00E676),
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        }
                    }

                    // Upload Speed Card
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .testTag("upload_speed_card"),
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFF131D31),
                        border = BorderStroke(1.dp, Color(0xFF1E2D4A))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp)
                        ) {
                            Text(
                                text = "Upload Speed",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF94A3B8),
                                fontSize = 12.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = String.format(Locale.US, "%.1f KB/s", uploadSpeedKb),
                                style = MaterialTheme.typography.titleLarge,
                                color = Color(0xFF38BDF8),
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Bandwidth Load Indicator
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Bandwidth load indicator",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF94A3B8),
                        fontSize = 12.sp
                    )
                    Text(
                        text = "$bandwidthLoadPercent%",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (bandwidthLoadPercent > 0) Color(0xFF00E5FF) else Color(0xFF94A3B8),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Progress Bar with Indicator Dot
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    // Track Background
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(Color(0xFF1A263D))
                    )

                    // Active Progress Line
                    val fraction = (bandwidthLoadPercent / 100f).coerceIn(0f, 1f)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(if (fraction < 0.01f) 0.01f else fraction)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color(0xFF00E5FF), Color(0xFF00E676))
                                )
                            )
                    )

                    // Dot Indicator
                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp)
                    ) {
                        val cx = size.width * fraction
                        drawCircle(
                            color = Color(0xFF00E676),
                            radius = 4.dp.toPx(),
                            center = Offset(cx.coerceIn(4.dp.toPx(), size.width - 4.dp.toPx()), size.height / 2f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Connection throughput waveform
                Text(
                    text = "Connection throughput waveform",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF94A3B8),
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Live Waveform Canvas Box
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .testTag("throughput_waveform_box"),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF0B1424),
                    border = BorderStroke(1.dp, Color(0xFF1E2D4A))
                ) {
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        val w = size.width
                        val h = size.height

                        // Baseline mid-line
                        val midY = h * 0.7f

                        if (waveformPoints.isEmpty() || waveformPoints.all { it == 0f }) {
                            // Flat standby line
                            drawLine(
                                color = Color(0xFF1E2D4A),
                                start = Offset(0f, midY),
                                end = Offset(w, midY),
                                strokeWidth = 2.dp.toPx(),
                                cap = StrokeCap.Round
                            )
                        } else {
                            val path = Path()
                            val stepX = w / (waveformPoints.size - 1).coerceAtLeast(1)

                            waveformPoints.forEachIndexed { index, point ->
                                val x = index * stepX
                                val y = h - (point * (h * 0.85f)) - (h * 0.08f)
                                if (index == 0) {
                                    path.moveTo(x, y)
                                } else {
                                    val prevX = (index - 1) * stepX
                                    val prevY = h - (waveformPoints[index - 1] * (h * 0.85f)) - (h * 0.08f)
                                    val cX = (prevX + x) / 2f
                                    path.quadraticBezierTo(prevX, prevY, cX, (prevY + y) / 2f)
                                }
                            }

                            drawPath(
                                path = path,
                                brush = Brush.horizontalGradient(
                                    listOf(Color(0xFF00E5FF), Color(0xFF00E676))
                                ),
                                style = Stroke(
                                    width = 2.5.dp.toPx(),
                                    cap = StrokeCap.Round,
                                    join = StrokeJoin.Round
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun formatTraffic(bytes: Long): String {
    if (bytes <= 0) return "0.0 KB"
    val kb = bytes / 1024.0
    return if (kb >= 1024.0) {
        String.format(Locale.US, "%.1f MB", kb / 1024.0)
    } else {
        String.format(Locale.US, "%.1f KB", kb)
    }
}
