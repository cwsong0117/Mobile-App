package com.hermen.ass1

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.material3.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.material3.Text
import androidx.compose.foundation.clickable
import androidx.compose.material3.TextField
import androidx.compose.material3.Button
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import com.hermen.ass1.User.UserRepository // or wherever your UserRepository is
import com.hermen.ass1.User.User
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.ktx.firestore
import com.hermen.ass1.User.SessionManager
import com.hermen.ass1.ui.theme.Screen

@Composable
fun LoginScreen(navController: NavController, isDarkTheme: Boolean) {
    val username = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val loginResult = remember { mutableStateOf("") }
    val userList = remember { mutableStateOf<List<User>>(emptyList()) }
    val backgroundColor = if (isDarkTheme) Color.Transparent else Color(0xFFE5FFFF)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp)
                .verticalScroll(rememberScrollState()), // Add verticalScroll
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // Header section
            Spacer(modifier = Modifier.height(100.dp)) // Adjust height, push down
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterStart) {
                Column(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 30.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Login",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(50.dp))

            // Logo section
            Box(modifier = Modifier
                .size(200.dp)
                .clip(RoundedCornerShape(12.dp))) {
                Image(
                    painter = painterResource(id = R.drawable.app_logo),
                    contentDescription = "App Logo",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Text(
                text = stringResource(R.string.app_name),
                color = Color.Black,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(50.dp))

            // Input fields for Username and Password
            Column(modifier = Modifier.padding(start = 30.dp, end = 30.dp)) {
                Text(
                    text = "Username",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp)
                )
                TextField(
                    value = username.value,
                    onValueChange = { username.value = it },
                    placeholder = { Text("ex: hello") },
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(50.dp)),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(30.dp))
                Text(
                    text = "Password",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp)
                )
                TextField(
                    value = password.value,
                    onValueChange = { password.value = it },
                    placeholder = { Text("ex: ******") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(50.dp)),
                    singleLine = true
                )
            }
            Spacer(modifier = Modifier.height(30.dp))

            // Login Button
            Button(onClick = {
                scope.launch {
                    // Simulate user fetching from Firestore
                    val users = UserRepository.getUsers()
                    userList.value = users

                    // Find the matching user from the list (by username)
                    val matchedUser = users.find {
                        it.name == username.value
                    }

                    if (matchedUser != null) {
                        // Authenticate using Firebase
                        FirebaseAuth.getInstance().signInWithEmailAndPassword(matchedUser.email, password.value)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    // Successful login
                                    SessionManager.currentUser = matchedUser
                                    loginResult.value = "✅ Login Successful"

                                    // Navigate to main screen
                                    navController.navigate(Screen.Main.route) {
                                        popUpTo(Screen.Login.route) { inclusive = true }
                                    }
                                } else {
                                    // Handle failed login
                                    loginResult.value = "❌ Invalid credentials or authentication failed."
                                }
                            }
                    } else {
                        // Handle case when no user is found in the list
                        loginResult.value = "❌ User not found in database."
                    }
                }
            }) {
                Text(text = "Login")
            }

            Spacer(modifier = Modifier.height(30.dp))

            // Show login result (error or success)
            Text(
                text = loginResult.value,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (loginResult.value == "✅ Login Successful") Color.Green else Color.Red,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            // Optionally display fetched users for debugging purposes
            userList.value.forEach { user ->
                Text(text = "User: ${user.name}, Email: ${user.email}")
            }
            Text(text = "Username: ${username.value}")
            Text(text = "Password: ${password.value}")

            Spacer(modifier = Modifier.height(50.dp))

            Text(text = "This is the Login Screen")
            Text(
                text = "← Back",
                modifier = Modifier
                    .padding(top = 20.dp)
                    .clickable { navController.popBackStack() },
                color = Color.Blue
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginPreview() {
    val navController = androidx.navigation.compose.rememberNavController()
    LoginScreen(navController, isDarkTheme = false)
}