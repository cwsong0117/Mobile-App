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

@Composable
fun UserProfileScreen(
    nestedNavController: NavHostController,
    rootNavController: NavHostController,
    isDarkTheme: Boolean
){
    val backgroundColor = if (isDarkTheme) Color(0xFF121212) else Color(0xFFE6F4F1)
    val fieldBackground = if (isDarkTheme) Color(0xFF2C2C2C) else Color(0xFFE0E0E0)
    val labelColor = if (isDarkTheme) Color.LightGray else Color.Blue
    val textColor = if (isDarkTheme) Color.White else Color.Black

//    var shouldLogout by remember { mutableStateOf(false) }
//
//    if (shouldLogout) {
//        // Navigate first, then clear session after recomposition
//        LaunchedEffect(Unit) {
//            navController.navigate(Screen.InitialPage.route) {
//                popUpTo(Screen.Main.route) { inclusive = true }
//            }
//
//            // Delay just enough to let navigation complete
//            kotlinx.coroutines.delay(300)
//            SessionManager.currentUser = null
//        }
//
//        // Don't draw any UI if we're logging out
//        return
//    }

    val user = SessionManager.currentUser ?: return // Fail-safe in case user is already null

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
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

            ProfileField("Name", user.name, labelColor, fieldBackground, textColor)
            ProfileField("Age", user.age.toString(), labelColor, fieldBackground, textColor)
            ProfileField("Position", user.position, labelColor, fieldBackground, textColor)
            ProfileField("Department", user.department, labelColor, fieldBackground, textColor)
            ProfileField("Contact No", user.contactNo, labelColor, fieldBackground, textColor)
            ProfileField("Email", user.email, labelColor, fieldBackground, textColor)

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

        TextButton(
            onClick = { nestedNavController.popBackStack() },
            modifier = Modifier.align(Alignment.TopStart)
        ) {
            Text("< PROFILE", color = if (isDarkTheme) Color.White else Color.Black)
        }

        Button(
            onClick = { /* Save logic */ },
            modifier = Modifier.align(Alignment.TopEnd),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB2DFDB))
        ) {
            Text("SAVE")
        }
    }
}

@Composable
fun ProfileField(
    label: String,
    value: String,
    labelColor: Color,
    fieldBackground: Color,
    textColor: Color
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Text(label, color = labelColor, fontSize = 14.sp)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(fieldBackground),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = value,
                modifier = Modifier.padding(horizontal = 16.dp),
                fontSize = 16.sp,
                color = textColor
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

            ProfileField("Name", user.name, labelColor, fieldBackground, textColor)
            ProfileField("Age", user.age.toString(), labelColor, fieldBackground, textColor)
            ProfileField("Position", user.position, labelColor, fieldBackground, textColor)
            ProfileField("Department", user.department, labelColor, fieldBackground, textColor)
            ProfileField("Contact No", user.contactNo, labelColor, fieldBackground, textColor)
            ProfileField("Email", user.email, labelColor, fieldBackground, textColor)

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
