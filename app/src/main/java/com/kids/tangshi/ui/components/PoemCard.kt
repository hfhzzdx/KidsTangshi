package com.kids.tangshi.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.kids.tangshi.data.Poem
import com.kids.tangshi.ui.theme.CardBackground1
import com.kids.tangshi.ui.theme.CardBackground2
import com.kids.tangshi.ui.theme.CardBackground3

/**
 * 诗词卡片组件 - 列表项，紧凑显示
 */
@Composable
fun PoemCard(
    poem: Poem,
    onClick: () -> Unit,
    onFavoriteClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = when (poem.dynasty) {
                "唐" -> CardBackground1
                "宋" -> CardBackground2
                else -> CardBackground3
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                // 标题 + 作者
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = poem.title,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "〔${poem.dynasty}〕${poem.author}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                // 内容预览：最多 2 行
                Text(
                    text = poem.content.replace("\n", " ").take(50) +
                        if (poem.content.length > 50) "…" else "",
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            // 点击提示
            Text(
                text = "▶",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

/**
 * 每日推荐卡片（大卡片）
 */
@Composable
fun DailyPoemCard(
    poem: Poem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "📖 今日推荐",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = poem.title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = "〔${poem.dynasty}〕${poem.author}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            // 预览截断
            Text(
                text = poem.content.replace("\n", " ").take(80) +
                    if (poem.content.length > 80) "…" else "",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "点击查看全文 →",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

/**
 * 学习进度卡片
 */
@Composable
fun StudyProgressCard(
    completedCount: Int,
    totalCount: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "学习进度",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { if (totalCount > 0) completedCount.toFloat() / totalCount.toFloat() else 0f },
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.secondary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "$completedCount / $totalCount",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}
