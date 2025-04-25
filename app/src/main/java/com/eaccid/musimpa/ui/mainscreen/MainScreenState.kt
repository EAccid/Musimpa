package com.eaccid.musimpa.ui.mainscreen

typealias GreetingText = String

//TODO add Loading
sealed class MainScreenState(
    val text: GreetingText = ""
) {
    data object Success : MainScreenState("Let's start!") //todo get text from resources in ui
    data object Error : MainScreenState("Try again.")
    data object NoData : MainScreenState("Hello!")
    data object OnSiteLogin : MainScreenState()
}

//TODO refactor state handling
data class MainScreenViewState(
    val state: MainScreenState = MainScreenState.NoData,
    val loginData: LoginOnSiteData? = null
)

data class LoginOnSiteData(
    val url: String
)

