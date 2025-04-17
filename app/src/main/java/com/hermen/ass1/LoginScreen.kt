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
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.ktx.firestore
import com.hermen.ass1.User.SessionManager

@Composable
fun LoginScreen(navController: NavController) {
    val username = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val loginResult = remember { mutableStateOf("") }
    val userList = remember { mutableStateOf<List<User>>(emptyList()) }
    val user = SessionManager.currentUser

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp)
                .verticalScroll(rememberScrollState()), // 添加 verticalScroll
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // 文字部分
            Spacer(modifier = Modifier.height(100.dp)) // 调整高度，推开顶部

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterStart
            ) {
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

            // 在文字和图标之间增加间隔
            Spacer(modifier = Modifier.height(50.dp))

            // 图标部分
            Box(
                modifier = Modifier
                    .size(200.dp) // 方形 Box
                    .clip(RoundedCornerShape(12.dp))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.app_logo), // 记得添加资源
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

            Column(
                modifier = Modifier
                    .padding(start = 30.dp, end = 30.dp)
            ) {
                Text(
                    text = "Username",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp)
                )
                TextField(
                    value = username.value,
                    onValueChange = { username.value = it },
                    placeholder = { Text("ex: hello") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(50.dp)),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(30.dp))
                Text(
                    text = "Password",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp)
                )
                TextField(
                    value = password.value,
                    onValueChange = { password.value = it },
                    placeholder = { Text("ex: ******") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(50.dp)),
                    singleLine = true
                )
            }
            Spacer(modifier = Modifier.height(30.dp))

            Button(onClick = {
                scope.launch {
                    val users = UserRepository.getUsers()
                    userList.value = users

                    // Find if there's a user with matching name and password
                    val matchedUser = users.find {
                        it.name == username.value && it.password == password.value
                    }

                    if (matchedUser != null) {
                        SessionManager.currentUser = matchedUser // ✅ 存进去！
                        loginResult.value = "✅ Correct"
                    } else {
                        loginResult.value = "❌ Invalid"
                    }
                }
            }) {
                Text(text = "Confirm")
            }

            Spacer(modifier = Modifier.height(30.dp))

            Text(
                text = "User Collection Data:",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
            userList.value.forEach { user ->
                Text(text = user.toString()) // 或者更优雅地格式化
                Text(text = "ID: ${user.id}, Name: ${user.name}, Password: ${user.password}")
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = loginResult.value,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (loginResult.value == "✅ Correct") Color.Green else Color.Red,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )


            Text(text = "Username: ${username.value}")
            Text(text = "Password: ${password.value}")
            Text(text = "Welcome ${user?.name}")
            Text(text = " ${user?.id}")
            Text(text = " ${user?.age}")
            Text(text = " ${user?.contactNo}")
            Text(text = " ${user?.department}")
            Text(text = " ${user?.position}")

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
    LoginScreen(navController)
}