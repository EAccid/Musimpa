package com.eaccid.musimpa.ui

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.eaccid.musimpa.ui.navigation.PreferencesDataStoreManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.koin.compose.koinInject


@Composable
fun SaveLastScreenEffect(route: String) {
    val preferencesDataStoreManager = koinInject<PreferencesDataStoreManager>()
    SideEffect {
        Log.i("temptest AppNavigation", "saved lastScreen: $route ")
    }
    LaunchedEffect(route) {
        saveLastScreenPreference(preferencesDataStoreManager, route)
    }
}

private suspend fun saveLastScreenPreference(
    preferencesManager: PreferencesDataStoreManager,
    route: String
) {
    preferencesManager.saveLastScreen(route)
}

@Composable
fun <T> ObserveAsEvents(flow: Flow<T>, onEvent: (T) -> Unit) {
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(flow, lifecycleOwner.lifecycle) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            withContext(Dispatchers.Main.immediate) {
                flow.collect(onEvent)
            }
        }
    }
}

@Composable
fun LogCompositions(tag: String) {
    val count = remember { mutableStateOf(0) }
    SideEffect {
        count.value++
        Log.d("Recompose", "$tag recomposed ${count.value} times")
    }
    DisposableEffect(Unit) {
        println("--> $tag DisposableEffect Entered")
        onDispose { println("<-- $tag DisposableEffect Disposed") }
    }
}