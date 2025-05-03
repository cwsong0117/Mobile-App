
package com.hermen.ass1.Announcement


import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
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
import com.hermen.ass1.R
import kotlinx.coroutines.CoroutineScope

@Composable
fun AnnouncementOverview(
    navController: NavHostController,
    isDarkTheme: Boolean
) {
    val announcements = remember { mutableStateOf(emptyList<Announcement>()) }
    val isLoading = remember { mutableStateOf(true) }
    val backgroundColor = if (isDarkTheme) Color.Black else Color(0xFFE5FFFF)
    val barColor = if (isDarkTheme) Color.Transparent else Color.White
    val textColor = if (isDarkTheme) Color.White else Color.Black
    val iconColor = if (isDarkTheme) Color.White else Color.Black

    val currentUser = SessionManager.currentUser
    val isAdmin = currentUser?.id?.startsWith("A") == true

    val navBackStackEntry = navController.currentBackStackEntryAsState().value
    val shouldRefresh = navBackStackEntry?.arguments?.getString("refresh") == "true"

    val isDeleteMode = remember { mutableStateOf(false) }
    val selectedAnnouncementIds = remember { mutableStateOf<List<String>>(emptyList()) }

    LaunchedEffect(Unit) {
        isLoading.value = true
        val fetchedAnnouncements = AnnouncementRepository.getAnnouncements()
        announcements.value = fetchedAnnouncements
        isLoading.value = false
    }

    LaunchedEffect(shouldRefresh) {
        isLoading.value = true
        val fetchedAnnouncements = AnnouncementRepository.getAnnouncements()
        announcements.value = fetchedAnnouncements
        isLoading.value = false
    }

    val onDeleteConfirmed: () -> Unit = {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                isLoading.value = true
                selectedAnnouncementIds.value.forEach { id ->
                    AnnouncementRepository.deleteAnnouncement(id)
                }
                val refreshed = AnnouncementRepository.getAnnouncements()
                announcements.value = refreshed
                isDeleteMode.value = false
                selectedAnnouncementIds.value = emptyList()
            } catch (e: Exception) {
                Log.e("OverviewDebug", "Failed to delete: ${e.message}")
            } finally {
                isLoading.value = false
            }
        }
    }
    val showDeleteDialog = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .background(backgroundColor)
            .fillMaxSize()
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Surface(modifier = Modifier.fillMaxWidth(), color = barColor) {
                    Row(
                        modifier = Modifier.height(52.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { navController.popBackStack(AppScreen.Home.name, inclusive = false) }) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_arrow_back_ios_new_24),
                                contentDescription = "Back",
                                modifier = Modifier.size(24.dp),
                                tint = iconColor
                            )
                        }
                        Text(
                            text = "Announcement",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 8.dp),
                            color = textColor
                        )
                    }
                }
                Divider(color = Color.LightGray, thickness = 2.dp)
            }

            Row(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isAdmin) {
                    Button(
                        onClick = {
                            if (isDeleteMode.value) {
                                isDeleteMode.value = false
                                selectedAnnouncementIds.value = emptyList()
                            } else {
                                isDeleteMode.value = true
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isDarkTheme) Color.Black else Color.White // Background changes based on theme
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        border = BorderStroke(2.dp, Color(0xFF80CBC4))
                    ) {
                        Text(
                            text = if (isDeleteMode.value) "Cancel" else "Delete",
                            color = Color(0xFF80CBC4)
                        )
                    }

                    if (isDeleteMode.value) {
                        Button(
                            onClick = { showDeleteDialog.value = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isDarkTheme) Color(0xFF80CBC4) else Color(0xFF009688)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            enabled = selectedAnnouncementIds.value.isNotEmpty()
                        ) {
                            Text(
                                "Confirm",
                                color = when {
                                    isDarkTheme && selectedAnnouncementIds.value.isNotEmpty() -> Color.Black
                                    selectedAnnouncementIds.value.isNotEmpty() -> Color.White
                                    else -> Color.Gray
                                }
                            )
                        }
                    }
                }

                if (isAdmin && !isDeleteMode.value) {
                    Button(
                        onClick = {
                            navController.navigate("CreateOrEditAnnouncementScreen?announcementId=&title=&content=")
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isDarkTheme) Color(0xFF80CBC4) else Color(0xFF009688)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text("+", color = if (isDarkTheme) Color.Black else Color.White)
                    }
                }
            }
        }

        // Main content
        if (isLoading.value) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(announcements.value) { announcement ->
                    AnnouncementRow(
                        announcement = announcement,
                        navController = navController,
                        isDeleteMode = isDeleteMode.value,
                        selectedAnnouncementIds = selectedAnnouncementIds,
                        isDarkTheme = isDarkTheme
                    )
                }
            }
        }
    }
    if (showDeleteDialog.value) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog.value = false },
            title = { Text("Confirm Deletion") },
            text = {
                Text("Are you sure you want to delete the selected announcements? This action cannot be undone.")
            },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog.value = false
                    onDeleteConfirmed()
                }) {
                    Text("Delete", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDeleteDialog.value = false
                }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun AnnouncementRow(
    announcement: Announcement,
    navController: NavHostController,
    isDeleteMode: Boolean = false,
    selectedAnnouncementIds: MutableState<List<String>>,
    isDarkTheme: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .clickable(enabled = !isDeleteMode) {
                if (!isDeleteMode) {
                    val encoded = URLEncoder.encode(Json.encodeToString(announcement), "UTF-8")
                    navController.navigate("${AppScreen.AnnouncementDetail.name}/$encoded")
                }
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isDeleteMode) {
            Checkbox(
                checked = selectedAnnouncementIds.value.contains(announcement.id),
                onCheckedChange = { isChecked ->
                    selectedAnnouncementIds.value = if (isChecked) {
                        selectedAnnouncementIds.value + announcement.id
                    } else {
                        selectedAnnouncementIds.value - announcement.id
                    }
                }
            )
            Spacer(modifier = Modifier.width(8.dp))
        }

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
                fontWeight = FontWeight.Bold,
                color = if (isDarkTheme) Color.White else Color.Black
            )

            Text(
                text = announcement.content,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = if (isDarkTheme) Color.White else Color.Black
            )
        }
    }
}
