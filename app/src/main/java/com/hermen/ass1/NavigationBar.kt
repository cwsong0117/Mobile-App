package com.hermen.ass1

// no modification in this page unless navigation purpose !!!!

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

data class NavItem(
    val label: String,
    val vectorIcon: ImageVector?,
    val imageRes:Int?,
    val route: String
)

@Composable
fun BottomNavigationBar(navController: NavController) {

    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    val navItemList = listOf(
        NavItem(
            "Home",
            ImageVector.vectorResource(id = R.drawable.baseline_home_24),
            null,
            AppScreen.Home.name // Route to navigate
        ),
        NavItem(
            "Attendance",
            ImageVector.vectorResource(id = R.drawable.baseline_access_time_24),
            null,
            AppScreen.Attendance.name
        ),
        NavItem(
            "Calendar",
            null,
            R.drawable._59592, // PNG Image
            AppScreen.AnnouncementOverview.name
        ),
        NavItem(
            "Profile",
            ImageVector.vectorResource(id = R.drawable.baseline_account_circle_24),
            null,
            AppScreen.UserProfile.name // Profile route to navigate
        )
    )

    NavigationBar {
        navItemList.forEach { navItem ->
            NavigationBarItem(
                selected = currentRoute == navItem.route, // Mark selected item
                onClick = {
                    navController.navigate(navItem.route) {
                        // Handle popBackStack if necessary to avoid building up the navigation stack
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                label = { Text(text = navItem.label) },
                icon = {
                    when {
                        navItem.vectorIcon != null -> {
                            Icon(imageVector = navItem.vectorIcon,
                                contentDescription = navItem.label,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        navItem.imageRes != null -> {
                            Icon(
                                painter = painterResource(id = navItem.imageRes),
                                contentDescription = navItem.label,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun BackButton(navController: NavController, title: String, isDarkTheme: Boolean) {
    val backgroundColor = if (isDarkTheme) Color.Transparent else Color.White
    val textColor = if (isDarkTheme) Color.White else Color.Black
    val iconColor = if (isDarkTheme) Color.White else Color.Black
    Surface(
        modifier = Modifier
            .fillMaxWidth(),
        color = backgroundColor
    ) {
        Row(
            modifier = Modifier
                .height(46.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_arrow_back_ios_new_24),
                    contentDescription = "Back",
                    modifier = Modifier.size(24.dp),
                    tint = iconColor
                )
            }
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp),
                color = textColor
            )
        }
    }
    Divider(
        color = Color.LightGray,
        thickness = 1.dp
    )
}

@Preview(showBackground = true)
@Composable
fun navBarPreview() {
    BottomNavigationBar(navController = rememberNavController())
}