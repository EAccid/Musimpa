package com.eaccid.musimpa.ui.mainscreen

import com.eaccid.musimpa.utils.AUTHENTICATE_REQUEST_TOKEN_URL

typealias GreetingText = String

//todo extract just data class with state
sealed class MainScreenState(val text: GreetingText, var tempWebViewUrlHolder: String = "AUTHENTICATE_REQUEST_TOKEN_URL + requestToken") {
    object Success : MainScreenState("Let's start!") //todo get text from resources
    object Error : MainScreenState("Try again.")
    object NoData : MainScreenState("Hello!") // todo some loading/new app status
    object NavigateToMovies : MainScreenState("")
    object NavigateToWebView : MainScreenState("")
}