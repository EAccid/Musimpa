package com.eaccid.musimpa.ui

//todo extract just data class with state
typealias GreetingText = String

sealed class MainScreenState(val text: GreetingText) {
    object Success : MainScreenState("Let's start!") //todo get text from resources
    object Error : MainScreenState("Try again.")
    object NoData : MainScreenState("Hello!") // todo some loading/new app status

}