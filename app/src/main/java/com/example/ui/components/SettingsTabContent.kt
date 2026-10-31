package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Compress
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.VpnConfig

@Composable
fun SettingsTabContent(
    config: VpnConfig,
    isTesting: Boolean,
    testResult: String?,
    isTestSuccess: Boolean?,
    onAutoReplaceChange: (Boolean) -> Unit,
    onCompressionChange: (Boolean) -> Unit,
    onBypassModeChange: (Boolean) -> Unit,
    onRunTest: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // System Test (ทดสอบระบบ) Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("system_test_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1A2E)),
            border = BorderStroke(1.dp, Color(0xFF1E2D4A))
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
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
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "ทดสอบระบบ (Diagnostics)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "วัดค่า Latency, DNS และ Ping ของเซิร์ฟเวอร์",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF94A3B8),
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = onRunTest,
                    enabled = !isTesting,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("run_diagnostics_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF00E5FF),
                        contentColor = Color(0xFF0A0F1D)
                    )
                ) {
                    if (isTesting) {
                        CircularProgressIndicator(
                            color = Color(0xFF0A0F1D),
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("กำลังทดสอบระบบ...", fontWeight = FontWeight.Bold)
                    } else {
                        Icon(Icons.Default.Speed, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("เริ่มการทดสอบระบบ", fontWeight = FontWeight.Bold)
                    }
                }

                if (testResult != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = testResult,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isTestSuccess == true) Color(0xFF00E676) else Color(0xFFFF5252),
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // Protocol Options
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1A2E)),
            border = BorderStroke(1.dp, Color(0xFF1E2D4A))
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "ตัวเลือกโปรโตคอลและการเชื่อมต่อ",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Safe Split Tunnel (ป้องกันเน็ตหลุด)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Dns, contentDescription = null, tint = Color(0xFF00E676), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("ป้องกันเน็ตหลุด (Safe Split Tunnel)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("เปิดไว้เพื่อให้เน็ตมือถือและแอปอื่น (YouTube, Line, Chrome) ใช้งานได้ปกติ ไม่หลุดขณะเชื่อมต่อ VPN", color = Color(0xFF00E676), fontSize = 11.sp)
                        }
                    }
                    Switch(
                        checked = config.bypassMode,
                        onCheckedChange = onBypassModeChange,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color(0xFF0A0F1D),
                            checkedTrackColor = Color(0xFF00E676)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Auto Replace
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Sync, contentDescription = null, tint = Color(0xFF00E5FF), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("แทนที่แท็กอัตโนมัติ (auto_replace)", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            Text("แทนที่ [crlf], [host], [ua] ในเพย์โหลดอัตโนมัติ", color = Color(0xFF94A3B8), fontSize = 11.sp)
                        }
                    }
                    Switch(
                        checked = config.autoReplace,
                        onCheckedChange = onAutoReplaceChange,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color(0xFF0A0F1D),
                            checkedTrackColor = Color(0xFF00E5FF)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Compression
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Compress, contentDescription = null, tint = Color(0xFF00E5FF), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("การบีบอัด SSH (Compression)", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            Text("ลดปริมาณการรับส่งข้อมูลผ่าน SSH", color = Color(0xFF94A3B8), fontSize = 11.sp)
                        }
                    }
                    Switch(
                        checked = config.enableCompression,
                        onCheckedChange = onCompressionChange,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color(0xFF0A0F1D),
                            checkedTrackColor = Color(0xFF00E5FF)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // DNS Info
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Dns, contentDescription = null, tint = Color(0xFF00E5FF), modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("เซิร์ฟเวอร์ DNS ของอุโมงค์ VPN", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        Text("1.1.1.1 (Cloudflare) / 8.8.8.8 (Google)", color = Color(0xFF00E676), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
