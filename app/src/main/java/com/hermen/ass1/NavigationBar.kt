package com.hermen.ass1

// no modification in this page unless navigation purpose !!!!

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.material3.IconButton
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController

data class NavItem(
    val label: String,
    val vectorIcon: ImageVector?,
    val imageRes:Int?
)

@Composable
fun BottomNavigationBar(modifier: Modifier = Modifier, navController: NavController) {

    val navItemList = listOf(
        NavItem(
            "Home",
            ImageVector.vectorResource(id = R.drawable.baseline_home_24),
            null
        ),
        NavItem(
            "Attendance", ImageVector.vectorResource(id = R.drawable.baseline_access_time_24),
            null
        ),
        NavItem(
            "Calendar",
            null,
            R.drawable._59592
        ), // PNG Image
        NavItem(
            "Profile",
            ImageVector.vectorResource(id = R.drawable.baseline_account_circle_24),
            null
        ) // PNG Image
    )
    NavigationBar {
        navItemList.forEach { navItem ->
            NavigationBarItem(
                selected = false, // Set `selected` properly
                onClick = {
                    navController.navigate(navItem.label)
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