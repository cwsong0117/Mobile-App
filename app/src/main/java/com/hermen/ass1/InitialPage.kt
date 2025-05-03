 package com.hermen.ass1

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import androidx.navigation.compose.NavHost
import com.hermen.ass1.ui.theme.Screen
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.ui.platform.LocalConfiguration
import com.hermen.ass1.User.SessionManager
import com.hermen.ass1.User.User
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hermen.ass1.ui.theme.DataStoreManager
import androidx.compose.material3.CircularProgressIndicator
import com.hermen.ass1.ui.theme.UserRepository
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.flow.firstOrNull // 用于访问 Flow 的 firstOrNull



 @Composable
fun Navigation(
    navController: NavHostController,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
) {
     val context = LocalContext.current
     val scope = rememberCoroutineScope()

     // 获取登录状态
     val loggedInState = DataStoreManager.getLoggedIn(context).collectAsState(initial = null)

     // ⏳ 如果登录状态还在加载，显示加载动画
     if (loggedInState.value == null) {
         Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
             CircularProgressIndicator()
         }
         return
     }

     // ✅ 自动登录逻辑，如果登录状态为 true 且 SessionManager.currentUser 为 null
     if (loggedInState.value == true && SessionManager.currentUser == null) {
         LaunchedEffect(Unit) {
             val email = DataStoreManager.getUserEmail(context).firstOrNull()
             val name = DataStoreManager.getUserName(context).firstOrNull()
             val savedUser = DataStoreManager.getCurrentUser(context)

             if (savedUser != null) {
                 SessionManager.currentUser = savedUser
                 DataStoreManager.saveCurrentUser(context, savedUser) // <- Save it again
             }

             if (!email.isNullOrEmpty() && !name.isNullOrEmpty()) {
                 val users = UserRepository.getUsers()
                 val matchedUser = users.find {
                     it.email.trim().lowercase() == email.trim().lowercase()
                 }

                 if (matchedUser != null) {
                     try {
                         // 自动登录操作
                         FirebaseAuth.getInstance().signInWithEmailAndPassword(
                             matchedUser.email,
                             matchedUser.password // 使用密码（或改为 token 认证）
                         ).await()

                         // 登录成功
                         SessionManager.currentUser = matchedUser
                         DataStoreManager.setLoggedIn(context, true)
                         DataStoreManager.saveUserInfo(context, matchedUser.name, matchedUser.email)

                         // 跳转到主页面
                         navController.navigate(Screen.Main.route) {
                             popUpTo(Screen.Login.route) { inclusive = true }
                         }
                     } catch (e: Exception) {
                         Log.e("AutoLogin", "❌ 自动登录失败: ${e.message}")
                     }
                 }
             }
         }
     }

     // 最终决定导航的目标页面（如果已登录则跳转到主页面）
     val startDestination = if (loggedInState.value == true) {
         Screen.Main.route
     } else {
         Screen.InitialPage.route // 未登录则跳转到登录页面
     }

     CompositionLocalProvider(LocalRootNavController provides navController) {
        NavHost(
            navController = navController,
            startDestination = startDestination
        ) {
            composable(Screen.InitialPage.route) {
                InitialPage(navController, isDarkTheme)
            }
            composable(Screen.Signup.route) {
                SignupScreen(navController, isDarkTheme)
            }
            composable(Screen.Login.route) {
                LoginScreen(navController, isDarkTheme)
            }
            composable(Screen.Main.route) {
                MainScreen(
                    isDarkTheme = isDarkTheme,
                    onToggleTheme = onToggleTheme,
                )
            }
        }
    }
}

@Composable
fun InitialPage(navController: NavController, isDarkTheme: Boolean) {

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val backgroundColor = if (isDarkTheme) Color.Transparent else Color(0xFFE5FFFF)
    val signUpButtonColor = if (isDarkTheme) Color.Transparent else Color(0xFF89CFF0)
    val logInButtonColor = if (isDarkTheme) Color.Transparent else Color(0xFF89CFF0)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        if (isLandscape) {
            // 💡 Landscape Mode: Horizontal layout (everything shown in one screen)
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {

                // Logo and App Name
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ){
                    Image(
                        painter = painterResource(id = R.drawable.app_logo),
                        contentDescription = "App Logo",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(150.dp)
                    )
                    Text(
                        text = stringResource(R.string.app_name),
                        color = Color.Black,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Buttons
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Button(
                        onClick = { navController.navigate(Screen.Signup.route) },
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(signUpButtonColor)
                    ) {
                        Text(text = "Signup", fontSize = 18.sp)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { navController.navigate("login") },
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(Color.White),
                        border = BorderStroke(2.dp, logInButtonColor)
                    ) {
                        Text(text = "Login", fontSize = 18.sp, color = logInButtonColor)
                    }
                }
            }
        } else {
            // 📱 Portrait Mode: Default (Vertical layout with scrollable height)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Spacer(modifier = Modifier.height(100.dp))

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Column(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = 60.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "GOOD",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Text(
                            text = "  DAY!",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                }

                Spacer(modifier = Modifier.height(50.dp))

                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .clip(RoundedCornerShape(12.dp))
                ) {
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

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Button(
                            onClick = { navController.navigate(Screen.Signup.route) },
                            modifier = Modifier
                                .fillMaxWidth(0.7f)
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(signUpButtonColor)
                        ) {
                            Text(text = "Sign In", fontSize = 18.sp)
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = { navController.navigate("login") },
                            modifier = Modifier
                                .fillMaxWidth(0.7f)
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(Color.White),
                            border = BorderStroke(2.dp, logInButtonColor)
                        ) {
                            Text(text = "Login", fontSize = 18.sp, color = logInButtonColor)
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun InitialPagePreview(){
    val fakeNavController = rememberNavController()
    InitialPage(navController = fakeNavController, isDarkTheme = false)
}