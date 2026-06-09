package com.kids.tangshi.ui.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kids.tangshi.data.Poem

/**
 * 诗词详情页
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    poem: Poem,
    ttsHelper: com.kids.tangshi.util.TtsHelper? = null,
    isSpeaking: Boolean = false,
    onIsSpeakingChanged: (Boolean) -> Unit = {},
    isFavorite: Boolean = false,
    onFavoriteClick: () -> Unit = {},
    onBackClick: () -> Unit = {},
    onStudyComplete: () -> Unit = {}
) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = poem.title) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Text("←", style = MaterialTheme.typography.titleMedium)
                    }
                },
                actions = {
                    IconButton(onClick = onFavoriteClick) {
                        Text(
                            text = if (isFavorite) "❤️" else "🤍",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    IconButton(onClick = {
                        // 分享：复制诗词到剪贴板
                        val shareText = "${poem.title}\n〔${poem.dynasty}〕${poem.author}\n\n${poem.content}"
                        val clipboardManager = androidx.compose.ui.platform.ClipboardManager.current
                        clipboardManager.setText(shareText)
                    }) {
                        Text("📋", style = MaterialTheme.typography.titleMedium)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            if (isSpeaking) {
                                ttsHelper?.stop()
                                onIsSpeakingChanged(false)
                            } else {
                                ttsHelper?.speak(poem.content)
                                onIsSpeakingChanged(true)
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(if (isSpeaking) "⏹ 停止朗读" else "📖 朗读")
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(
                        onClick = onStudyComplete,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("✅ 学完了")
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = "📖 ${poem.title}",
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "〔${poem.dynasty}〕${poem.author}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surfaceVariant
            )
            Text(
                text = poem.content,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                lineHeight = 32.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (poem.translation.isNotEmpty()) {
                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surfaceVariant
                )
                Text(
                    text = "📝 译文",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = poem.translation,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Start,
                    lineHeight = 28.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
