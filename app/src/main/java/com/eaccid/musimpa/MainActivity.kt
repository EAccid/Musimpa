package com.eaccid.musimpa

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import com.eaccid.musimpa.ui.AppNavigation
import com.eaccid.musimpa.ui.theme.MusimpaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("twicetest MainActivity", "MainActivity onCreate called")
        setContent {
            SideEffect {
                Log.i("twicetest ", " --------------- ")
                Log.i("twicetest MainActivity", "MainActivity: ${this@MainActivity}, ")
            }
            MusimpaTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(1.0f),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

