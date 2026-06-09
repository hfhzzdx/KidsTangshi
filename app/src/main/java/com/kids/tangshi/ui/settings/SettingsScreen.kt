package com.kids.tangshi.ui.settings

import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.io.File
import java.io.FileOutputStream

/**
 * 设置页
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    ttsHelper: com.kids.tangshi.util.TtsHelper? = null,
    onBackClick: () -> Unit = {},
    onRequestImport: (((Uri) -> Unit) -> Unit)? = null
) {
    var speechRate by remember { mutableStateOf(1.0f) }
    var selectedTheme by remember { mutableStateOf("light") }
    val context = LocalContext.current
    var importStatus by remember { mutableStateOf("") }

    // 处理文件选择回调
    val importCallback: (Uri) -> Unit = { uri ->
        try {
            context.contentResolver.openInputStream(uri)?.use { input ->
                val fileName = uri.lastPathSegment ?: "custom_poems.json"
                val jsonText = input.bufferedReader().readText()
                org.json.JSONArray(jsonText)

                val poemsDir = File(context.filesDir, "poems")
                if (!poemsDir.exists()) poemsDir.mkdirs()
                val outFile = File(poemsDir, fileName)
                FileOutputStream(outFile).use { output ->
                    output.write(jsonText.toByteArray(Charsets.UTF_8))
                }
                importStatus = "✅ 导入成功: ${outFile.absolutePath}"
                Toast.makeText(context, "导入成功: $fileName", Toast.LENGTH_SHORT).show()
            } ?: run {
                importStatus = "❌ 无法读取文件"
            }
        } catch (e: Exception) {
            importStatus = "❌ 导入失败: ${e.message}"
            Toast.makeText(context, "导入失败", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("⚙️ 设置") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Text("<", style = MaterialTheme.typography.titleMedium)
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
            // TTS 语速 + 诊断
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
                            Text("−", style = MaterialTheme.typography.titleLarge)
                        }
                        Text("%.2fx".format(speechRate), style = MaterialTheme.typography.bodyMedium)
                        IconButton(onClick = {
                            if (speechRate < 2.0f) {
                                speechRate += 0.25f
                                ttsHelper?.setSpeechRate(speechRate)
                            }
                        }) {
                            Text("+", style = MaterialTheme.typography.titleLarge)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))

                val hasChinese = ttsHelper?.isChineseAvailable() ?: false
                val langStatus = ttsHelper?.getLanguageStatus() ?: "未知"
                Text(
                    "中文支持: ${if (hasChinese) "✅ 可用" else "⚠️ 不可用 ($langStatus)"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (hasChinese) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(8.dp))

                // 没有中文支持时，显示引导卡片
                if (!hasChinese) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("⚠️ 未检测到中文 TTS，请尝试：",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("1. 设置 → 小爱同学 → 语音设置 → 朗读引擎",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                            Text("2. 设置 → 声音和振动 → 朗读文字",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                            Text("3. 设置 → 应用设置 → 应用管理 → 搜索\"TTS\"",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                OutlinedButton(
                    onClick = {
                        try {
                            val intents = listOf(
                                Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS),
                                Intent(Settings.ACTION_SETTINGS)
                            )
                            for (intent in intents) {
                                try {
                                    context.startActivity(intent)
                                    Toast.makeText(context, "请找到『文字转语音』选项", Toast.LENGTH_LONG).show()
                                    break
                                } catch (_: Exception) { }
                            }
                        } catch (e: Exception) {
                            Toast.makeText(context, "请手动打开设置 → 辅助功能 → 文字转语音", Toast.LENGTH_LONG).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("🔧 打开文字转语音设置")
                }
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedButton(
                    onClick = {
                        if (ttsHelper?.isInitialized() != true) {
                            Toast.makeText(context, "TTS 正在初始化，请稍候再试", Toast.LENGTH_SHORT).show()
                            return@OutlinedButton
                        }
                        val result = ttsHelper?.speak("床前明月光，疑是地上霜。") ?: -99
                        val msg = when (result) {
                            0 -> "朗读请求已发送(0)"
                            -1 -> "朗读失败(-1ERROR)"
                            -2 -> "朗读失败(-2SERVER_ERROR)"
                            else -> "朗读失败($result 未初始化)"
                        }
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("🔊 测试朗读")
                }
            }

            HorizontalDivider()

            // 导入诗词数据
            SettingsSection(title = "📂 导入诗词数据") {
                Text(
                    "选择 JSON 文件导入自定义诗词数据",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        onRequestImport?.invoke(importCallback)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("选择 JSON 文件")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "JSON 格式示例：\n[{\"id\":\"1\",\"title\":\"静夜思\",\"author\":\"李白\",\"dynasty\":\"唐\",\"content\":\"床前明月光...\",\"translation\":\"...\"}]",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (importStatus.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = importStatus,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
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
