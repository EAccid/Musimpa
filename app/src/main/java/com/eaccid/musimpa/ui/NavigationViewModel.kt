package com.eaccid.musimpa.ui

import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class NavigationViewModel : ViewModel() {
    private val _backStackSize = MutableStateFlow(0)
    val backStackSize = _backStackSize.asStateFlow()

    private val _currentScreen = MutableStateFlow<Screen?>(Screen.MainScreen)
    val currentRoute = _currentScreen.asStateFlow()

    fun updateBackStackSize(size: Int) {
        _backStackSize.value = size
    }

    fun updateCurrentRoute(route: Screen) {
        _currentScreen.value = route
    }

    fun handleBackPress(navController: NavController) {
        if (_backStackSize.value > 2) {
            navController.popBackStack()
        } else {
            if (_currentScreen.value != Screen.MainScreen) {
                navController.popBackStack(route = Screen.MainScreen.route, inclusive = false)
            } else {
                TODO()
            }
        }
    }
}