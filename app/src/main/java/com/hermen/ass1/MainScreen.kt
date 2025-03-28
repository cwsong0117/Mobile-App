package com.hermen.ass1

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Icon
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

data class NavItem(
    val label: String,
    val vectorIcon: ImageVector?,
    val imageRes:Int?
)

@Composable
fun MainScreen(modifier: Modifier = Modifier) {

    val navItemList = listOf(
        NavItem("Home", ImageVector.vectorResource(id = R.drawable.baseline_home_24), null),
        NavItem("Clock", ImageVector.vectorResource(id = R.drawable.baseline_access_time_24), null),
        NavItem("Calendar", null, R.drawable._59592), // PNG Image
        NavItem("Profile", ImageVector.vectorResource(id = R.drawable.baseline_account_circle_24), null) // PNG Image
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                navItemList.forEach { navItem ->
                    NavigationBarItem(
                        selected = false, // Set `selected` properly
                        onClick = { /*TODO*/ },
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
        ) { innerPadding ->
        ContentScreen(modifier = Modifier.padding(innerPadding))
    }
}

@Composable
fun ContentScreen(modifier: Modifier = Modifier) {

}