package com.kids.tangshi

import android.net.Uri
import android.os.Bundle
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
    var screen by remember { mutableStateOf("home") }
    var selectedPoem by remember { mutableStateOf<Poem?>(null) }

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
                onBackClick = { screen = "home" }
            )
        }
        "settings" -> SettingsScreen(
            onBackClick = { screen = "home" },
            onRequestImport = onRequestImport
        )
    }
}
