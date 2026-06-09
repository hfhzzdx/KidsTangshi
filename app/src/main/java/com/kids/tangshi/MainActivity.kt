package com.kids.tangshi

import android.os.Bundle
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.kids.tangshi.data.Poem
import com.kids.tangshi.ui.detail.DetailScreen
import com.kids.tangshi.ui.home.HomeScreen
import com.kids.tangshi.ui.settings.SettingsScreen
import com.kids.tangshi.ui.theme.KidsTangshiTheme

class MainActivity : ComponentActivity() {

    private var _importCallback: ((Uri) -> Unit)? = null

    private val filePicker = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { _importCallback?.invoke(it) }
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
                        onPickFile = { callback ->
                            _importCallback = callback
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
    onPickFile: ((android.net.Uri) -> Unit)? = null
) {
    // 简单的回退式导航
    var screen by remember { mutableStateOf("home") }  // "home", "detail", "settings"
    var selectedPoem by remember { mutableStateOf<Poem?>(null) }

    when (screen) {
        "home" -> HomeScreen(
            onPoemClick = { poem ->
                selectedPoem = poem
                screen = "detail"
            },
            onSettingsClick = {
                screen = "settings"
            }
        )
        "detail" -> selectedPoem?.let { poem ->
            DetailScreen(
                poem = poem,
                onBackClick = {
                    screen = "home"
                }
            )
        }
        "settings" -> SettingsScreen(
            onBackClick = {
                screen = "home"
            },
            onPickFile = onPickFile
        )
    }
}
