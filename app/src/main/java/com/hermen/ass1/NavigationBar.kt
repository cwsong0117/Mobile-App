package com.hermen.ass1

// no modification in this page unless navigation purpose !!!!

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material3.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import com.wx.myapplication.NavItem

@Composable
fun BackButton(navController: NavController, title: String, isDarkTheme: Boolean) {
    val backgroundColor = if (isDarkTheme) Color.Transparent else Color.White
    val textColor = if (isDarkTheme) Color.White else Color.Black
    val iconColor = if (isDarkTheme) Color.White else Color.Black

    Surface(modifier = Modifier.fillMaxWidth(), color = backgroundColor) {
        Row(
            modifier = Modifier.height(46.dp).fillMaxWidth(),
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
        Divider(color = Color.LightGray, thickness = 1.dp)
    }
}

@Composable
fun BottomNavigationBar(navItems: List<NavItem>,navController: NavHostController, currentRoute: String?, isDarkTheme: Boolean) {
    val backgroundColor = if (isDarkTheme) Color.DarkGray else Color.White
    Box(
        Modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding(bottom = 28.dp)
            .background(backgroundColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Loop through nav items and add them as icon buttons
            navItems.forEach { item ->
                IconButton(onClick = { navController.navigate(item.route) }) {
                    val isSelected = currentRoute == item.route
                    val iconModifier =
                        if (isSelected) Modifier.size(40.dp) else Modifier.size(35.dp)
                    Image(
                        painter = painterResource(id = item.vectorIcon),
                        contentDescription = item.label,
                        contentScale = ContentScale.Crop,
                        modifier = iconModifier
                    )
                }
            }
        }
    }
}



@Composable
fun FooterRail(navItems: List<NavItem>,navController: NavHostController, currentRoute: String?, isDarkTheme: Boolean){
    val backgroundColor = if (isDarkTheme) Color.DarkGray else Color.White
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .padding(start = 8.dp)
            .background(backgroundColor)
    ) {
        NavigationRail(
            modifier = Modifier
                .width(82.dp),
            containerColor = Color.White
        ) {
            navItems.forEach { item ->
                NavigationRailItem(
                    selected = (currentRoute ?: navItems.first().route) == item.route,
                    onClick = { navController.navigate(item.route) },
                    icon = {
                        IconResource(item.vectorIcon, item.label)
                    },
                    alwaysShowLabel = false,
                    colors = NavigationRailItemColors(
                        selectedIconColor = Color.White, selectedTextColor = Color.Black,
                        selectedIndicatorColor = Color.White,
                        unselectedIconColor = Color.Black,
                        unselectedTextColor = Color.Black,
                        disabledIconColor = Color.Black,
                        disabledTextColor = Color.Black
                    )
                )
            }
        }
    }
}

@Composable
fun DrawerContent(navItems: List<NavItem>, navController: NavHostController, currentRoute: String?, isDarkTheme: Boolean) {
    val backgroundColor = if (isDarkTheme) Color.DarkGray else Color.White
    Column(
        modifier = Modifier.padding(top = 30.dp).background(backgroundColor),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        navItems.forEach { item ->
            NavigationDrawerItem(
                label = { Text(
                    text = item.label,
                    fontSize = 16.sp,
                    fontWeight = Bold,
                    modifier = Modifier.padding(top = 12.dp, start = 12.dp)
                )
                },
                selected = (currentRoute ?: navItems.first().route) == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            launchSingleTop = true
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            restoreState = true
                        }
                    }
                },
                icon = { IconResource(item.vectorIcon, item.label) },
                modifier = Modifier.padding(horizontal = 10.dp),
                colors = NavigationDrawerItemDefaults.colors(
                    unselectedContainerColor = Color.Transparent,
                    selectedContainerColor = Color.White,
                )
            )
        }
    }
}

@Composable
fun IconResource(iconRes: Int, description: String) {
    Image(
        painter = painterResource(id = iconRes),
        contentDescription = description,
        contentScale = ContentScale.Crop,
        modifier = Modifier.size(30.dp)
    )
}