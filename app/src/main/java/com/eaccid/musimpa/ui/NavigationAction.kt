package com.eaccid.musimpa.ui

import androidx.navigation.NavOptionsBuilder

sealed interface NavigationAction {

    data class Navigate(
        val destination: Screen,
        val navOptions: NavOptionsBuilder.() -> Unit = {}
    ) : NavigationAction

    //data object PopBackStack: NavigationAction
    data object NavigateUp : NavigationAction
}