package com.wx.myapplication

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.PermanentDrawerSheet
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.runtime.getValue
import com.hermen.ass1.AppScreen
import com.hermen.ass1.BottomNavigationBar
import com.hermen.ass1. DrawerContent
import com.hermen.ass1.FooterRail
import com.hermen.ass1.R
import com.hermen.ass1.ui.theme.utils.NavigationType

data class NavItem(
    val label: String,
    val vectorIcon: Int,
    val route: String
)

val NavItems = listOf(
    NavItem("Home", R.drawable.baseline_home_24, AppScreen.Home.name),
    NavItem("Attendance", R.drawable.baseline_access_time_24,  AppScreen.Attendance.name),
    NavItem("MeetingRoom", R.drawable.meeting_nav_bar, "meeting_room_screen"),
    NavItem("Profile", R.drawable.baseline_account_circle_24, AppScreen.UserProfile.name),
)
@Composable
fun IndicateFooter(
    navigationType: NavigationType,
    navController: NavHostController,
    content: @Composable () -> Unit
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    when (navigationType) {
        NavigationType.BOTTOM_NAVIGATION -> {
            Scaffold(
                bottomBar = {
                    BottomNavigationBar(NavItems, navController, currentRoute)
                }
            ) {
                Box(modifier = Modifier.padding(it)) {
                    content()
                }
            }
        }

        NavigationType.NAVIGATION_RAIL -> {
            Row {
                FooterRail(NavItems, navController, currentRoute)
                content()
            }
        }

        NavigationType.PERMANENT_NAVIGATION_DRAWER -> {
            PermanentNavigationDrawer(
                drawerContent = {
                    PermanentDrawerSheet(modifier = Modifier.width(220.dp)) {
                        DrawerContent(NavItems, navController, currentRoute)
                    }
                }
            ) {
                content()
            }
        }
    }
}
