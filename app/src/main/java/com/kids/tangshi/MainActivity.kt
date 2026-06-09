package com.kids.tangshi

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.kids.tangshi.data.FavoriteManager
import com.kids.tangshi.data.Poem
import com.kids.tangshi.ui.detail.DetailScreen
import com.kids.tangshi.ui.home.HomeScreen
import com.kids.tangshi.ui.settings.SettingsScreen
import com.kids.tangshi.ui.theme.KidsTangshiTheme
import com.kids.tangshi.util.TtsHelper
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private var importCallback: ((Uri) -> Unit)? = null

    private val filePicker = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { importCallback?.invoke(it) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KidsTangshiTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    KidsTangshiApp(
                        onRequestImport = {
                            importCallback = it
                            filePicker.launch("application/json")
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun KidsTangshiApp(
    onRequestImport: (((Uri) -> Unit) -> Unit)? = null
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val ttsHelper = remember { TtsHelper(context) }
    val favoriteManager = remember { FavoriteManager(context) }

    var screen by remember { mutableStateOf("home") }
    var selectedPoem by remember { mutableStateOf<Poem?>(null) }
    var isFavorite by remember { mutableStateOf(false) }
    var studiedIds by remember { mutableStateOf(setOf<String>()) }

    // 当选中诗词变化时，刷新收藏状态
    LaunchedEffect(selectedPoem?.id) {
        selectedPoem?.let { poem ->
            isFavorite = favoriteManager.isFavorite(poem.id)
        }
    }

    when (screen) {
        "home" -> HomeScreen(
            onPoemClick = { poem ->
                selectedPoem = poem
                screen = "detail"
            },
            onSettingsClick = { screen = "settings" }
        )
        "detail" -> selectedPoem?.let { poem ->
            DetailScreen(
                poem = poem,
                ttsHelper = ttsHelper,
                isFavorite = isFavorite,
                onFavoriteClick = {
                    scope.launch {
                        if (isFavorite) {
                            favoriteManager.removeFavorite(poem.id)
                            isFavorite = false
                            Toast.makeText(context, "已取消收藏", Toast.LENGTH_SHORT).show()
                        } else {
                            favoriteManager.addFavorite(poem)
                            isFavorite = true
                            Toast.makeText(context, "已收藏 ❤️", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                onBackClick = { screen = "home" },
                onStudyComplete = {
                    if (poem.id !in studiedIds) {
                        studiedIds = studiedIds + poem.id
                        Toast.makeText(context, "🎉 太棒了，又学了一首！", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "这首已经学过了哦 😊", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }
        "settings" -> SettingsScreen(
            ttsHelper = ttsHelper,
            onBackClick = { screen = "home" },
            onRequestImport = onRequestImport
        )
    }
}
