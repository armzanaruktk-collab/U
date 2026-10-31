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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AboutTabContent
import com.example.ui.components.ConnectDashboard
import com.example.ui.components.ConnectionModal
import com.example.ui.components.DeleteProfileDialog
import com.example.ui.components.ExportIniDialog
import com.example.ui.components.ImportIniDialog
import com.example.ui.components.LivePreviewSection
import com.example.ui.components.ModeSelector
import com.example.ui.components.NewProfileDialog
import com.example.ui.components.PayloadSection
import com.example.ui.components.PayloadTemplateDialog
import com.example.ui.components.SettingsTabContent
import com.example.ui.components.SniSection
import com.example.ui.components.SniSuggestionsDialog
import com.example.ui.components.SshSection
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.AccentRed
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
    val context = LocalContext.current
    var showMenu by remember { mutableStateOf(false) }
    var showProfileDropdown by remember { mutableStateOf(false) }
    var showSupportDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is UiEvent.ShowToast -> {
                    snackbarHostState.showSnackbar(event.message)
                }
                else -> {}
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF00E5FF).copy(alpha = 0.15f),
                            border = BorderStroke(1.5.dp, Color(0xFF00E5FF)),
                            modifier = Modifier.size(38.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = Color(0xFF00E5FF),
                                modifier = Modifier
                                    .padding(7.dp)
                                    .size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Foxy VPN",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "SSH TUNNEL • PANTHER CORE",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF94A3B8),
                                fontSize = 10.sp,
                                letterSpacing = 0.8.sp
                            )
                        }
                    }
                },
                actions = {
                    // Support Pill Button
                    Surface(
                        modifier = Modifier
                            .clickable { showSupportDialog = true }
                            .testTag("top_support_button"),
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFF064E3B),
                        border = BorderStroke(1.dp, Color(0xFF00E676).copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Support",
                                tint = Color(0xFF00E676),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Support",
                                color = Color(0xFF00E676),
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold
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
                            Icon(Icons.Default.MoreVert, contentDescription = "Menu", tint = Color.White)
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
                    containerColor = Color(0xFF0A0F1D)
                )
            )
        },
        floatingActionButton = {
            if (uiState.selectedTab == 1 || uiState.hasUnsavedChanges) {
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
            }
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier.testTag("bottom_nav_bar"),
                containerColor = Color(0xFF0B1424)
            ) {
                // Tab 0: Connect
                NavigationBarItem(
                    selected = uiState.selectedTab == 0,
                    onClick = { viewModel.selectTab(0) },
                    icon = { Icon(Icons.Default.Bolt, contentDescription = "Connect") },
                    label = { Text("Connect") },
                    modifier = Modifier.testTag("tab_connect"),
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF0A0F1D),
                        selectedTextColor = Color(0xFF00E676),
                        indicatorColor = Color(0xFF00E676),
                        unselectedIconColor = Color(0xFF64748B),
                        unselectedTextColor = Color(0xFF64748B)
                    )
                )

                // Tab 1: Configurations
                NavigationBarItem(
                    selected = uiState.selectedTab == 1,
                    onClick = { viewModel.selectTab(1) },
                    icon = { Icon(Icons.Default.Tune, contentDescription = "Configurations") },
                    label = { Text("Configurations") },
                    modifier = Modifier.testTag("tab_configurations"),
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF0A0F1D),
                        selectedTextColor = Color(0xFF00E676),
                        indicatorColor = Color(0xFF00E676),
                        unselectedIconColor = Color(0xFF64748B),
                        unselectedTextColor = Color(0xFF64748B)
                    )
                )

                // Tab 2: Settings
                NavigationBarItem(
                    selected = uiState.selectedTab == 2,
                    onClick = { viewModel.selectTab(2) },
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                    label = { Text("Settings") },
                    modifier = Modifier.testTag("tab_settings"),
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF0A0F1D),
                        selectedTextColor = Color(0xFF00E676),
                        indicatorColor = Color(0xFF00E676),
                        unselectedIconColor = Color(0xFF64748B),
                        unselectedTextColor = Color(0xFF64748B)
                    )
                )

                // Tab 3: About
                NavigationBarItem(
                    selected = uiState.selectedTab == 3,
                    onClick = { viewModel.selectTab(3) },
                    icon = { Icon(Icons.Default.Info, contentDescription = "About") },
                    label = { Text("About") },
                    modifier = Modifier.testTag("tab_about"),
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF0A0F1D),
                        selectedTextColor = Color(0xFF00E676),
                        indicatorColor = Color(0xFF00E676),
                        unselectedIconColor = Color(0xFF64748B),
                        unselectedTextColor = Color(0xFF64748B)
                    )
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
                    // TAB 0: CONNECT DASHBOARD & NETWORK TELEMETRY MONITOR
                    item {
                        ConnectDashboard(
                            status = uiState.vpnStatus,
                            location = uiState.location,
                            pingMs = uiState.pingMs,
                            bytesIn = uiState.bytesIn,
                            bytesOut = uiState.bytesOut,
                            downloadSpeedKb = uiState.downloadSpeedKb,
                            uploadSpeedKb = uiState.uploadSpeedKb,
                            bandwidthLoadPercent = uiState.bandwidthLoadPercent,
                            waveformPoints = uiState.waveformPoints,
                            onToggleConnect = {
                                if (uiState.vpnStatus == VpnStatus.CONNECTED) {
                                    viewModel.disconnectVpn(context)
                                } else {
                                    viewModel.openConnectionModal(context)
                                }
                            }
                        )
                    }

                    // System Test / Diagnostics Button (ทดสอบระบบ)
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("quick_diagnostics_card"),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1A2E)),
                            border = BorderStroke(1.dp, Color(0xFF1E2D4A))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "ทดสอบระบบ (Diagnostics Test)",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                        Text(
                                            text = "ทดสอบ Ping และวัดความเร็วเพื่ออัปเดต Telemetry",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color(0xFF94A3B8),
                                            fontSize = 11.sp
                                        )
                                    }

                                    Button(
                                        onClick = { viewModel.testConnection() },
                                        enabled = !uiState.isTestingConnection,
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFF00E5FF),
                                            contentColor = Color(0xFF0A0F1D)
                                        )
                                    ) {
                                        Icon(Icons.Default.Speed, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("ทดสอบ", fontWeight = FontWeight.Bold)
                                    }
                                }

                                if (uiState.connectionTestResult != null) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = uiState.connectionTestResult ?: "",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (uiState.isTestSuccess == true) Color(0xFF00E676) else Color(0xFFFF5252),
                                        lineHeight = 18.sp
                                    )
                                }
                            }
                        }
                    }
                }

                1 -> {
                    // TAB 1: Configurations Editor
                    item {
                        // Profile Switcher Header Card
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("profile_banner_card"),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1A2E)),
                            border = BorderStroke(1.dp, Color(0xFF1E2D4A))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "โปรไฟล์ที่กำลังแก้ไข:",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color(0xFF94A3B8),
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

                                    Spacer(modifier = Modifier.width(10.dp))

                                    // Switch profile button
                                    Box {
                                        Surface(
                                            modifier = Modifier
                                                .clickable { showProfileDropdown = true }
                                                .testTag("profile_switcher_chip"),
                                            shape = RoundedCornerShape(12.dp),
                                            color = Color(0xFF142036),
                                            border = BorderStroke(1.dp, Color(0xFF00E5FF).copy(alpha = 0.4f))
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = "สลับโปรไฟล์",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFF00E5FF)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Icon(
                                                    imageVector = Icons.Default.Tune,
                                                    contentDescription = "Switch profile",
                                                    tint = Color(0xFF00E5FF),
                                                    modifier = Modifier.size(16.dp)
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
                                        color = Color(0xFF94A3B8),
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
                    // TAB 2: Settings & Diagnostics
                    item {
                        SettingsTabContent(
                            config = uiState.editingConfig,
                            isTesting = uiState.isTestingConnection,
                            testResult = uiState.connectionTestResult,
                            isTestSuccess = uiState.isTestSuccess,
                            onAutoReplaceChange = { viewModel.updateAutoReplace(it) },
                            onCompressionChange = { viewModel.updateEnableCompression(it) },
                            onBypassModeChange = { viewModel.updateBypassMode(it) },
                            onRunTest = { viewModel.testConnection() }
                        )
                    }
                }

                3 -> {
                    // TAB 3: About App & Support
                    item {
                        AboutTabContent(
                            onContactSupport = { showSupportDialog = true }
                        )
                    }
                }
            }
        }
    }

    // Support Dialog
    if (showSupportDialog) {
        AlertDialog(
            onDismissRequest = { showSupportDialog = false },
            title = {
                Text(
                    text = "ฝ่ายสนับสนุน (Foxy VPN Support)",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "• SSH TUNNEL: Panther Core v2.4.0\n• Protocol: HTTP Custom Payload & SNI\n• ชุมชนผู้ใช้งาน: Telegram @FoxyVPNTunnel\n• ติดต่อแอดมิน: support@foxyvpn.net\n• สถานะเครือข่าย: พร้อมใช้งาน 24 ชั่วโมง",
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 20.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showSupportDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF))
                ) {
                    Text("ตกลง", color = Color(0xFF0A0F1D), fontWeight = FontWeight.Bold)
                }
            }
        )
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
            onCancel = { viewModel.disconnectVpn(context) },
            onDisconnect = { viewModel.disconnectVpn(context) },
            onCloseModal = { viewModel.closeConnectionModal() }
        )
    }
}
