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
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
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
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.composable
import com.hermen.ass1.IndicateFooter
import coil.compose.AsyncImage
import com.hermen.ass1.Attendance.AdminScreen
import com.hermen.ass1.PaySlip.PaySlip
import com.hermen.ass1.PaySlip.PaySlipDetailsScreen
import com.hermen.ass1.PaySlip.PaySlipHomeScreen
import com.hermen.ass1.PaySlip.PaySlipHomeScreenForAdmin

enum class AppScreen(@StringRes val title: Int) {
    Home(title = R.string.app_name),
    Attendance(title = R.string.attendance),
    ClockIn(title = R.string.clock_in),
    ClockOut(title = R.string.clock_out),
    AnnouncementOverview(title = R.string.announcement_overview),
    AnnouncementDetail(title = R.string.announcement_detail),
    UserProfile(title = R.string.user_profile),
    LeaveApplication(title = R.string.leave_application),
    ApproveLeave(title = R.string.approve_leave),
    CreateOrEditAnnouncement(title = R.string.create_or_edit_announcement),
    ShowLeave(title = R.string.approve_leave),
    PaySlip(title = R.string.pay_slip),
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
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val navigationType = getNavigationType(context, context.resources.configuration.orientation)

    IndicateFooter(isDarkTheme = isDarkTheme, navigationType = navigationType, navController = navController) {
        AppNavHost(
            navController = navController,
            modifier = modifier,
            isDarkTheme = isDarkTheme,
            onToggleTheme = onToggleTheme
        )
    }
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = AppScreen.Home.name,
        modifier = modifier
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
                navController = navController,
                gotoHistoryScreen = {
                    navController.navigate("attendanceHistory")
                },
                gotoClockInScreen = {
                    navController.navigate(AppScreen.ClockIn.name)
                },
                gotoClockOutScreen = {
                    navController.navigate(AppScreen.ClockOut.name)
                },
                gotoAdminScreen = {
                    navController.navigate("admin_screen")
                },
                isDarkTheme = isDarkTheme,
                modifier = modifier
            )
        }
        composable("attendanceHistory") {
            AttendanceHistory(isDarkTheme = isDarkTheme, navController = navController)
        }
        composable("admin_screen") {
            AdminScreen(isDarkTheme = isDarkTheme, navController = navController)
        }
        composable(AppScreen.LeaveApplication.name) {
            LeaveApplication(navController = navController, isDarkTheme = isDarkTheme)
        }
        composable(AppScreen.ApproveLeave.name) {
            ApproveLeave(navController = navController, isDarkTheme = isDarkTheme)
        }
        composable(AppScreen.ShowLeave.name) {
            ShowLeave(navController = navController, isDarkTheme = isDarkTheme)
        }
        composable(AppScreen.ClockIn.name) {
            ClockIn(navController = navController, isDarkTheme = isDarkTheme, modifier = modifier)
        }
        composable(AppScreen.ClockOut.name) {
            ClockOut(navController = navController, isDarkTheme = isDarkTheme, modifier = modifier)
        }
        composable(AppScreen.AnnouncementOverview.name) {
            AnnouncementOverview(navController = navController, isDarkTheme = isDarkTheme)
        }
        composable("${AppScreen.AnnouncementDetail.name}/{announcementJson}") {
                backStackEntry ->
            val json = backStackEntry.arguments?.getString("announcementJson") ?: ""
            AnnouncementDetailScreen(json, navController, isDarkTheme)
        }
        composable(AppScreen.UserProfile.name) {
            UserProfileScreen(
                nestedNavController = navController,
                rootNavController = LocalRootNavController.current,
                isDarkTheme = isDarkTheme
            )
        }
        composable("meeting_room_screen") {
            MeetingRoomApply(navController = navController, isDarkTheme = isDarkTheme)
        }
        composable("roomDetail/{roomName}") { backStackEntry ->
            val roomName = backStackEntry.arguments?.getString("roomName") ?: "default_room"
            RoomDetail(navController = navController, roomName = roomName, isDarkTheme = isDarkTheme)
        }
        composable("status_details/{applyId}") { backStackEntry ->
            val applyId = backStackEntry.arguments?.getString("applyId") ?: ""
            val viewModel: RoomViewModel = viewModel()
            StatusDetails(navController = navController, applyId = applyId, viewModel = viewModel, isDarkTheme = isDarkTheme)
        }
        composable("leave_screen") {
            LeaveApply(navController = navController, isDarkTheme = isDarkTheme)
        }
        composable(
            route = "CreateOrEditAnnouncementScreen?announcementId={announcementId}",
            arguments = listOf(
                navArgument("announcementId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val announcementId = backStackEntry.arguments?.getString("announcementId")
            CreateOrEditAnnouncement(
                navController = navController,
                announcementId = announcementId,
                isDarkTheme = isDarkTheme
            )
        }
        composable("paySlip") {
            PaySlip(navController = navController, isDarkTheme = isDarkTheme)
        }
        composable("paySlipDetails/{month}/{year}") { backStackEntry ->
            val month = backStackEntry.arguments?.getString("month") ?: ""
            val year = backStackEntry.arguments?.getString("year") ?: ""
            PaySlipDetailsScreen(
                navController = navController,
                month = month,
                year = year,
                isDarkTheme = isDarkTheme
            )
        }
        composable("viewPayslip") {
            PaySlipHomeScreen(navController = navController, isDarkTheme = isDarkTheme)
        }
        composable("managePayslip") {
            PaySlipHomeScreenForAdmin(navController = navController, isDarkTheme = isDarkTheme)
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
    val backgroundColor = if (isDarkTheme) Color.Black else Color(0xFFE5FFFF)
    val iconTint = if (isDarkTheme) Color.White else Color.Black

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(top = 16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(48.dp))
            AppLogo(modifier = Modifier.size(200.dp), isDarkTheme = isDarkTheme)
            Spacer(modifier = Modifier.height(16.dp))
            ApplicationSection(navController = navController, isDarkTheme = isDarkTheme)
            Spacer(modifier = Modifier.height(16.dp))
            AnnouncementSection(navController = navController, isDarkTheme = isDarkTheme)
            Spacer(modifier = Modifier.height(16.dp))
            GotoAttendanceOverview(navController = navController)
        }

        IconButton(
            onClick = { onToggleTheme() },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = "Toggle Theme",
                tint = iconTint
            )
        }
    }
}

@Composable
fun AppLogo(
    modifier: Modifier = Modifier,
    isDarkTheme: Boolean
) {
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
            color = if (isDarkTheme) Color.White else Color.Black,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
fun ApplicationSection(
    navController: NavController,
    isDarkTheme: Boolean
) {
    val apps = listOf(
        AppItem("Meeting Room", R.drawable.meeting_room, "meeting_room_screen"),
        AppItem("Leave", R.drawable.leave, AppScreen.LeaveApplication.name),
        AppItem("Attendance Panel", R.drawable.attendance, "attendanceHistory")
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Application",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp),
            color = if (isDarkTheme) Color.White else Color.Black
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
                    Text(text = app.name,
                        fontSize = 14.sp,
                        color = if (isDarkTheme) Color.White else Color.Black
                    )
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
    isDarkTheme: Boolean,
    viewModel: AnnouncementViewModel = viewModel()
) {
    // Collect the announcements
    val announcements by viewModel.announcements.collectAsState()

    // Add LaunchedEffect to refresh data when this composable enters composition
    LaunchedEffect(Unit) {
        viewModel.loadAnnouncements()
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Announcement",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp),
            color = if (isDarkTheme) Color.White else Color.Black
        )
        Spacer(modifier = Modifier.height(8.dp))
        if (announcements.isEmpty()) {
            Text(
                text = "No announcements available.",
                modifier = Modifier.padding(horizontal = 16.dp),
                fontSize = 14.sp,
                color = if (isDarkTheme) Color.White else Color.Black
            )
        } else {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp)
            ) {
                items(announcements) { announcement ->
                    AnnouncementCard(
                        title = announcement.title,
                        imageUrl = announcement.imageUrl,
                        onClick = {
                            navController.navigate(AppScreen.AnnouncementOverview.name)
                        },
                        isDarkTheme = isDarkTheme
                    )
                }
            }
        }
    }
}

@Composable
fun AnnouncementCard(title: String, imageUrl: String?, onClick: () -> Unit, isDarkTheme: Boolean) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(120.dp)
            .padding(5.dp)
            .clickable(onClick = onClick)
    ) {
        if (!imageUrl.isNullOrEmpty()) {
            AsyncImage(
                model = imageUrl,
                contentDescription = "Announcement Image",
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(Color.Gray)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = title,
            fontSize = 14.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            color = if (isDarkTheme) Color.White else Color.Black
        )
    }
}