package com.eaccid.musimpa.ui.mainscreen

typealias GreetingText = String

//todo add Loading
sealed class MainScreenState(
    val text: GreetingText = ""
) {
    object Success : MainScreenState("Let's start!") //todo get text from resources in ui
    object Error : MainScreenState("Try again.")
    object NoData : MainScreenState("Hello!")
    object OnSiteLogin : MainScreenState()
}

//todo refactor state handling
data class MainScreenViewState(
    val state: MainScreenState = MainScreenState.NoData,
    val loginData: LoginOnSiteData? = null
)

data class LoginOnSiteData(
    val url: String
)

