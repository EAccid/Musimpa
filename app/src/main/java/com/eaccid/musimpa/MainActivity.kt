package com.eaccid.musimpa

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.eaccid.musimpa.ui.ScreenNavigation
import com.eaccid.musimpa.ui.theme.MusimpaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MusimpaTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(1.0f),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ScreenNavigation()
                }
            }
        }
    }
}

