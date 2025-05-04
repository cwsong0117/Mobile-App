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
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.hermen.ass1.AppScreen
import com.hermen.ass1.R
import java.net.URLDecoder
import java.nio.file.WatchEvent

@Composable
fun CreateOrEditAnnouncement(
    navController: NavHostController,
    announcementId: String? = null,
    isDarkTheme: Boolean
) {
    val viewModel: AnnouncementViewModel = viewModel()

    // State to track if data is loading
    val isLoading = remember { mutableStateOf(true) }

    // Track the original values for detecting changes
    val originalTitle = remember { mutableStateOf("") }
    val originalContent = remember { mutableStateOf("") }
    val originalImageUrl = remember { mutableStateOf<String?>(null) }

    // SOLUTION: Instead of using rememberSaveable directly, use the ViewModel state
    // and only use local state for temporary UI changes

    // The form fields are now synced with ViewModel state
    val title = viewModel.title.value
    val content = viewModel.content.value

    // Load announcement data if editing
    LaunchedEffect(announcementId) {
        if (!announcementId.isNullOrBlank()) {
            isLoading.value = true
            val announcement = AnnouncementRepository.getAnnouncementById(announcementId)
            announcement?.let {
                // Update all relevant states
                viewModel.title.value = it.title
                viewModel.content.value = it.content
                viewModel.imageUrl.value = it.imageUrl

                // Store original values for change detection
                originalTitle.value = it.title
                originalContent.value = it.content
                originalImageUrl.value = it.imageUrl
            }
            isLoading.value = false
        } else {
            isLoading.value = false
        }
    }

    LaunchedEffect(viewModel.saveSuccessful.value){
        if (viewModel.saveSuccessful.value) {
            viewModel.saveSuccessful.value = false
            navController.navigate(AppScreen.AnnouncementOverview.name){
                popUpTo("CreateOrEditAnnouncement") { inclusive = true }
            }
        }
    }

    val backgroundColor = if (isDarkTheme) Color.Black else Color(0xFFE5FFFF)
    val barColor = if (isDarkTheme) Color.Black else Color.White
    val textColor = if (isDarkTheme) Color.White else Color.Black
    val iconColor = if (isDarkTheme) Color.White else Color.Black
    val buttonBackground = if (isDarkTheme) Color(0xFF80CBC4) else Color(0xFF009688)
    val buttonTextColor = if (isDarkTheme) Color.Black else Color.White

    val uploadedState = remember { mutableStateOf(false) }

    // Ensure imageUri is tied to the ViewModel state
    val imageUri = remember {
        mutableStateOf<Uri?>(if (!uploadedState.value && viewModel.imageUrl.value != null) Uri.parse(viewModel.imageUrl.value) else null)
    }

    // Update imageUri when viewModel.imageUrl changes
    LaunchedEffect(viewModel.imageUrl.value) {
        if (!uploadedState.value && viewModel.imageUrl.value != null) {
            imageUri.value = Uri.parse(viewModel.imageUrl.value)
        }
    }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            imageUri.value = it
            uploadedState.value = true  // Mark that user has uploaded an image
            viewModel.uploadSuccessful.value = true
            Log.d("Upload Debug", "URL:${it}")
        }
    }

    val titleError = remember { mutableStateOf(false) }
    val contentError = remember { mutableStateOf(false) }

    val saveEnabled = viewModel.title.value.isNotBlank() && viewModel.content.value.isNotBlank()

    val saveButtonBackgroundColor = when {
        saveEnabled && isDarkTheme -> Color(0xFF80CBC4)
        saveEnabled && !isDarkTheme -> Color(0xFF009688)
        !saveEnabled && isDarkTheme -> Color.DarkGray
        else -> Color.Gray
    }

    val saveButtonTextColor = when {
        saveEnabled && isDarkTheme -> Color.Black
        saveEnabled && !isDarkTheme -> Color.White
        !saveEnabled && isDarkTheme -> Color.Gray
        else -> Color.DarkGray
    }

    val showDialog = remember { mutableStateOf(false) }
    val isTitleChanged = viewModel.title.value != originalTitle.value
    val isContentChanged = viewModel.content.value != originalContent.value
    val isImageChanged = (imageUri.value != null && uploadedState.value) ||
            (viewModel.imageUrl.value != originalImageUrl.value)

    val onBackPressed = {
        if (isTitleChanged || isContentChanged || isImageChanged) {
            showDialog.value = true
        } else {
            navController.popBackStack()
        }
    }

    val onBackPressedUnit: () -> Unit = {
        onBackPressed()
    }

    if (isLoading.value) {
        Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = if (isDarkTheme) Color(0xFF80CBC4) else Color(0xFF009688)
            )
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(barColor)
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
                        IconButton(onClick = onBackPressedUnit) { // Use the back press logic
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
                            .background(saveButtonBackgroundColor, shape = RoundedCornerShape(8.dp))
                    ) {
                        IconButton(
                            onClick = {
                                titleError.value = viewModel.title.value.isBlank()
                                contentError.value = viewModel.content.value.isBlank()

                                if (!titleError.value && !contentError.value) {
                                    if (!announcementId.isNullOrEmpty()) {
                                        viewModel.updateAnnouncement(announcementId, imageUri.value)
                                        viewModel.uploadSuccessful.value = false
                                    } else {
                                        viewModel.createAnnouncementWithCustomId(imageUri.value)
                                    }
                                }
                            },
                            enabled = saveEnabled
                        ) {
                            Text(
                                text = "Save",
                                color = saveButtonTextColor,
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
                Column {
                    TextField(
                        value = viewModel.title.value,
                        onValueChange = {
                            viewModel.title.value = it
                            titleError.value = it.isBlank()
                        },
                        isError = titleError.value,
                        colors = TextFieldDefaults.textFieldColors(
                            backgroundColor = if (isDarkTheme) Color.DarkGray else Color.White,
                            textColor = if (isDarkTheme) Color.White else Color.Black,
                            cursorColor = if (isDarkTheme) Color.White else Color.Black,
                            placeholderColor = if (isDarkTheme) Color.LightGray else Color.Gray,
                            errorIndicatorColor = Color.Red
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (isDarkTheme) Color.DarkGray else Color.White,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 4.dp),
                        maxLines = 3,
                        placeholder = { Text("Enter title", color = if (isDarkTheme) Color.LightGray else Color.Gray) }
                    )

                    if (titleError.value) {
                        Text(
                            text = "Title cannot be empty",
                            color = Color.Red,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(start = 12.dp, top = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text("Content", color = textColor, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(4.dp))
                Column {
                    TextField(
                        value = viewModel.content.value,
                        onValueChange = {
                            viewModel.content.value = it
                            contentError.value = it.isBlank()
                        },
                        isError = contentError.value,
                        colors = TextFieldDefaults.textFieldColors(
                            backgroundColor = if (isDarkTheme) Color.DarkGray else Color.White,
                            textColor = if (isDarkTheme) Color.White else Color.Black,
                            cursorColor = if (isDarkTheme) Color.White else Color.Black,
                            placeholderColor = if (isDarkTheme) Color.LightGray else Color.Gray,
                            errorIndicatorColor = Color.Red
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .background(
                                if (isDarkTheme) Color.DarkGray else Color.White,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 4.dp),
                        maxLines = 10,
                        placeholder = { Text("Enter content", color = if (isDarkTheme) Color.LightGray else Color.Gray) }
                    )

                    if (contentError.value) {
                        Text(
                            text = "Content cannot be empty",
                            color = Color.Red,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(start = 12.dp, top = 4.dp)
                        )
                    }
                }

                // Display image if available
                if (imageUri.value != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = if (uploadedState.value) "Selected Image" else "Existing Image",
                        color = textColor,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Image(
                        painter = rememberAsyncImagePainter(imageUri.value),
                        contentDescription = "Image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else if (viewModel.imageUrl.value != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Existing Image", color = textColor, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Image(
                        painter = rememberAsyncImagePainter(Uri.parse(viewModel.imageUrl.value!!)),
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

        if (showDialog.value) {
            AlertDialog(
                onDismissRequest = { showDialog.value = false },
                title = {
                    Text(
                        text = "Discard Changes?",
                        color = if (isDarkTheme) Color.White else Color.Black
                    )
                },
                text = {
                    Text(
                        text = "You have unsaved changes. Do you want to discard them?",
                        color = if (isDarkTheme) Color.White else Color.Black
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            navController.popBackStack() // Discard changes and go back
                            showDialog.value = false
                        },
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Discard",
                            color = if (isDarkTheme) Color(0xFFFF0000) else Color.Red
                        )
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showDialog.value = false },
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Cancel",
                            color = if (isDarkTheme) Color(0xFFB2C5FF) else Color(0xFF495D92)
                        )
                    }
                },
                shape = RoundedCornerShape(16.dp),
                backgroundColor = if (isDarkTheme) Color(0xFF232630) else Color.White
            )
        }
    }
}