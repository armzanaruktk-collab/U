package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.VpnConfig
import com.example.ui.theme.AccentRed
import com.example.ui.theme.CyberCyan

data class PayloadTemplate(
    val title: String,
    val description: String,
    val recommendedMode: Int,
    val payload: String
)

data class SniSuggestion(
    val domain: String,
    val category: String,
    val description: String
)

@Composable
fun NewProfileDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var profileName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("สร้างโปรไฟล์ใหม่", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text(
                    text = "ระบุชื่อโปรไฟล์การตั้งค่า (เช่น True 5G Free, Cloudflare SSL, Work SSH):",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = profileName,
                    onValueChange = { profileName = it },
                    label = { Text("ชื่อโปรไฟล์") },
                    placeholder = { Text("เช่น โปรไฟล์ที่ 2") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("new_profile_name_input"),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(profileName) },
                enabled = profileName.isNotBlank(),
                modifier = Modifier.testTag("confirm_create_profile_button"),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("สร้างโปรไฟล์")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("ยกเลิก")
            }
        }
    )
}

@Composable
fun ImportIniDialog(
    onDismiss: () -> Unit,
    onImport: (String, String) -> Unit
) {
    var iniText by remember { mutableStateOf("") }
    var profileName by remember { mutableStateOf("โปรไฟล์นำเข้า") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.FileUpload, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("นำเข้าจาก settings.ini", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column {
                Text(
                    text = "วางข้อความเนื้อหาจากไฟล์ settings.ini เพื่อแปลงเป็นข้อมูลในแอปโดยอัตโนมัติ:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = profileName,
                    onValueChange = { profileName = it },
                    label = { Text("ชื่อโปรไฟล์ใหม่") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = iniText,
                    onValueChange = { iniText = it },
                    label = { Text("เนื้อหา settings.ini") },
                    placeholder = { Text("[mode]\nconnection_mode = 1\n...") },
                    minLines = 6,
                    maxLines = 10,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("import_ini_text_input"),
                    shape = RoundedCornerShape(10.dp),
                    textStyle = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onImport(iniText, profileName) },
                enabled = iniText.isNotBlank(),
                modifier = Modifier.testTag("confirm_import_ini_button"),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("นำเข้า")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("ยกเลิก")
            }
        }
    )
}

@Composable
fun ExportIniDialog(
    config: VpnConfig,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val iniContent = remember(config) { config.toIniString() }
    var copied by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.FileDownload, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("ส่งออกการตั้งค่า", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column {
                Text(
                    text = "ข้อมูลในรูปแบบ settings.ini มาตรฐาน สำหรับคัดลอกไปใช้ในเซิร์ฟเวอร์หรือแชร์ให้ผู้อื่น:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                ) {
                    Text(
                        text = iniContent,
                        modifier = Modifier.padding(10.dp),
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        },
        confirmButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = {
                        val sendIntent: Intent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, iniContent)
                            type = "text/plain"
                        }
                        val shareIntent = Intent.createChooser(sendIntent, "แชร์การตั้งค่า VPN")
                        context.startActivity(shareIntent)
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("แชร์")
                }
                Button(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("settings.ini", iniContent))
                        copied = true
                    },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("copy_export_button")
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (copied) "คัดลอกแล้ว" else "คัดลอก")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("ปิด")
            }
        }
    )
}

@Composable
fun DeleteProfileDialog(
    profileName: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Delete, contentDescription = null, tint = AccentRed)
                Spacer(modifier = Modifier.width(8.dp))
                Text("ยืนยันการลบโปรไฟล์", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Text(
                text = "คุณแน่ใจหรือไม่ว่าต้องการลบโปรไฟล์ '$profileName' ? การดำเนินการนี้ไม่สามารถยกเลิกได้",
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = AccentRed),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("confirm_delete_profile_button")
            ) {
                Text("ลบโปรไฟล์")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("ยกเลิก")
            }
        }
    )
}

@Composable
fun PayloadTemplateDialog(
    onDismiss: () -> Unit,
    onSelectTemplate: (String, Int) -> Unit
) {
    val templates = listOf(
        PayloadTemplate(
            title = "WebSocket Cloudflare CDN (มาตรฐาน)",
            description = "GET / HTTP/1.1 พร้อม Upgrade: websocket และ Dalvik UA นิยมใช้กับ CDN",
            recommendedMode = 1,
            payload = "GET / HTTP/1.1[crlf]Host: [host][crlf]Connection: Upgrade[crlf]User-Agent: [ua][crlf]Upgrade: websocket[crlf][crlf]"
        ),
        PayloadTemplate(
            title = "HTTP Direct CONNECT",
            description = "คำสั่ง CONNECT ตรงไปยัง host และ port รองรับ Squid Proxy",
            recommendedMode = 1,
            payload = "CONNECT [host_port] HTTP/1.0[crlf]Host: [host][crlf][crlf]"
        ),
        PayloadTemplate(
            title = "Front Domain / SNI Host Inject",
            description = "ส่ง X-Online-Host และ Bug Host ในส่วนหัว HTTP",
            recommendedMode = 3,
            payload = "GET / HTTP/1.1[crlf]Host: [host][crlf]X-Online-Host: [host][crlf]Connection: Keep-Alive[crlf]User-Agent: [ua][crlf][crlf]"
        ),
        PayloadTemplate(
            title = "Instant Split Payload (แยกแพ็กเก็ตทันที)",
            description = "ใช้แท็ก [split] เพื่อแยกส่ง Payload สองส่วนเพื่อหลบหลีกการตรวจสอบ",
            recommendedMode = 1,
            payload = "GET / HTTP/1.1[crlf]Host: [host][crlf][split]Connection: Upgrade[crlf]Upgrade: websocket[crlf][crlf]"
        ),
        PayloadTemplate(
            title = "Delay Split Payload (แยกแพ็กเก็ตหน่วง 0.5s)",
            description = "ใช้ [delay_split] หน่วงเวลา 0.5 วินาทีก่อนส่งแพ็กเก็ตส่วนที่สอง",
            recommendedMode = 1,
            payload = "GET / HTTP/1.1[crlf]Host: [host][crlf][delay_split]User-Agent: [ua][crlf]Connection: Upgrade[crlf][crlf]"
        )
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("เลือกเทมเพลตเพย์โหลด", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(templates) { item ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectTemplate(item.payload, item.recommendedMode) },
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = item.title,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "โหมด ${item.recommendedMode}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = item.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 12.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = item.payload,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.5.sp
                                ),
                                maxLines = 2,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("ปิด")
            }
        }
    )
}

@Composable
fun SniSuggestionsDialog(
    onDismiss: () -> Unit,
    onSelectSni: (String) -> Unit
) {
    val suggestions = listOf(
        SniSuggestion("fast.com", "CDN / Speedtest", "Netflix CDN / Speedtest"),
        SniSuggestion("cdn.cloudflare.com", "Cloudflare", "Cloudflare CDN Edge Network"),
        SniSuggestion("m.facebook.com", "Social Media", "Meta / Facebook Mobile Edge"),
        SniSuggestion("line.naver.jp", "Chat App", "LINE Application Server"),
        SniSuggestion("zoom.us", "Video Meeting", "Zoom Video Communications"),
        SniSuggestion("edge.microsoft.com", "Microsoft", "Microsoft Edge Cloud CDN"),
        SniSuggestion("v16-webapp.tiktok.com", "TikTok", "TikTok Media Edge"),
        SniSuggestion("api.twitter.com", "Twitter / X", "X / Twitter API Edge")
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Language, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("SNI / Bug Host แนะนำ", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(suggestions) { item ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectSni(item.domain) },
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = item.domain,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace),
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = item.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp
                                )
                            }
                            Text(
                                text = item.category,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("ปิด")
            }
        }
    )
}
