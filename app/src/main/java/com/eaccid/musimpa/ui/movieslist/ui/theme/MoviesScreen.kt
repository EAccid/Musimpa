package com.eaccid.musimpa.ui.movieslist.ui.theme

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.eaccid.musimpa.ui.theme.MusimpaTheme
import org.koin.androidx.compose.koinViewModel


@Composable
fun MoviesScreen(navController: NavController) {
    val viewModel: MoviesScreenViewModel = koinViewModel()
    Text(text = "jjjjjj")
}

@Preview(showBackground = true)
@Composable
fun MoviesScreenPreview() {
    MusimpaTheme {
        MoviesScreen(rememberNavController())
    }
}
