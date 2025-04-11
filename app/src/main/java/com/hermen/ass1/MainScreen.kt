package com.hermen.ass1

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.material3.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

enum class AppScreen(@StringRes val title: Int) {
    Home(title = R.string.app_name),
    Attendance(title = R.string.attendance),
    ClockIn(title = R.string.clock_in),
    ClockOut(title = R.string.clock_out)
}

@Composable
fun MainScreen(
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    modifier: Modifier = Modifier
) {

    val navController = rememberNavController()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = AppScreen.Home.name,
            modifier = modifier.padding(innerPadding)
        ) {
            composable(route = AppScreen.Home.name) {
                Home(
                    navController = navController,
                    modifier = modifier,
                    isDarkTheme = isDarkTheme,
                    onToggleTheme = onToggleTheme
                )
            }

            composable(route = AppScreen.Attendance.name) {
                AttendanceOverview(
                    gotoClockInScreen = {
                        navController.navigate(AppScreen.ClockIn.name)
                    },
                    gotoClockOutScreen = {
                        navController.navigate(AppScreen.ClockOut.name)
                    },
                    onBackButtonClicked = {
                        navController.popBackStack()
                    },
                    modifier = modifier
                )
            }

            composable(route = AppScreen.ClockIn.name) {
                ClockIn(
                    onBackButtonClicked = {
                        navController.popBackStack()
                    },
                    onBackToHomeClicked = {
                        navController.navigate(AppScreen.Home.name) {
                            popUpTo(AppScreen.Home.name) { inclusive = false }
                        }
                    },
                    modifier = modifier
                )
            }
            composable(route = AppScreen.ClockOut.name) {
                ClockOut(
                    onBackButtonClicked = {
                        navController.popBackStack()
                    },
                    onBackToHomeClicked = {
                        navController.navigate(AppScreen.Home.name) {
                            popUpTo(AppScreen.Home.name) { inclusive = false }
                        }
                    },
                    modifier = modifier
                )
            }
        }

    }
}

//@Composable
//fun ContentScreen(
//    modifier: Modifier = Modifier,
//    isDarkTheme: Boolean,
//    onToggleTheme: () -> Unit,
//    navController: NavController
//) {
//
//    NavHost(
//        navController = navController,
//        startDestination = AppScreen.Home.name,
//        modifier = modifier
//    ) {
//        composable(route = AppScreen.Home.name) {
//            Home(
//                navController = navController,
//                modifier = modifier,
//                isDarkTheme = isDarkTheme,
//                onToggleTheme = onToggleTheme
//            )
//        }
//
//        composable(route = AppScreen.Attendance.name) {
//            AttendanceOverview(
//                gotoClockInScreen = {
//                    navController.navigate(AppScreen.ClockIn.name)
//                },
//                gotoClockOutScreen = {
//                    navController.navigate(AppScreen.ClockOut.name)
//                },
//                onBackButtonClicked = {
//                    navController.popBackStack()
//                },
//                modifier = modifier
//            )
//        }
//
//        composable(route = AppScreen.ClockIn.name) {
//            ClockIn(
//                onBackButtonClicked = {
//                    navController.popBackStack()
//                },
//                onBackToHomeClicked = {
//                    navController.navigate(AppScreen.Home.name) {
//                        popUpTo(AppScreen.Home.name) { inclusive = false }
//                    }
//                },
//                modifier = modifier
//            )
//        }
//        composable(route = AppScreen.ClockOut.name) {
//            ClockOut(
//                onBackButtonClicked = {
//                    navController.popBackStack()
//                },
//                onBackToHomeClicked = {
//                    navController.navigate(AppScreen.Home.name) {
//                        popUpTo(AppScreen.Home.name) { inclusive = false }
//                    }
//                },
//                modifier = modifier
//            )
//        }
//    }
//}

@Composable
fun GotoAttendanceOverview(
    navController: NavController // 🔹 Use NavController instead of NavHostController
) {
    TextButton(
        onClick = {
            navController.navigate(AppScreen.Attendance.name) // 🔹 Navigate to AttendanceOverview
        }
    ) {

        Spacer(modifier = Modifier.width(30.dp))
        Text(
            text = "Attendance Overview >>",
            color = colorResource(id = R.color.teal_200),
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )
    }
}


@Composable
fun Home(
    navController: NavController,
    modifier: Modifier,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit
) {
    val icon = if (isDarkTheme) Icons.Filled.LightMode else Icons.Filled.DarkMode

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        IconButton(
            onClick = { onToggleTheme() },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
        ) {
            Icon(imageVector = icon, contentDescription = "Toggle Theme")
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            AppLogo(modifier = Modifier.size(200.dp))

            Spacer(modifier = Modifier.height(16.dp))

            ApplicationSection(navController = navController)

            Spacer(modifier = Modifier.height(16.dp))

            NotificationSection(navController = navController)

            GotoAttendanceOverview(navController = navController)
        }
    }
}

@Composable
fun AppLogo(modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally // Centers both Box & Text
    ) {
        Box(
            modifier = modifier
                .padding(top = 16.dp)
                .size(300.dp) // Square Box
                .clip(RoundedCornerShape(12.dp)) // Ensures shape consistency
        ) {
            Image(
                painter = painterResource(id = R.drawable.app_logo), // Load from drawable
                contentDescription = "App Logo",
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize() // Make image fill the entire Box
            )
        }

        Text(
            text = stringResource(R.string.app_name),
            color = Color.Black,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

data class AppItem(val name: String, val icon: Int, val route: String)

@Composable
fun ApplicationSection(navController: NavController) {
    val apps = listOf(
        AppItem("Meeting Room", R.drawable.meeting_room, "meeting_room_screen"),
        AppItem("Leave", R.drawable.leave, "leave_screen"),
        AppItem("Attendance Panel", R.drawable.attendance, "attendance_screen")
    )

    Column {
        Text(
            text = "Application",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            apps.forEach { app ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .wrapContentHeight()
                        .clickable { navController.navigate(app.route) }
                        .align(Alignment.CenterVertically)
                ) {
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .clip(CircleShape)
                    ) {
                        Image(
                            painter = painterResource(id = app.icon),
                            contentDescription = app.name,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                    Text(text = app.name, fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
fun AppNavigation(
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit
) {
    MainScreen(
        isDarkTheme = isDarkTheme,
        onToggleTheme = onToggleTheme
    )

//    val navController = rememberNavController()
//    NavHost(navController, startDestination = "home") {
//        composable("home") {
//            MainScreen(
//                isDarkTheme = isDarkTheme,
//                onToggleTheme = onToggleTheme
//            )
//        }
//        composable("meeting_room_screen") { MeetingRoomScreen() }
//        composable("leave_screen") { LeaveScreen() }
//        composable("attendance_screen") { AttendanceScreen() }
    }


@Composable
fun NotificationSection(navController: NavController) {
    val notifications = listOf(
        "New Message", "System Alert", "Upcoming Event", "New Comment"
        // Add more notifications as needed
    )

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Notifications",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp)
        ) {
            items(notifications.size) { index -> // Using the index instead of the notification directly
                val notification = notifications[index]
                NotificationCard(
                    title = notification,
                    onClick = {
                        // Navigate to Notification Detail (TODO)
                        navController.navigate("notification_detail_screen") // This can be a placeholder
                    }
                )
            }
        }
    }
}

@Composable
fun NotificationCard(title: String, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(5.dp)
            .clickable(onClick = onClick) // Handle navigation on click
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .background(Color.Gray) // Placeholder empty icon
        ) {
            // You can replace the Color.Gray with an actual image/icon later
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = title,
            fontSize = 14.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}



