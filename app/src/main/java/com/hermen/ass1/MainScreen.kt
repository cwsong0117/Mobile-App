package com.hermen.ass1

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.NavigationBarItem
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

enum class AppScreen() {
    Home,
    Attendance,
}

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
    val navController = rememberNavController()
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ){
        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier.fillMaxSize()
        ){
            AppLogo(modifier = Modifier.size(200.dp))
            //AttendanceOverview(navController)
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

//@Composable
//fun AttendanceOverview(
//    navController: NavHostController
//) {
//    NavHost(
//        navController = navController,
//        startDestination = AttendanceOverviewScreen.Start.route
//    ) {
//        composable(route = AttendanceOverviewScreen.Start.route) {
//            //AttendanceOverviewContent(navController)
//        }
//    }
//}
//
//fun composable(route: Any, function: @Composable () -> Unit) {

//}

//@Composable
//fun AttendanceOverviewContent(navController: NavHostController) {
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp)
//    ) {
//        Column {
//            // Back button to navigate to previous screen
//            Button(
//                onClick = { navController.navigateUp() },
//            ) {
//                Text("← Back")
//            }
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            Text(
//                text = "Attendance Overview",
//                color = colorResource(id = R.color.teal_200),
//            )
//
//            // Add your attendance overview content here
//            Box(
//                modifier = Modifier
//                    .padding(16.dp)
//                    .background(Color.LightGray)
//            ) {
//                Text(
//                    text = "Your attendance data will appear here",
//                    modifier = Modifier.padding(16.dp),
//                )
//            }
//        }
//    }
//}


