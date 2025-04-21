package com.hermen.ass1.Announcement

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.hermen.ass1.R
import java.net.URLDecoder

@Composable
fun CreateOrEditAnnouncement(
    navController: NavHostController,
    announcementId: String? = null,
    isDarkTheme: Boolean,
    title: String? = null,
    content: String? = null,
    imageUrl: String? = null
) {
    val viewModel: AnnouncementViewModel = viewModel()
    Log.d("TEST DEBUG", "passed URL: ${imageUrl}")

    // Load announcement data if editing
    LaunchedEffect(announcementId) {
        if (!announcementId.isNullOrBlank()) {
            viewModel.loadAnnouncement(announcementId)
        } else {
            title?.let { viewModel.title.value = it }
            content?.let { viewModel.content.value = it }
        }
    }

    LaunchedEffect(viewModel.saveSuccessful.value){
        if (viewModel.saveSuccessful.value) {
            viewModel.saveSuccessful.value = false
            navController.popBackStack()
        }
    }

    // Observe the title and content from the viewModel
    val currentTitle = viewModel.title.value
    val currentContent = viewModel.content.value

    val backgroundColor = if (isDarkTheme) Color.Transparent else Color(0xFFE5FFFF)
    val textColor = if (isDarkTheme) Color.White else Color.Black
    val iconColor = if (isDarkTheme) Color.White else Color.Black
    val buttonBackground = if (isDarkTheme) Color(0xFF80CBC4) else Color(0xFF009688)
    val buttonTextColor = if (isDarkTheme) Color.Black else Color.White

    val context = LocalContext.current
    val imageUri = remember { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        imageUri.value = uri
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .verticalScroll(rememberScrollState())
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(backgroundColor)
        ) {
            Row(
                modifier = Modifier
                    .height(56.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .padding(start = 8.dp, top = 8.dp)
                        .height(36.dp)
                ) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_arrow_back_ios_new_24),
                            contentDescription = "Back",
                            tint = iconColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = if (!announcementId.isNullOrBlank()) "Edit Announcement" else "New Announcement",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )

                Spacer(modifier = Modifier.weight(1f))

                Box(
                    modifier = Modifier
                        .padding(end = 8.dp, top = 8.dp)
                        .height(36.dp)
                        .background(buttonBackground, shape = RoundedCornerShape(8.dp))
                ) {
                    IconButton(
                        onClick = {
                            if (!announcementId.isNullOrEmpty()) {
                                Log.d("DEBUG", "announcementId is NOT null or empty: $announcementId")
                                viewModel.updateAnnouncement(announcementId, imageUri.value)
                            } else {
                                Log.d("DEBUG", "announcementId is null or empty")
                                viewModel.createAnnouncementWithCustomId()
                            }
                        }
                    ) {
                        Text(
                            text = "Save",
                            color = buttonTextColor,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Divider(color = Color.LightGray, thickness = 1.dp)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text("Title", color = textColor, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, shape = RoundedCornerShape(16.dp))
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                TextField(
                    value = currentTitle,
                    onValueChange = { viewModel.title.value = it },
                    colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.Transparent),
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3,
                    placeholder = { Text("Enter title") }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Content", color = textColor, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, shape = RoundedCornerShape(16.dp))
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                TextField(
                    value = currentContent,
                    onValueChange = { viewModel.content.value = it },
                    colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.Transparent),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    maxLines = 10,
                    placeholder = { Text("Enter content") }
                )
            }
            Log.d("Debug","image url: ${imageUrl}")
            if (imageUri.value != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Text("Selected Image", color = textColor, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(4.dp))
                Image(
                    painter = rememberAsyncImagePainter(imageUri.value),
                    contentDescription = "Selected Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            } else if (!imageUrl.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text("Existing Image", color = textColor, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(4.dp))
                Image(
                    painter = rememberAsyncImagePainter(imageUrl),
                    contentDescription = "Existing Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = { launcher.launch("image/*") },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(buttonBackground, shape = RoundedCornerShape(8.dp)),
                colors = ButtonDefaults.buttonColors(backgroundColor = buttonBackground)
            ) {
                Text("Upload Image", color = buttonTextColor, fontWeight = FontWeight.Bold)
            }
        }
    }
}
