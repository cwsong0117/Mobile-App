package com.hermen.ass1.Announcement


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.hermen.ass1.Announcement.AnnouncementRepository
import com.hermen.ass1.AppScreen
//import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.net.URLEncoder
import kotlinx.serialization.encodeToString


@Composable
fun AnnouncementOverview(navController: NavHostController) {
    // State for announcements
    val announcements = remember { mutableStateOf(emptyList<Announcement>()) }
    val isLoading = remember { mutableStateOf(true) }

    // Fetch announcements data from Firestore
    LaunchedEffect(Unit) {
        isLoading.value = true
        val fetchedAnnouncements = AnnouncementRepository.getAnnouncements()
        announcements.value = fetchedAnnouncements
        isLoading.value = false
    }

    // UI Layout
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Loading indicator
        if (isLoading.value) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            // Displaying announcements in a list
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(announcements.value) { announcement ->
                    AnnouncementRow(announcement = announcement, navController = navController)
                }
            }
        }
    }
}

@Composable
fun AnnouncementRow(announcement: Announcement, navController: NavHostController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .clickable {
                val encoded = URLEncoder.encode(Json.encodeToString(announcement), "UTF-8")
                navController.navigate("${AppScreen.AnnouncementDetail.name}/$encoded")
            }
    ) {
        // Rectangle logo placeholder
        Box(
            modifier = Modifier
                .size(50.dp)
                .background(Color.Gray, shape = RoundedCornerShape(8.dp))
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            // Title (bold text)
            Text(
                text = announcement.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            // Content with truncated text and "read more"
            Text(
                text = announcement.content,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            if (announcement.content.length > 100) {
                TextButton(onClick = {
                    val encoded = URLEncoder.encode(Json.encodeToString(announcement), "UTF-8")
                    navController.navigate("${AppScreen.AnnouncementDetail.name}/$encoded")
                }) {
                    Text("Read more", color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewAnnouncementOverview() {
    AnnouncementOverview(navController = rememberNavController())
}
