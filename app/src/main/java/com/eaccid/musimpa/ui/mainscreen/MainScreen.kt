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
import androidx.compose.runtime.rememberUpdatedState
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
import com.eaccid.musimpa.ui.Screen
import com.eaccid.musimpa.ui.theme.MusimpaTheme
import org.koin.compose.rememberKoinInject

@Composable
fun MainScreen(navController: NavController) {
    val authenticationRepository = rememberKoinInject<AuthenticationRepository>()
    //would be great to have viewmodel injected by DI, but koin causes recomposition

    // next is super-overhead for this project, as Jetpack Navigation does handle it with factory
    // but good to know though
    val factory by remember { lazy { MainScreenViewModelFactory(authenticationRepository) } }
    val currentBackStackEntry by rememberUpdatedState(newValue = navController.currentBackStackEntry)
    // Only create the viewModel if we have a valid NavBackStackEntry
    currentBackStackEntry?.let { navBackStackEntry ->
        val viewModel: MainScreenViewModel =
            viewModel(viewModelStoreOwner = navBackStackEntry, factory = factory)
        val viewState by viewModel.uiState.collectAsStateWithLifecycle()
        MainScreenContent(viewState, onLoginClicked = {
            viewModel.login()
        }, onMoviesClicked = {
            navController.navigate(Screen.MoviesScreen.route) //todo add navOptions to clear backstack
        }, onWebActionSucceed = { succeed: Boolean ->
            viewModel.onWebAction(succeed)
        }
        )
        SideEffect {
            Log.i(
                "twicetest @Composable//MainScreen",
                "@Composable//MainScreen ->> viewModel: $viewModel"
            )
        }
    }
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
        val url: String = request?.url.toString() ?: ""
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