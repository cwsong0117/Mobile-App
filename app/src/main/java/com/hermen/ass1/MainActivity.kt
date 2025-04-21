
package com.hermen.ass1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.rememberNavController
import com.hermen.ass1.ui.theme.Ass1Theme
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val viewModel: ThemeViewModel by viewModels()

        setContent {
            val navController = rememberNavController()

            Ass1Theme(darkTheme = viewModel.isDarkTheme.value) {
                Navigation(
                    navController = navController,
                    isDarkTheme = viewModel.isDarkTheme.value,
                    onToggleTheme = viewModel::toggleTheme,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    val isDarkTheme = false

    Ass1Theme(darkTheme = isDarkTheme) {
        // Use a local composition here to avoid preview crash
        val navController = rememberNavController()

//        MainScreen(
//            isDarkTheme = isDarkTheme,
//            onToggleTheme = {} // No-op in preview
//
//        )
    }
}


