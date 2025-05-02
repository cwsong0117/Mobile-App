package com.hermen.ass1.Announcement

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.hermen.ass1.BackButton
import java.net.URLEncoder

@Composable
fun AnnouncementDetailScreen(
    json: String,
    navController: NavController,
    isDarkTheme: Boolean
) {
    val announcement = remember {
        Json.decodeFromString<Announcement>(json)
    }

    val encodedImageUrl = URLEncoder.encode(announcement.imageUrl, "UTF-8")

    val decodedTitle = URLDecoder.decode(announcement.title, "UTF-8")
    val decodedContent = URLDecoder.decode(announcement.content, "UTF-8")
    val textColor = if (isDarkTheme) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onSurface
    val backgroundColor = if (isDarkTheme) Color(0xFF121212) else Color(0xFFE6F4F1)
    val saveButtonColor = if (isDarkTheme) Color(0xFF80CBC4) else Color(0xFF009688)
    val buttonTextColor = if (isDarkTheme) Color.Black else Color.White

    Log.d("Announcement Image Url", "Url: ${announcement.imageUrl}")

    Column(
        modifier = Modifier
            .background(backgroundColor)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            BackButton(navController = navController, title = "", isDarkTheme = isDarkTheme)

            // Edit Button aligned to the right (End)
            if (SessionManager.currentUser?.id == announcement.employeeID) {
                Button(
                    onClick = {
                        navController.navigate(
                            "CreateOrEditAnnouncementScreen?announcementId=${announcement.id}&title=${decodedTitle}&content=${decodedContent}&imageUrl=${encodedImageUrl}"
                        )
                    },
                    colors = ButtonDefaults.buttonColors(backgroundColor = saveButtonColor),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(end = 12.dp, top = 6.dp, bottom = 8.dp)
                        .height(36.dp)
                ) {
                    Text(text = "EDIT",
                        color = buttonTextColor,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Content Section
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(vertical = 8.dp, horizontal = 16.dp)
        ) {

            if (!announcement.imageUrl.isNullOrEmpty()) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(announcement.imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Announcement Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Title and Content
            Text(
                text = decodedTitle,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = decodedContent,
                style = MaterialTheme.typography.bodyLarge,
                color = textColor
            )

            Spacer(modifier = Modifier.weight(1f))

            // Footer Information
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Posted by: ${announcement.employeeID}",
                        style = MaterialTheme.typography.bodySmall,
                        color = textColor
                    )
                    Text(
                        text = "At: ${announcement.created_at}",
                        style = MaterialTheme.typography.bodySmall,
                        color = textColor
                    )
                }
            }
        }
    }
}
