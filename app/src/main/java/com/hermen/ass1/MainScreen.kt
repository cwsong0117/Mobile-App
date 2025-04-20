package com.hermen.ass1

import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.hermen.ass1.Announcement.*
import com.hermen.ass1.Attendance.AttendanceHistory
import com.hermen.ass1.Attendance.AttendanceOverview
import com.hermen.ass1.Attendance.ClockIn
import com.hermen.ass1.Attendance.ClockOut
import com.hermen.ass1.LeaveApplication.LeaveApply
import com.hermen.ass1.MeetingRoom.RoomViewModel
import com.hermen.ass1.User.UserProfileScreen

enum class AppScreen(@StringRes val title: Int) {
    Home(title = R.string.app_name),
    Attendance(title = R.string.attendance),
    ClockIn(title = R.string.clock_in),
    ClockOut(title = R.string.clock_out),
    AnnouncementOverview(title = R.string.announcement_overview),
    AnnouncementDetail(title = R.string.announcement_detail),
    UserProfile(title = R.string.user_profile),
    LeaveApplication(title = R.string.leave_application),
    CreateOrEditAnnouncement(title = R.string.create_or_edit_announcement)
}

data class AppItem(
    val name: String,
    val icon: Int,
    val route: String
)

val LocalRootNavController = staticCompositionLocalOf<NavHostController> {
    error("No Root NavController provided")
}

@Composable
fun MainScreen(
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val themeViewModel: ThemeViewModel = viewModel()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            BottomNavigationBar(
                navController = navController
            )
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
                    gotoHistoryScreen = {
                        navController.navigate("attendanceHistory")
                    },
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

            composable(route = "attendanceHistory") {
               AttendanceHistory( onBackButtonClicked = {
                   navController.popBackStack()
               })
            }

            composable(route = AppScreen.LeaveApplication.name) {
                LeaveApplication(navController = navController, isDarkTheme = isDarkTheme)
            }

            composable(route = AppScreen.ClockIn.name) {
                ClockIn(
                    onBackButtonClicked = {
                        navController.popBackStack()
                    },
                    modifier = modifier
                )
            }

            composable(route = AppScreen.ClockOut.name) {
                ClockOut(
                    onBackButtonClicked = {
                        navController.popBackStack()
                    },
                    modifier = modifier
                )
            }

            composable(route = AppScreen.AnnouncementOverview.name) {
                AnnouncementOverview(
                    navController = navController,
                    isDarkTheme = isDarkTheme)
            }

            composable(
                route = "CreateOrEditAnnouncementScreen?announcementId={announcementId}&title={title}&content={content}",
                arguments = listOf(
                    navArgument("announcementId") { type = NavType.StringType },
                    navArgument("title") { type = NavType.StringType },
                    navArgument("content") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val announcementId = backStackEntry.arguments?.getString("announcementId")
                val title = backStackEntry.arguments?.getString("title")
                val content = backStackEntry.arguments?.getString("content")

                // Debugging logs to check the values of the parameters
                Log.d("CreateOrEditAnnouncement", "announcementId: $announcementId, title: $title, content: $content")

                CreateOrEditAnnouncement(
                    navController = navController,
                    announcementId = announcementId,
                    isDarkTheme = isDarkTheme,
                    title = title,
                    content = content
                )
            }

            composable(
                route = "${AppScreen.AnnouncementDetail.name}/{announcementJson}",
                arguments = listOf(navArgument("announcementJson") { type = NavType.StringType })
            ) { backStackEntry ->
                val json = backStackEntry.arguments?.getString("announcementJson") ?: ""
                AnnouncementDetailScreen(
                    json = json,
                    navController = navController,
                    themeViewModel = themeViewModel
                )
            }

            composable(route = AppScreen.UserProfile.name) {
                UserProfileScreen(
                    nestedNavController = navController,
                    rootNavController = LocalRootNavController.current, // <- from CompositionLocal or state lift
                    isDarkTheme = isDarkTheme
                )
            }

            composable("meeting_room_screen") {
                MeetingRoomApply(
                    navController = navController,
                    isDarkTheme = isDarkTheme)
            }

            composable(
            route = "roomDetail/{roomName}",
            arguments = listOf(
            navArgument("roomName") {
                type = NavType.StringType
                defaultValue = "default_room"
                nullable = false
            })
            ) { backStackEntry ->
                val roomName = backStackEntry.arguments?.getString("roomName") ?: "default_room"
                Log.d("NAVIGATION", "Passed room name: $roomName")
                RoomDetail(navController = navController,
                    roomName = roomName,
                    isDarkTheme = isDarkTheme
                )
            }

            composable("status_details/{applyId}",
            arguments = listOf(
            navArgument("applyId") {
                type = NavType.StringType
            })
            ) {
                backStackEntry ->
                val applyId = backStackEntry.arguments?.getString("applyId") ?: ""
                val viewModel: RoomViewModel = viewModel()
                StatusDetails(navController = navController, applyId = applyId, viewModel = viewModel, isDarkTheme = isDarkTheme)
            }

            composable(
                route = "leave_screen"
            ) {
                LeaveApply(
                    navController = navController,
                    isDarkTheme = isDarkTheme
                )
            }
        }
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
    val backgroundColor = if (isDarkTheme) Color.Transparent else Color(0xFFE5FFFF)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(48.dp))
            AppLogo(modifier = Modifier.size(200.dp))
            Spacer(modifier = Modifier.height(16.dp))
            ApplicationSection(navController = navController)
            Spacer(modifier = Modifier.height(16.dp))
            AnnouncementSection(navController = navController)
            Spacer(modifier = Modifier.height(16.dp))
            GotoAttendanceOverview(navController = navController)
        }

        IconButton(
            onClick = { onToggleTheme() },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
        ) {
            Icon(imageVector = icon, contentDescription = "Toggle Theme")
        }
    }
}

@Composable
fun AppLogo(modifier: Modifier = Modifier) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = modifier
                .padding(top = 16.dp)
                .size(300.dp)
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
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
fun ApplicationSection(navController: NavController) {
    val apps = listOf(
        AppItem("Meeting Room", R.drawable.meeting_room, "meeting_room_screen"),
        AppItem("Leave", R.drawable.leave, AppScreen.LeaveApplication.name),
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
fun GotoAttendanceOverview(navController: NavController) {
    TextButton(
        onClick = { navController.navigate(AppScreen.Attendance.name) }
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
fun AnnouncementSection(
    navController: NavController,
    viewModel: AnnouncementViewModel = viewModel()
) {
    val announcements by viewModel.announcements.collectAsState()

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Notifications",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        if (announcements.isEmpty()) {
            Text(
                text = "No announcements available.",
                modifier = Modifier.padding(horizontal = 16.dp),
                fontSize = 14.sp
            )
        } else {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp)
            ) {
                items(announcements) { announcement ->
                    AnnouncementCard(
                        title = announcement.title,
                        onClick = {
                            navController.navigate(AppScreen.AnnouncementOverview.name)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun AnnouncementCard(title: String, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(120.dp)
            .padding(5.dp)
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .background(Color.Gray)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = title,
            fontSize = 14.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp)
        )
    }
}