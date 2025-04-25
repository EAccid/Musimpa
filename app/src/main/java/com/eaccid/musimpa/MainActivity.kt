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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.eaccid.musimpa.ui.navigation.AppNavigation
import com.eaccid.musimpa.ui.navigation.PreferencesDataStoreManager
import com.eaccid.musimpa.ui.theme.MusimpaTheme
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    private val preferencesDataStoreManager by inject<PreferencesDataStoreManager>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("temptest MainActivity", "MainActivity ${this@MainActivity} - onCreate")

        // TODO try to move lastScreen to another logic
        // Launch a coroutine in lifecycleScope, using Lifecycle.State.STARTED to ensure it runs when Activity is started
        lifecycleScope.launch {
            // Collect the data only when the Activity is in STARTED state
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                val lastScreen = preferencesDataStoreManager.getLastScreen()
                setContent {
                    SideEffect {
                        Log.i("temptest ", " --------------- ")
                        Log.i("temptest MainActivity", "MainActivity")
                    }
                    MusimpaTheme {
                        // A surface container using the 'background' color from the theme
                        Surface(
                            modifier = Modifier.fillMaxSize(1.0f),
                            color = MaterialTheme.colorScheme.background
                        ) {
                            AppNavigation(lastScreen)
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i("temptest MainActivity", "MainActivity ${this@MainActivity} - onDestroy")
    }
}

