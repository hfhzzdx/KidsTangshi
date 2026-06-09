package com.kids.tangshi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.kids.tangshi.ui.theme.KidsTangshiTheme
import com.kids.tangshi.ui.home.HomeScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KidsTangshiTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    KidsTangshiApp()
                }
            }
        }
    }
}

@Composable
fun KidsTangshiApp() {
    HomeScreen()
}

@Preview(showBackground = true)
@Composable
fun KidsTangshiAppPreview() {
    KidsTangshiTheme {
        KidsTangshiApp()
    }
}
