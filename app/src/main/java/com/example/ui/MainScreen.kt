package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.ConnectionModal
import com.example.ui.components.DeleteProfileDialog
import com.example.ui.components.ExportIniDialog
import com.example.ui.components.ImportIniDialog
import com.example.ui.components.LivePreviewSection
import com.example.ui.components.ModeSelector
import com.example.ui.components.NewProfileDialog
import com.example.ui.components.PayloadSection
import com.example.ui.components.PayloadTemplateDialog
import com.example.ui.components.SniSection
import com.example.ui.components.SniSuggestionsDialog
import com.example.ui.components.SshSection
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.AccentRed
import com.example.ui.theme.CyberCyan
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: VpnConfigViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showMenu by remember { mutableStateOf(false) }
    var showProfileDropdown by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is UiEvent.ShowToast -> {
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .padding(6.dp)
                                    .size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "VPN Settings",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "HTTP Custom Config Manager",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            )
                        }
                    }
                },
                actions = {
                    // Profile Switcher Chip in Top Bar
                    Box {
                        Surface(
                            modifier = Modifier
                                .clickable { showProfileDropdown = true }
                                .testTag("profile_switcher_button"),
                            shape = RoundedCornerShape(20.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = uiState.editingConfig.name,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.widthIn(max = 120.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.Tune,
                                    contentDescription = "Switch profile",
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = showProfileDropdown,
                            onDismissRequest = { showProfileDropdown = false }
                        ) {
                            Text(
                                text = "เลือกโปรไฟล์:",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            uiState.profiles.forEach { profile ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(
                                                text = profile.name,
                                                fontWeight = if (profile.id == uiState.activeProfileId) FontWeight.Bold else FontWeight.Normal,
                                                color = if (profile.id == uiState.activeProfileId) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = "โหมด ${profile.connectionMode} • ${if (profile.sshHost.isNotBlank()) profile.sshHost else "ไม่ได้ระบุ Host"}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                fontSize = 11.sp
                                            )
                                        }
                                    },
                                    onClick = {
                                        viewModel.switchProfile(profile.id)
                                        showProfileDropdown = false
                                    }
                                )
                            }
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Add, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("+ เพิ่มโปรไฟล์ใหม่", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                                    }
                                },
                                onClick = {
                                    showProfileDropdown = false
                                    viewModel.setShowNewProfileDialog(true)
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    // Overflow Menu
                    Box {
                        IconButton(
                            onClick = { showMenu = true },
                            modifier = Modifier.testTag("overflow_menu_button")
                        ) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                        }

                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("สร้างโปรไฟล์ใหม่") },
                                leadingIcon = { Icon(Icons.Default.Add, contentDescription = null) },
                                onClick = {
                                    showMenu = false
                                    viewModel.setShowNewProfileDialog(true)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("คัดลอกโปรไฟล์ปัจจุบัน") },
                                leadingIcon = { Icon(Icons.Default.ContentCopy, contentDescription = null) },
                                onClick = {
                                    showMenu = false
                                    viewModel.duplicateCurrentProfile()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("นำเข้าจาก settings.ini") },
                                leadingIcon = { Icon(Icons.Default.FileUpload, contentDescription = null) },
                                onClick = {
                                    showMenu = false
                                    viewModel.setShowImportDialog(true)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("ส่งออกเป็น settings.ini") },
                                leadingIcon = { Icon(Icons.Default.FileDownload, contentDescription = null) },
                                onClick = {
                                    showMenu = false
                                    viewModel.setShowExportDialog(true)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("คืนค่าเริ่มต้น") },
                                leadingIcon = { Icon(Icons.Default.RestartAlt, contentDescription = null) },
                                onClick = {
                                    showMenu = false
                                    viewModel.resetToDefaults()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("ลบโปรไฟล์ปัจจุบัน", color = AccentRed) },
                                leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = AccentRed) },
                                onClick = {
                                    showMenu = false
                                    viewModel.setShowDeleteConfirmDialog(true)
                                }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { viewModel.saveChanges() },
                modifier = Modifier
                    .testTag("save_settings_fab")
                    .navigationBarsPadding(),
                containerColor = if (uiState.hasUnsavedChanges) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer,
                contentColor = if (uiState.hasUnsavedChanges) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondaryContainer,
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 4.dp),
                icon = {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = "Save settings"
                    )
                },
                text = {
                    Text(
                        text = if (uiState.hasUnsavedChanges) "บันทึกการตั้งค่า *" else "บันทึกแล้ว",
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier.testTag("bottom_nav_bar"),
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                NavigationBarItem(
                    selected = uiState.selectedTab == 0,
                    onClick = { viewModel.selectTab(0) },
                    icon = { Icon(Icons.Default.Tune, contentDescription = "Editor") },
                    label = { Text("แก้ไขการตั้งค่า") },
                    modifier = Modifier.testTag("tab_editor")
                )
                NavigationBarItem(
                    selected = uiState.selectedTab == 1,
                    onClick = { viewModel.selectTab(1) },
                    icon = { Icon(Icons.Default.Terminal, contentDescription = "Preview") },
                    label = { Text("พรีวิวเพย์โหลด") },
                    modifier = Modifier.testTag("tab_preview")
                )
                NavigationBarItem(
                    selected = uiState.selectedTab == 2,
                    onClick = { viewModel.selectTab(2) },
                    icon = { Icon(Icons.Default.Speed, contentDescription = "Diagnostics") },
                    label = { Text("ทดสอบระบบ") },
                    modifier = Modifier.testTag("tab_diagnostics")
                )
            }
        }
    ) { innerPadding ->
        val listState = rememberLazyListState()

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (uiState.selectedTab) {
                0 -> {
                    // TAB 0: Settings Editor
                    item {
                        // HERO CONNECT CARD (With Glowing Animation Trigger)
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("vpn_connect_hero_card"),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF0F1A2E)
                            ),
                            border = BorderStroke(
                                1.5.dp,
                                if (uiState.vpnStatus == VpnStatus.CONNECTED) Color(0xFF00E676)
                                else if (uiState.vpnStatus == VpnStatus.CONNECTING) Color(0xFF00E5FF)
                                else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = if (uiState.vpnStatus == VpnStatus.CONNECTED) Color(0xFF00E676).copy(alpha = 0.2f)
                                               else if (uiState.vpnStatus == VpnStatus.CONNECTING) Color(0xFF00E5FF).copy(alpha = 0.2f)
                                               else Color.White.copy(alpha = 0.08f),
                                        border = BorderStroke(
                                            2.dp,
                                            if (uiState.vpnStatus == VpnStatus.CONNECTED) Color(0xFF00E676)
                                            else if (uiState.vpnStatus == VpnStatus.CONNECTING) Color(0xFF00E5FF)
                                            else Color.White.copy(alpha = 0.2f)
                                        ),
                                        modifier = Modifier.size(52.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = Icons.Default.Security,
                                                contentDescription = null,
                                                tint = if (uiState.vpnStatus == VpnStatus.CONNECTED) Color(0xFF00E676)
                                                       else if (uiState.vpnStatus == VpnStatus.CONNECTING) Color(0xFF00E5FF)
                                                       else Color.White.copy(alpha = 0.7f),
                                                modifier = Modifier.size(28.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(14.dp))

                                    Column {
                                        Text(
                                            text = when (uiState.vpnStatus) {
                                                VpnStatus.CONNECTED -> "เชื่อมต่อแล้ว (Connected)"
                                                VpnStatus.CONNECTING -> "กำลังเชื่อมต่อ..."
                                                VpnStatus.DISCONNECTED -> "ไม่ได้เชื่อมต่อ VPN"
                                            },
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                        Text(
                                            text = if (uiState.vpnStatus == VpnStatus.CONNECTED) {
                                                val mins = uiState.connectedSeconds / 60
                                                val secs = uiState.connectedSeconds % 60
                                                String.format(Locale.getDefault(), "%02d:%02d • %s", mins, secs, uiState.editingConfig.sshHost.ifBlank { "VPN Tunnel" })
                                            } else {
                                                "โหมด ${uiState.editingConfig.connectionMode} • ${uiState.editingConfig.sshHost.ifBlank { "ยังไม่ระบุ Host" }}"
                                            },
                                            style = MaterialTheme.typography.bodySmall,
                                            color = if (uiState.vpnStatus == VpnStatus.CONNECTED) Color(0xFF00E676) else MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontSize = 12.sp
                                        )
                                    }
                                }

                                Button(
                                    onClick = { viewModel.openConnectionModal() },
                                    modifier = Modifier.testTag("hero_connect_button"),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (uiState.vpnStatus == VpnStatus.CONNECTED) Color(0xFF00E676)
                                                         else if (uiState.vpnStatus == VpnStatus.CONNECTING) Color(0xFF00E5FF)
                                                         else Color(0xFF0284C7)
                                    )
                                ) {
                                    Icon(
                                        imageVector = if (uiState.vpnStatus == VpnStatus.CONNECTED) Icons.Default.Stop else Icons.Default.PlayArrow,
                                        contentDescription = null,
                                        tint = Color(0xFF0A0F1D),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (uiState.vpnStatus == VpnStatus.CONNECTED) "จัดการ" else "เชื่อมต่อ",
                                        color = Color(0xFF0A0F1D),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    item {
                        // Profile Banner Card
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("profile_banner_card"),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            ),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "โปรไฟล์ที่กำลังแก้ไข:",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontSize = 11.sp
                                        )
                                        OutlinedTextField(
                                            value = uiState.editingConfig.name,
                                            onValueChange = { viewModel.updateProfileName(it) },
                                            singleLine = true,
                                            label = { Text("ชื่อโปรไฟล์") },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .testTag("profile_name_edit_input"),
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val dateStr = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                                        .format(Date(uiState.editingConfig.updatedAt))
                                    Text(
                                        text = "อัปเดตล่าสุด: $dateStr",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 11.sp
                                    )

                                    if (uiState.hasUnsavedChanges) {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = MaterialTheme.colorScheme.error.copy(alpha = 0.15f)
                                        ) {
                                            Text(
                                                text = "ยังไม่ได้บันทึก",
                                                color = MaterialTheme.colorScheme.error,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                            )
                                        }
                                    } else {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = AccentGreen.copy(alpha = 0.15f)
                                        ) {
                                            Text(
                                                text = "บันทึกในแอปแล้ว",
                                                color = AccentGreen,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    item {
                        ModeSelector(
                            selectedMode = uiState.editingConfig.connectionMode,
                            autoReplace = uiState.editingConfig.autoReplace,
                            onModeSelected = { viewModel.updateConnectionMode(it) },
                            onAutoReplaceChanged = { viewModel.updateAutoReplace(it) }
                        )
                    }

                    item {
                        SshSection(
                            config = uiState.editingConfig,
                            showPassword = uiState.showPassword,
                            onHostChange = { viewModel.updateSshHost(it) },
                            onPortChange = { viewModel.updateSshPort(it) },
                            onUsernameChange = { viewModel.updateSshUsername(it) },
                            onPasswordChange = { viewModel.updateSshPassword(it) },
                            onCompressionChange = { viewModel.updateEnableCompression(it) },
                            onAuthMethodChange = { viewModel.updateAuthMethod(it) },
                            onPublicKeyChange = { viewModel.updatePublicKey(it) },
                            onTogglePasswordVisibility = { viewModel.togglePasswordVisibility() }
                        )
                    }

                    item {
                        PayloadSection(
                            config = uiState.editingConfig,
                            onPayloadChange = { viewModel.updatePayload(it) },
                            onInsertTag = { viewModel.insertTagToPayload(it) },
                            onProxyIpChange = { viewModel.updateProxyIp(it) },
                            onProxyPortChange = { viewModel.updateProxyPort(it) },
                            onOpenTemplates = { viewModel.setShowTemplateDialog(true) }
                        )
                    }

                    item {
                        SniSection(
                            config = uiState.editingConfig,
                            onSniChange = { viewModel.updateSniServerName(it) },
                            onOpenSuggestions = { viewModel.setShowSniSuggestionsDialog(true) }
                        )
                    }
                }

                1 -> {
                    // TAB 1: Live Payload & INI Preview
                    item {
                        LivePreviewSection(
                            config = uiState.editingConfig,
                            isTestingConnection = uiState.isTestingConnection,
                            testResult = uiState.connectionTestResult,
                            isTestSuccess = uiState.isTestSuccess,
                            onRunTest = { viewModel.testConnection() }
                        )
                    }
                }

                2 -> {
                    // TAB 2: Diagnostics & Connection Tests
                    item {
                        LivePreviewSection(
                            config = uiState.editingConfig,
                            isTestingConnection = uiState.isTestingConnection,
                            testResult = uiState.connectionTestResult,
                            isTestSuccess = uiState.isTestSuccess,
                            onRunTest = { viewModel.testConnection() }
                        )
                    }
                }
            }
        }
    }

    // Modal Dialogs
    if (uiState.showNewProfileDialog) {
        NewProfileDialog(
            onDismiss = { viewModel.setShowNewProfileDialog(false) },
            onConfirm = { viewModel.createNewProfile(it) }
        )
    }

    if (uiState.showImportDialog) {
        ImportIniDialog(
            onDismiss = { viewModel.setShowImportDialog(false) },
            onImport = { iniText, name -> viewModel.importFromIni(iniText, name) }
        )
    }

    if (uiState.showExportDialog) {
        ExportIniDialog(
            config = uiState.editingConfig,
            onDismiss = { viewModel.setShowExportDialog(false) }
        )
    }

    if (uiState.showDeleteConfirmDialog) {
        DeleteProfileDialog(
            profileName = uiState.editingConfig.name,
            onDismiss = { viewModel.setShowDeleteConfirmDialog(false) },
            onConfirm = { viewModel.deleteCurrentProfile() }
        )
    }

    if (uiState.showTemplateDialog) {
        PayloadTemplateDialog(
            onDismiss = { viewModel.setShowTemplateDialog(false) },
            onSelectTemplate = { payload, mode -> viewModel.applyPayloadTemplate(payload, mode) }
        )
    }

    if (uiState.showSniSuggestionsDialog) {
        SniSuggestionsDialog(
            onDismiss = { viewModel.setShowSniSuggestionsDialog(false) },
            onSelectSni = { viewModel.applySniSuggestion(it) }
        )
    }

    if (uiState.showConnectionModal) {
        ConnectionModal(
            status = uiState.vpnStatus,
            step = uiState.connectionStep,
            statusText = uiState.connectionStatusText,
            connectedSeconds = uiState.connectedSeconds,
            host = uiState.editingConfig.sshHost,
            port = uiState.editingConfig.sshPort,
            logs = uiState.connectionLogs,
            onCancel = { viewModel.disconnectVpn() },
            onDisconnect = { viewModel.disconnectVpn() },
            onCloseModal = { viewModel.closeConnectionModal() }
        )
    }
}
