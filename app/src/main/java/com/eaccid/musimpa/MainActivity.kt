package com.eaccid.musimpa

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.eaccid.musimpa.ui.theme.MusimpaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MusimpaTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(1.0f),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }
}

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
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
            modifier = columnChildModifier,
            text = "Hello!"
        )
        Button(modifier = columnChildModifier.width(150.dp), onClick = { /*TODO*/ }) {
            Text(text = "Login")
        }
        Button(modifier = columnChildModifier.width(150.dp), onClick = { /*TODO*/ }) {
            Text(text = "Start movies")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MusimpaTheme {
        MainScreen()
    }
}