package com.hermen.ass1

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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

@Composable
fun Navigation(
    navController: NavHostController,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit
) {
    CompositionLocalProvider(LocalRootNavController provides navController) {
        NavHost(
            navController = navController,
            startDestination = Screen.InitialPage.route
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
                    onToggleTheme = onToggleTheme
                )
            }
        }
    }
}

@Composable
fun InitialPage(navController: NavController, isDarkTheme:Boolean) {

    val backgroundColor = if (isDarkTheme) Color.Transparent else Color(0xFFE5FFFF)
    val signUpButtonColor = if (isDarkTheme) Color.Transparent else Color(0xFF00008B)
    val logInButtonColor = if (isDarkTheme) Color.Transparent else Color(0xFF89CFF0	)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
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

            // 在图标和按钮之间增加间距
            Spacer(modifier = Modifier.height(50.dp))

            // 按钮部分
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp), // 设置 Box 高度
                contentAlignment = Alignment.Center // 让 Box 内部内容居中
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = { navController.navigate(Screen.Signup.route) },
                        modifier = Modifier
                            .fillMaxWidth(0.7f) // 按钮宽度 70% 屏幕宽度
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(signUpButtonColor)
                    ) {
                        Text(text = "Sign In", fontSize = 18.sp)
                    }

                    Spacer(modifier = Modifier.height(20.dp)) // 按钮间距

                    Button(
                        onClick = { navController.navigate("login") },
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(logInButtonColor)
                    ) {
                        Text(text = "Login", fontSize = 18.sp, color = Color.Black)
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
