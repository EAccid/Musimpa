package com.eaccid.musimpa.ui.navigation

import androidx.navigation.NavOptionsBuilder

sealed interface NavigationAction {

    data class Navigate(
        val destination: Screen,
        val navOptions: NavOptionsBuilder.() -> Unit = {}
    ) : NavigationAction

    data object NavigateUp : NavigationAction
}