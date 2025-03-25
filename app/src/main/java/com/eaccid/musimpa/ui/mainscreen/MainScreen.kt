package com.eaccid.musimpa.ui.mainscreen

import android.annotation.SuppressLint
import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.eaccid.musimpa.repository.AuthenticationRepository
import com.eaccid.musimpa.ui.SaveLastScreenEffect
import com.eaccid.musimpa.ui.Screen
import com.eaccid.musimpa.ui.theme.MusimpaTheme
import org.koin.compose.koinInject

@Composable
fun MainScreen(navController: NavController) {
    //would be great to have viewmodel injected by DI, but koin causes recomposition
    val authenticationRepository = koinInject<AuthenticationRepository>()

    // next is just an overhead but good to know though

    /** currentBackStackEntry by rememberUpdatedState:
     * It ensures that currentRoute always holds the latest value of currentBackStackEntry?.destination?.route.
    Does not trigger recomposition when currentRoute updates, making it useful in effects like LaunchedEffect.
    It does not cause recomposition when currentRoute changes
    (which might be needed if you want the UI to react).**/
    //val currentBackStackEntry by rememberUpdatedState(newValue = navController.currentBackStackEntry)

    /** remember navController.getBackStackEntry:
     * Retrieves a specific NavBackStackEntry from the navigation stack for Screen.MainScreen.route.
    Remembers the result across recompositions, as long as navController doesn't change.
    If the route is not in the back stack, it throws an exception.
    It does not reactively update when navigation changes
    (it's tied to remember, which doesn't automatically track state changes).**/
    val navBackStackEntry = remember(navController) {
        navController.getBackStackEntry(Screen.MainScreen.route)
    }
    val factory by remember { lazy { MainScreenViewModelFactory(authenticationRepository) } }
    val viewModel: MainScreenViewModel =
        viewModel(viewModelStoreOwner = navBackStackEntry, factory = factory)
    val viewState by viewModel.uiState.collectAsStateWithLifecycle()
    MainScreenContent(viewState, onLoginClicked = {
        viewModel.login()
    }, onMoviesClicked = {
        navController.navigate(Screen.MovieListScreen.route) {
            // TODO still got two screen recomposes
            // Avoid multiple copies of the same destination when
            // reselecting the same item
            launchSingleTop = true
            // Restore state when reselecting a previously selected item
            restoreState = true
        }
    }, onWebActionSucceed = { succeed: Boolean ->
        viewModel.onWebAction(succeed)
    }
    )
    SideEffect {
        Log.i("twicetest ", " ---------------")
        Log.i(
            "twicetest @Composable//MainScreen",
            "@Composable//MainScreen ->> viewModel 1: $viewModel"
        )
    }
    SaveLastScreenEffect(Screen.MainScreen.route)
}

@Composable
fun MainScreenContent(
    viewState: MainScreenViewState,
    onLoginClicked: () -> Unit,
    onMoviesClicked: () -> Unit,
    onWebActionSucceed: (Boolean) -> Unit
) {
    Box() {
        Column(
            modifier = Modifier
                .fillMaxSize(1.0f)
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(all = 16.dp)

        ) {
            val columnChildModifier = Modifier
                .padding(bottom = 12.dp)
                // Align Modifier.Element requires a ColumnScope
                .align(Alignment.CenterHorizontally)
            Text(
                modifier = columnChildModifier, text = viewState.state.text
            )
            when (viewState.state) {
                MainScreenState.NoData, MainScreenState.Error -> {
                    Button(
                        modifier = columnChildModifier.width(150.dp), onClick = onLoginClicked
                    ) {
                        Text(text = "Login")
                    }
                }
                //todo handle onsite login url properly
                MainScreenState.OnSiteLogin -> {
                    WebView(
                        url = viewState.loginData?.url ?: "",
                        TMDBWebViewClient(onWebActionSucceed)
                    )
                }

                else -> {
                    Button(
                        modifier = columnChildModifier.width(150.dp), onClick = onMoviesClicked
                    ) {
                        Text(text = "Start movies")
                    }
                }
            }
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebView(url: String, webViewClient: WebViewClient) {
    AndroidView(
        modifier = Modifier.fillMaxSize(1.0f),
        factory = { context ->
            WebView(context).apply {
                this.webViewClient = webViewClient
            }
        },
        update = { webView ->
            webView.settings.javaScriptEnabled = true
            webView.loadUrl(url)
        }
    )
}

class TMDBWebViewClient(val succeed: (Boolean) -> Unit) : WebViewClient() {
    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        val url = request?.url.toString()
        if (url.contains("/allow")) {
            succeed(true)
            return false
        }
        view?.loadUrl(url)
        return super.shouldOverrideUrlLoading(view, request)
    }
}

class MainViewPreviewParameterProvider : PreviewParameterProvider<MainScreenViewState> {
    override val values = sequenceOf(
        MainScreenViewState(MainScreenState.NoData)
//        MainScreenState.NoData, MainScreenState.Error, MainScreenState.Success
    )
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview(@PreviewParameter(MainViewPreviewParameterProvider::class) viewState: MainScreenViewState) {
    MusimpaTheme {
        MainScreenContent(viewState, {}, {}, {})
    }
}