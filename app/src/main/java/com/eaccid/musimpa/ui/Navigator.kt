package com.eaccid.musimpa.ui

import androidx.navigation.NavOptionsBuilder
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

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