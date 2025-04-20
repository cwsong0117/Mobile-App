package com.hermen.ass1.Announcement

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.hermen.ass1.R
import com.hermen.ass1.ThemeViewModel
import com.hermen.ass1.User.SessionManager
import kotlinx.serialization.json.Json
import java.net.URLDecoder
import androidx.compose.material.Surface
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.ButtonDefaults
import java.net.URLEncoder

@Composable
fun AnnouncementDetailScreen(
    json: String,
    navController: NavController,
    themeViewModel: ThemeViewModel
) {
    val announcement = remember {
        Json.decodeFromString<Announcement>(URLDecoder.decode(json, "UTF-8"))
    }

    val isDarkTheme = themeViewModel.isDarkTheme.value
    val textColor = if (isDarkTheme) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onSurface
    val iconColor = if (isDarkTheme) Color.White else Color.Black
    val backgroundColor = if (isDarkTheme) Color(0xFF121212) else Color(0xFFE6F4F1)
    val saveButtonColor = if (isDarkTheme) Color(0xFF80CBC4) else Color(0xFF009688)
    val buttonTextColor = if (isDarkTheme) Color.Black else Color.White

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        // Top bar with back and conditional edit button
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = backgroundColor
        ) {
            Row(
                modifier = Modifier
                    .height(56.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_arrow_back_ios_new_24),
                        contentDescription = "Back",
                        tint = iconColor
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                if (SessionManager.currentUser?.id == announcement.employeeID) {
                    Button(
                        onClick = {
                            navController.navigate(
                                "CreateOrEditAnnouncementScreen?announcementId=${announcement.id}&title=${announcement.title}&content=${announcement.content}"
                            )
                        },
                        colors = ButtonDefaults.buttonColors(backgroundColor = saveButtonColor),
                        modifier = Modifier
                            .height(36.dp)
                            .align(Alignment.CenterVertically)
                            .padding(end = 8.dp)
                    ) {
                        Text(
                            text = "EDIT",
                            color = buttonTextColor
                        )
                    }
                }
            }
        }

        Divider(color = Color.LightGray, thickness = 1.dp)

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
}