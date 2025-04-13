package com.hermen.ass1.Announcement

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.hermen.ass1.ThemeViewModel
import kotlinx.serialization.json.Json
import java.net.URLDecoder

@Composable
fun AnnouncementDetailScreen(
    json: String,
    navController: NavHostController,
    themeViewModel: ThemeViewModel
) {
    val announcement = remember {
        Json.decodeFromString<Announcement>(URLDecoder.decode(json, "UTF-8"))
    }

    val isDarkTheme = themeViewModel.isDarkTheme.value
    val textColor = if (isDarkTheme) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onSurface


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            text = announcement.title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = textColor
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = announcement.content,
            style = MaterialTheme.typography.bodyLarge,
            color = textColor
        )

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "Posted by: ${announcement.employeeID}",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "At: ${announcement.created_at}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
