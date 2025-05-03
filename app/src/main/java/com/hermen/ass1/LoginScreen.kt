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
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import com.hermen.ass1.ui.theme.DataStoreManager
import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.Flow
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(navController: NavController, isDarkTheme: Boolean) {
    val username = rememberSaveable { mutableStateOf("") }
    val password = rememberSaveable { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val loginResult = rememberSaveable { mutableStateOf("") }
    val userList = rememberSaveable { mutableStateOf<List<User>>(emptyList()) }
    val user = SessionManager.currentUser
    val backgroundColor = if (isDarkTheme) Color.Transparent else Color(0xFFE5FFFF)
    val showPassword = rememberSaveable { mutableStateOf(false) }
    val showDialog = rememberSaveable { mutableStateOf(false) }
    val useEmail = rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current

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
            Spacer(modifier = Modifier.height(60.dp)) // Adjust height, push down
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterStart) {
                Column(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { navController.popBackStack() },
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.Black
                            )
                        }
                        Text(
                            text = "Login",
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
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
                    text = if (useEmail.value) "Email" else "Username",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp)
                )
                TextField(
                    value = username.value,
                    onValueChange = { username.value = it },
                    placeholder = { Text(if (useEmail.value) "ex: email@example.com" else "ex: Lee Kee Zhan") },
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
                    visualTransformation = if (showPassword.value) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val image = if (showPassword.value)
                            Icons.Filled.Visibility
                        else Icons.Filled.VisibilityOff

                        Icon(
                            imageVector = image,
                            contentDescription = if (showPassword.value) "Hide password" else "Show password",
                            modifier = Modifier.clickable { showPassword.value = !showPassword.value }
                        )
                    },
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(50.dp)),
                    singleLine = true
                )
            }
            Spacer(modifier = Modifier.height(30.dp))

            Button(onClick = {
                scope.launch {
                    if (useEmail.value) {
                        // 🔐 Firebase email login
                        FirebaseAuth.getInstance().signInWithEmailAndPassword(
                            username.value.trim().lowercase(),
                            password.value
                        ).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                scope.launch {
                                    val currentEmail = FirebaseAuth.getInstance().currentUser?.email
                                    val matchedUser = UserRepository.getUsers().find {
                                        it.email.trim().lowercase() == currentEmail
                                    }
                                    if (matchedUser != null) {
                                        SessionManager.currentUser = matchedUser
                                        DataStoreManager.setLoggedIn(context, true)
                                        DataStoreManager.saveUserInfo(context, matchedUser.name, matchedUser.email)
                                        DataStoreManager.saveCurrentUser(context, matchedUser)
                                        loginResult.value = "✅ Login Successful"
                                        navController.navigate(Screen.Main.route) {
                                            popUpTo(Screen.Login.route) { inclusive = true }
                                        }
                                    } else {
                                        loginResult.value = "❌ User email not found in Firestore."
                                    }
                                }
                            } else {
                                loginResult.value = "❌ Authentication failed."
                            }
                        }
                    } else {
                        // 👤 Username login (custom logic)
                        val users = UserRepository.getUsers()
                        val matchedUser = users.find {
                            it.name == username.value.trim() && it.password == password.value
                        }

                        if (matchedUser != null) {
                            FirebaseAuth.getInstance().signInWithEmailAndPassword(
                                matchedUser.email,
                                password.value
                            ).addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    scope.launch {
                                        DataStoreManager.setLoggedIn(context, true)
                                        DataStoreManager.saveUserInfo(context, matchedUser.name, matchedUser.email)
                                        DataStoreManager.saveCurrentUser(context, matchedUser)
                                        SessionManager.currentUser = matchedUser
                                        loginResult.value = "✅ Login Successful"
                                        navController.navigate(Screen.Main.route) {
                                            popUpTo(Screen.Login.route) { inclusive = true }
                                        }
                                    }
                                } else {
                                    loginResult.value = "❌ Invalid credentials or authentication failed."
                                    showDialog.value = true
                                }
                            }
                        } else {
                            loginResult.value = "❌ User not found in database."
                            showDialog.value = true
                        }
                    }
                }
            }) {
                Text(text = "Confirm")
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

            Spacer(modifier = Modifier.height(30.dp))
            if (showDialog.value) {
                AlertDialog(
                    onDismissRequest = { showDialog.value = false },
                    title = { Text(text = "Login Failed") },
                    text = { Text("Would you like to try logging in with your email instead?") },
                    confirmButton = {
                        Button(onClick = {
                            showDialog.value = false
                            useEmail.value = true
                            username.value = "" // 清空旧用户名
                        }) {
                            Text("Yes")
                        }
                    },
                    dismissButton = {
                        Button(onClick = {
                            showDialog.value = false
                        }) {
                            Text("No")
                        }
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginPreview() {
    val navController = androidx.navigation.compose.rememberNavController()
    LoginScreen(navController, isDarkTheme = false)
}