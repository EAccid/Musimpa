package com.eaccid.musimpa.ui

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.eaccid.musimpa.ui.theme.MusimpaTheme
import org.koin.androidx.compose.koinViewModel


@Composable
fun MainScreen() {
    val viewModel: MainScreenViewModel = koinViewModel()
    val viewState by viewModel.uiState.collectAsState()
    MainScreenContent(viewState) { viewModel.login() }
}

@Composable
fun MainScreenContent(viewState: MainScreenState, onLoginClicked: () -> Unit) {
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
            modifier = columnChildModifier, text = viewState.text
        )
        if (viewState == MainScreenState.NoData || viewState == MainScreenState.Error) {
            Button(modifier = columnChildModifier.width(150.dp), onClick = onLoginClicked) {
                Text(text = "Login")
            }
        } else {
            Button(modifier = columnChildModifier.width(150.dp),
                onClick = { /*TODO navigation jet pack*/ }) {
                Text(text = "Start movies")
            }
        }
    }

}

class MainViewPreviewParameterProvider : PreviewParameterProvider<MainScreenState> {
    override val values = sequenceOf(
        MainScreenState.NoData, MainScreenState.Error, MainScreenState.Success
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview(@PreviewParameter(MainViewPreviewParameterProvider::class) viewState: MainScreenState) {
    MusimpaTheme {
        MainScreenContent(viewState) {}
    }
}