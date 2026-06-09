package com.kids.tangshi.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Remove
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 设置页
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    ttsHelper: com.kids.tangshi.util.TtsHelper? = null,
    onBackClick: () -> Unit = {}
) {
    var speechRate by remember { mutableStateOf(1.0f) }
    var selectedTheme by remember { mutableStateOf("light") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("⚙️ 设置") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // TTS 语速
            SettingsSection(title = "🔊 朗读设置") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("朗读语速", style = MaterialTheme.typography.bodyLarge)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconButton(onClick = {
                            if (speechRate > 0.5f) {
                                speechRate -= 0.25f
                                ttsHelper?.setSpeechRate(speechRate)
                            }
                        }) {
                            Icon(Icons.AutoMirrored.Filled.Remove, contentDescription = "减慢")
                        }
                        Text("%.2fx".format(speechRate), style = MaterialTheme.typography.bodyMedium)
                        IconButton(onClick = {
                            if (speechRate < 2.0f) {
                                speechRate += 0.25f
                                ttsHelper?.setSpeechRate(speechRate)
                            }
                        }) {
                            Icon(Icons.Default.Add, contentDescription = "加快")
                        }
                    }
                }
            }

            HorizontalDivider()

            // 主题
            SettingsSection(title = "🎨 主题设置") {
                val themes = listOf("light" to "☀️ 浅色", "dark" to "🌙 深色", "system" to "📱 跟随系统")
                themes.forEach { (theme, label) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(selected = selectedTheme == theme, onClick = { selectedTheme = theme })
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = selectedTheme == theme, onClick = { selectedTheme = theme })
                        Text(label, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(start = 8.dp))
                    }
                }
            }

            HorizontalDivider()

            // 关于
            SettingsSection(title = "ℹ️ 关于") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("版本", style = MaterialTheme.typography.bodyLarge)
                    Text("1.0.0", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("诗词数量", style = MaterialTheme.typography.bodyLarge)
                    Text("50+ 首", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(12.dp))
        content()
    }
}
