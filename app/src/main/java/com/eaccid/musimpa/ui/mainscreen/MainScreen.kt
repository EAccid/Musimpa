package com.eaccid.musimpa.ui.mainscreen

import android.annotation.SuppressLint
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.eaccid.musimpa.ui.Screen
import com.eaccid.musimpa.ui.theme.MusimpaTheme
import com.eaccid.musimpa.utils.showToast
import org.koin.androidx.compose.koinViewModel

@Composable
fun MainScreen(navController: NavController) {
    val context = LocalContext.current //todo check if there is better solution
    val viewModel: MainScreenViewModel = koinViewModel()
    val viewState by viewModel.uiState.collectAsState()
    MainScreenContent(viewState, onLoginClicked = {
        viewModel.login()
        context.showToast("onLoginClicked")
    }, onMoviesClicked = {
        navController.navigate(Screen.MoviesScreen.route)
        context.showToast("onMoviesClicked")
    }, onWebActionSucceed = { succeed: Boolean ->
        viewModel.onWebAction(succeed)
    }
    )
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