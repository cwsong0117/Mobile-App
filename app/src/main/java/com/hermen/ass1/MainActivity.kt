
package com.hermen.ass1

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavType
import com.hermen.ass1.ui.theme.Ass1Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val viewModel: ThemeViewModel by viewModels()

        setContent {
            Ass1Theme(darkTheme = viewModel.isDarkTheme.value) {
                MainScreen(
                    isDarkTheme = viewModel.isDarkTheme.value,
                    onToggleTheme = viewModel::toggleTheme
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    val isDarkTheme = false // or true to preview dark mode

    Ass1Theme(darkTheme = isDarkTheme) {
        MainScreen(
            isDarkTheme = isDarkTheme,
            onToggleTheme = {} // No-op for preview
        )
    }
}

