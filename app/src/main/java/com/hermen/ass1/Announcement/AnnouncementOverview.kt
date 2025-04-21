
package com.hermen.ass1.Announcement


import android.os.Bundle
import android.util.Log
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.hermen.ass1.Announcement.AnnouncementRepository
import com.hermen.ass1.AppScreen
import com.hermen.ass1.BackButton
import com.hermen.ass1.User.SessionManager
//import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.net.URLEncoder
import kotlinx.serialization.encodeToString
import coil.compose.AsyncImage
import coil.request.ImageRequest

@Composable
fun AnnouncementOverview(
    navController: NavHostController,
    isDarkTheme: Boolean
) {
    val announcements = remember { mutableStateOf(emptyList<Announcement>()) }
    val isLoading = remember { mutableStateOf(true) }
    val backgroundColor = if (isDarkTheme) Color.Transparent else Color(0xFFE5FFFF)

    val currentUser = SessionManager.currentUser
    val isAdmin = currentUser?.id?.startsWith("A") == true

    val navBackStackEntry = navController.currentBackStackEntryAsState().value
    val shouldRefresh = navBackStackEntry?.arguments?.getString("refresh") == "true"

    LaunchedEffect(Unit) {
        isLoading.value = true
        val fetchedAnnouncements = AnnouncementRepository.getAnnouncements()
        Log.d("DEBUG", "Fetched Announcements: $fetchedAnnouncements")  // Log fetched data
        announcements.value = fetchedAnnouncements
        isLoading.value = false
    }

    LaunchedEffect(shouldRefresh) {
        isLoading.value = true
        val fetchedAnnouncements = AnnouncementRepository.getAnnouncements()
        announcements.value = fetchedAnnouncements
        isLoading.value = false
    }

    Column(
        modifier = Modifier
            .background(backgroundColor)
            .fillMaxSize()
    ) {
        Box {
            // Back Button (your existing composable)
            BackButton(navController = navController, title = "Announcement", isDarkTheme = isDarkTheme)

            if (isAdmin) {
                IconButton(
                    onClick = {
                        // Navigate to CreateOrEditAnnouncement with empty parameters for a new announcement
                        navController.navigate("CreateOrEditAnnouncementScreen?announcementId=&title=&content=")
                    },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(end = 12.dp, top = 6.dp)
                        .size(36.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp)
                        )
                ) {
                    Text("+", color = MaterialTheme.colorScheme.primary, fontSize = 20.sp)
                }
            }
        }

        // Main content
        if (isLoading.value) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
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
        // Show image if available, otherwise show gray box
        if (!announcement.imageUrl.isNullOrBlank()) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(announcement.imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "Announcement Image",
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(Color.Gray, shape = RoundedCornerShape(8.dp))
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = announcement.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

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
