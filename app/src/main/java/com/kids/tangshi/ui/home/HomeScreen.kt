package com.kids.tangshi.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kids.tangshi.data.Poem
import com.kids.tangshi.data.PoemCategory
import com.kids.tangshi.data.PoemRepository
import com.kids.tangshi.ui.components.DailyPoemCard
import com.kids.tangshi.ui.components.PoemCard
import com.kids.tangshi.ui.components.StudyProgressCard
import kotlinx.coroutines.launch

/**
 * 首页界面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onPoemClick: (Poem) -> Unit = {},
    onSettingsClick: () -> Unit = {}
) {
    var selectedCategory by remember { mutableStateOf(PoemCategory.TANG) }
    var searchQuery by remember { mutableStateOf("") }
    var showSearch by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val repository = remember { PoemRepository(context) }

    var tangPoems by remember { mutableStateOf(listOf<Poem>()) }
    var songPoems by remember { mutableStateOf(listOf<Poem>()) }
    var dailyPoem by remember { mutableStateOf<Poem?>(null) }
    var studiedCount by remember { mutableStateOf(0) }

    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        tangPoems = repository.loadTangPoems()
        songPoems = repository.loadSongPoems()
        dailyPoem = repository.getDailyPoem()
    }

    val currentPoems = when (selectedCategory) {
        PoemCategory.TANG -> tangPoems
        PoemCategory.SONG -> songPoems
        PoemCategory.THREE_HUNDRED -> tangPoems + songPoems
    }

    val filteredPoems = if (searchQuery.isEmpty()) {
        currentPoems
    } else {
        currentPoems.filter {
            it.title.contains(searchQuery, ignoreCase = true) ||
            it.author.contains(searchQuery, ignoreCase = true)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "📚 幼儿学诗词",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                actions = {
                    IconButton(onClick = { showSearch = !showSearch }) {
                    Text("🔍", style = MaterialTheme.typography.titleLarge)
                    }
                    IconButton(onClick = onSettingsClick) {
                    Text("⚙️", style = MaterialTheme.typography.titleLarge)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 搜索栏
            if (showSearch) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    placeholder = { Text("搜索诗词或作者...") },
                    singleLine = true
                )
            }

            // 分类标签
            ScrollableTabRow(
                selectedTabIndex = selectedCategory.ordinal,
                edgePadding = 16.dp,
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ) {
                PoemCategory.entries.forEachIndexed { index, category ->
                    Tab(
                        selected = selectedCategory.ordinal == index,
                        onClick = { selectedCategory = category },
                        text = { Text(category.displayName) }
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 每日推荐
                if (dailyPoem != null && searchQuery.isEmpty()) {
                    item {
                        DailyPoemCard(
                            poem = dailyPoem!!,
                            onClick = { onPoemClick(dailyPoem!!) }
                        )
                    }
                }

                // 学习进度
                if (searchQuery.isEmpty()) {
                    item {
                        StudyProgressCard(
                            completedCount = studiedCount,
                            totalCount = currentPoems.size
                        )
                    }
                }

                // 诗词列表
                items(filteredPoems, key = { it.id }) { poem ->
                    PoemCard(
                        poem = poem,
                        onClick = { onPoemClick(poem) }
                    )
                }
            }
        }
    }
}
