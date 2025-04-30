package com.hermen.ass1.Attendance

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.hermen.ass1.BackButton

@Composable
fun AdminScreen(isDarkTheme: Boolean, navController: NavController){
    val backgroundColor = if (isDarkTheme) Color.Black else Color(0xFFE5FFFF)
    Column(
        modifier = Modifier
            .background(backgroundColor)
            .fillMaxSize()
    ) {
        BackButton(navController = navController, title = "ADMIN PANEL", isDarkTheme = isDarkTheme)
    }
}