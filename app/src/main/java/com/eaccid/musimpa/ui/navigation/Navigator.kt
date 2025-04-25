package com.eaccid.musimpa.ui.navigation

import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.navigation.NavOptionsBuilder
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow


/** Not in use
 * just trying out the way of navigation */

interface Navigator {
    val startDestination: Screen
    val navigationActions: Flow<NavigationAction>

    suspend fun navigate(
        destination: Screen,
        navOptions: NavOptionsBuilder.() -> Unit = {}
    )

    suspend fun navigateUp()
}

class DefaultNavigator(
    override val startDestination: Screen
) : Navigator {

//    private val _navigationActions = MutableSharedFlow<NavigationAction>(
//        replay = 10,
//        extraBufferCapacity = 10
//    )
//    override val navigationActions: SharedFlow<NavigationAction> =
//        _navigationActions.asSharedFlow()

    private val _navigationActions = Channel<NavigationAction>()
    override val navigationActions =
        _navigationActions.receiveAsFlow()

    override suspend fun navigate(destination: Screen, navOptions: NavOptionsBuilder.() -> Unit) {
        _navigationActions.send(
            NavigationAction.Navigate(
                destination = destination,
                navOptions = navOptions
            )
        )
    }

    override suspend fun navigateUp() {
        NavigationAction.NavigateUp
    }
}

class NavigationViewModel : ViewModel() {
    private val _backStackSize = MutableStateFlow(0)
    val backStackSize = _backStackSize.asStateFlow()

    private val _currentScreen = MutableStateFlow<Screen?>(Screen.Main)
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
            if (_currentScreen.value != Screen.Main) {
                navController.popBackStack(route = Screen.Main.route, inclusive = false)
            } else {
                TODO()
            }
        }
    }
}

