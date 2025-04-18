package com.hermen.ass1.User

import android.util.Log
import androidx.compose.runtime.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hermen.ass1.User.User
import com.hermen.ass1.User.UserRepository
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.hermen.ass1.ThemeViewModel
import com.hermen.ass1.ui.theme.Screen
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.text.TextStyle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import android.net.Uri
import android.app.Activity
import android.content.Intent
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.net.toUri
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.launch

@Composable
fun UserProfileScreen(
    nestedNavController: NavHostController,
    rootNavController: NavHostController,
    isDarkTheme: Boolean,
    userProfileViewModel: UserProfileViewModel = viewModel()
) {
    val backgroundColor = if (isDarkTheme) Color(0xFF121212) else Color(0xFFE6F4F1)
    val fieldBackground = if (isDarkTheme) Color(0xFF2C2C2C) else Color(0xFFE0E0E0)
    val labelColor = if (isDarkTheme) Color.LightGray else Color.Blue
    val textColor = if (isDarkTheme) Color.White else Color.Black
    val saveButtonColor = if (userProfileViewModel.hasChanges) {
        if (isDarkTheme) Color(0xFF80CBC4) else Color(0xFF009688) // Brighter/Mint
    } else {
        if (isDarkTheme) Color(0xFF616161) else Color(0xFFB2DFDB) // Normal
    }

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            userProfileViewModel.markChangesMade()
        }
    }

    val userImageUrl = SessionManager.currentUser?.imageUrl
    Log.d("UserProfile", "userImageUrl = $userImageUrl")
    if (!userImageUrl.isNullOrEmpty()) {
        Text(
            text = userImageUrl,
            color = Color.Green,
            fontSize = 12.sp,
            modifier = Modifier.padding(8.dp)
        )
    }


    if (selectedImageUri != null) {
        // Load local selected image
        val bitmap = remember(selectedImageUri) {
            MediaStore.Images.Media.getBitmap(context.contentResolver, selectedImageUri)
        }
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = "Profile Image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    } else if (!userImageUrl.isNullOrEmpty()) {
        // Load from URL (needs Coil or Glide Compose)
        AsyncImage(
            model = userImageUrl,
            contentDescription = "Profile Image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    } else {
        Text("Tap to Upload", color = Color.White)
    }

    val coroutineScope = rememberCoroutineScope()
    val user = SessionManager.currentUser ?: return // Fail-safe in case user is already null

    // Initialize ViewModel with the current user data
    LaunchedEffect(user) {
        userProfileViewModel.initializeUserData(user)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Column(
            modifier = Modifier
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(if (isDarkTheme) Color.Black else Color.White),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(
                    onClick = { nestedNavController.popBackStack() },
                    modifier = Modifier
                ) {
                    Text("< PROFILE", color = if (isDarkTheme) Color.White else Color.Black)
                }

                Button(
                    onClick = {
                        coroutineScope.launch {
                            selectedImageUri?.let { uri ->
                                try {
                                    val newUrl = userProfileViewModel.uploadImageAndGetUrl(uri)
                                    SessionManager.currentUser?.imageUrl = newUrl
                                    userProfileViewModel.imageUrl = newUrl // if you have it
                                } catch (e: Exception) {
                                    Log.e("Save", "Image upload failed: ${e.message}")
                                    return@launch
                                }
                            }

                            userProfileViewModel.saveUserProfile()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = saveButtonColor),
                    enabled = userProfileViewModel.hasChanges
                ) {
                    Text("SAVE")
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(if (isDarkTheme) Color.DarkGray else Color.LightGray) // Border color
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                Column(
                    modifier = Modifier
                        .padding(bottom = 16.dp), // Adding some padding to the bottom for better spacing
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(16.dp))

                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(Color.Gray)
                            .clickable { launcher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        when {
                            selectedImageUri != null -> {
                                val bitmap = remember(selectedImageUri) {
                                    MediaStore.Images.Media.getBitmap(context.contentResolver, selectedImageUri)
                                }
                                Image(
                                    bitmap = bitmap.asImageBitmap(),
                                    contentDescription = "Selected Image",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }

                            !userImageUrl.isNullOrEmpty() -> {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(userImageUrl)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = "Firebase Profile Image",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }

                            else -> {
                                Text("Tap to Upload", color = Color.White)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    EditableProfileField(
                        label = "Name",
                        value = userProfileViewModel.name,
                        onValueChange = {
                            userProfileViewModel.name = it
                            userProfileViewModel.checkForChanges()
                        },
                        labelColor = labelColor,
                        fieldBackground = fieldBackground,
                        textColor = textColor,
                        errorMessage = userProfileViewModel.nameErrorMessage
                    )

                    EditableProfileField(
                        label = "Age",
                        value = userProfileViewModel.age,
                        onValueChange = { userProfileViewModel.onAgeChanged(it) },
                        labelColor = labelColor,
                        fieldBackground = fieldBackground,
                        textColor = textColor,
                        errorMessage = userProfileViewModel.ageErrorMessage
                    )

                    EditableProfileField(
                        label = "Position",
                        value = userProfileViewModel.position,
                        onValueChange = {
                            userProfileViewModel.position = it
                            userProfileViewModel.checkForChanges()
                        },
                        labelColor = labelColor,
                        fieldBackground = fieldBackground,
                        textColor = textColor,
                        errorMessage = userProfileViewModel.positionErrorMessage
                    )

                    EditableProfileField(
                        label = "Department",
                        value = userProfileViewModel.department,
                        onValueChange = {
                            userProfileViewModel.department = it
                            userProfileViewModel.checkForChanges()
                        },
                        labelColor = labelColor,
                        fieldBackground = fieldBackground,
                        textColor = textColor,
                        errorMessage = userProfileViewModel.departmentErrorMessage
                    )

                    EditableProfileField(
                        label = "Contact No",
                        value = userProfileViewModel.contactNo,
                        onValueChange = { userProfileViewModel.onContactNoChanged(it) },
                        labelColor = labelColor,
                        fieldBackground = fieldBackground,
                        textColor = textColor,
                        errorMessage = userProfileViewModel.contactNoErrorMessage
                    )

                    EditableProfileField(
                        label = "Email",
                        value = userProfileViewModel.email,
                        onValueChange = {
                            userProfileViewModel.email = it
                            userProfileViewModel.checkForChanges()
                        },
                        labelColor = labelColor,
                        fieldBackground = fieldBackground,
                        textColor = textColor,
                        errorMessage = userProfileViewModel.emailErrorMessage
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = {
                            SessionManager.currentUser = null
                            rootNavController.navigate(Screen.InitialPage.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    ) {
                        Text("Logout")
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
fun EditableProfileField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    labelColor: Color,
    fieldBackground: Color,
    textColor: Color,
    errorMessage: String? = null // Receive the error message from the ViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp, horizontal = 24.dp)
    ) {
        Text(label, color = labelColor, fontSize = 14.sp)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min) // Let the height adjust based on content
                .padding(top = 8.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(fieldBackground),
            contentAlignment = Alignment.CenterStart
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange, // Directly call onValueChange without any processing
                textStyle = TextStyle(fontSize = 16.sp, color = textColor),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 16.dp)
            )
        }

        // Show error message below the text field, if any
        errorMessage?.let {
            Spacer(modifier = Modifier.height(4.dp)) // Add some space before the error
            Text(
                text = it,
                color = Color.Red,
                fontSize = 12.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UserProfileScreenPreview() {
    val dummyUser = User(
        age = 30,
        contactNo = "012-3456 7890",
        department = "IT",
        email = "leekz@gmail.com",
        name = "LEE KEE ZHAN",
        position = "IT Manager"
    )
    val fakeThemeViewModel = ThemeViewModel()
    UserProfileContentPreview(dummyUser, fakeThemeViewModel)
}

@Composable
fun UserProfileContentPreview(user: User, themeViewModel: ThemeViewModel) {
    val isDarkTheme by themeViewModel.isDarkTheme
    val backgroundColor = if (isDarkTheme) Color(0xFF121212) else Color(0xFFE6F4F1)
    val fieldBackground = if (isDarkTheme) Color(0xFF2C2C2C) else Color(0xFFE0E0E0)
    val labelColor = if (isDarkTheme) Color.LightGray else Color.Blue
    val textColor = if (isDarkTheme) Color.White else Color.Black

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color.Gray),
                contentAlignment = Alignment.Center
            ) {
                Text("Image", color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))

            EditableProfileField("Name", user.name, {}, labelColor, fieldBackground, textColor)
            EditableProfileField("Age", user.age.toString(), {}, labelColor, fieldBackground, textColor)
            EditableProfileField("Position", user.position, {}, labelColor, fieldBackground, textColor)
            EditableProfileField("Department", user.department, {}, labelColor, fieldBackground, textColor)
            EditableProfileField("Contact No", user.contactNo, {}, labelColor, fieldBackground, textColor)
            EditableProfileField("Email", user.email, {}, labelColor, fieldBackground, textColor)

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                modifier = Modifier
                    .border(1.dp, Color.Red, shape = RoundedCornerShape(12.dp))
            ) {
                Text("LOGOUT", color = Color.Red)
            }
        }

        Button(
            onClick = { },
            modifier = Modifier.align(Alignment.TopEnd),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB2DFDB))
        ) {
            Text("SAVE")
        }
    }
}
