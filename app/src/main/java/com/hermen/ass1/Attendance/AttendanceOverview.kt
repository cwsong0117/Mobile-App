package com.hermen.ass1.Attendance

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hermen.ass1.R
import kotlinx.coroutines.delay
import java.util.Calendar
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import com.google.firebase.Timestamp
import com.hermen.ass1.BackButton
import com.hermen.ass1.User.SessionManager

fun getMalaysiaTime(): Timestamp {
    val malaysiaTimeZone = TimeZone.getTimeZone("Asia/Kuala_Lumpur")
    val calendar = Calendar.getInstance(malaysiaTimeZone)
    return Timestamp(calendar.time)
}

@Composable
fun AttendanceOverview(
    navController: NavController,
    gotoHistoryScreen: () -> Unit,
    gotoClockInScreen: () -> Unit,
    gotoClockOutScreen: () -> Unit,
    gotoAdminScreen: () -> Unit,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier,
) {

    val landscape = isLandscape()

    //background color
    val backgroundColor = if (isDarkTheme) Color.Black else Color(0xFFE5FFFF)
    //text color
    val textColor = if (isDarkTheme) Color.White else Color.Black

    val malaysiaTimeZone = TimeZone.getTimeZone("Asia/Kuala_Lumpur")

    // Mutable state holding the current Firebase timestamp
    var currentTimestamp by remember { mutableStateOf(getMalaysiaTime()) }

    // Update every second
    LaunchedEffect(Unit) {
        while (true) {
            currentTimestamp = getMalaysiaTime()
            delay(1000L)
        }
    }

    // Convert the timestamp to Calendar
    val calendar = Calendar.getInstance(malaysiaTimeZone).apply {
        time = currentTimestamp.toDate()
    }

    // Format date (e.g., 19/04/2025)
    val dateFormat = remember {
        SimpleDateFormat("EEEE, dd/MM/yyyy", Locale.getDefault()).apply {
            timeZone = malaysiaTimeZone
        }
    }
    val currentDate = dateFormat.format(calendar.time)

    // Extract hour, minute, AM/PM
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)
    val amPm = if (calendar.get(Calendar.AM_PM) == Calendar.AM) "AM" else "PM"
    //Get current time function

    val currentUserId = SessionManager.currentUser?.id

    if (landscape) {
        Column(
            modifier = Modifier
                .background(backgroundColor)
                .fillMaxSize()
        ){
            BackButton(navController = navController, title = "ATTENDANCE", isDarkTheme = isDarkTheme)

            if (currentUserId?.startsWith("A") == true) {
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, end = 16.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clickable { gotoAdminScreen() }
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.more_vertical),
                            contentDescription = "more for admin",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.fillMaxSize(),
                            colorFilter = ColorFilter.tint(
                                if (isDarkTheme) Color.White else Color.Black
                            )
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()), // <-- Make it scrollable,
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
            ) {

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ){
                    Row {
                        Box(
                            modifier = Modifier
                                .padding(5.dp)
                                .size(100.dp)
                                .clip(RoundedCornerShape(32.dp))
                                .background(colorResource(id = R.color.teal_200))
                        ) {
                            Text(
                                text = "%02d".format(hour),
                                color = Color.Black,
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.align(Alignment.Center) // Add padding here (change value as needed)
                            )
                        }

                        Text(
                            text = ":",
                            color = textColor,
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 32.dp) // Add padding here (change value as needed)
                        )

                        Box(
                            modifier = Modifier
                                .padding(5.dp)
                                .size(100.dp)
                                .clip(RoundedCornerShape(32.dp))
                                .background(colorResource(id = R.color.teal_200))
                        ) {
                            Text(
                                text = "%02d".format(minute),
                                color = Color.Black,
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }

                        Text(
                            text = amPm,
                            color = textColor,
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 40.dp) // Add padding here (change value as needed)
                        )
                    }

                    Text(
                        text = currentDate,
                        color = textColor,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 32.dp) // Add padding here (change value as needed)
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    //choose clock in clock out
                    Row {
                        // Clock-In
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clickable { gotoClockInScreen() }
                                .padding(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .padding(top = 16.dp)
                                    .size(150.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.baseline_access_time_24),
                                    contentDescription = "clock-in",
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier.fillMaxSize(),
                                    colorFilter = ColorFilter.tint(
                                        if (isDarkTheme) Color.White else Color.Black
                                    )
                                )
                            }

                            Text(
                                text = "Clock-IN",
                                color = MaterialTheme.colorScheme.onBackground,
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
                        }

                        // Clock-Out
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clickable { gotoClockOutScreen() }
                                .padding(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .padding(top = 16.dp)
                                    .size(150.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.clock_out_logo),
                                    contentDescription = "clock-out",
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier.fillMaxSize(),
                                    colorFilter = ColorFilter.tint(
                                        if (isDarkTheme) Color.White else Color.Black
                                    )
                                )
                            }

                            Text(
                                text = "Clock-OUT",
                                color = MaterialTheme.colorScheme.onBackground,
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(50.dp))

                    Button(
                        onClick = gotoHistoryScreen
                    ) {
                        Text(text = "History")
                    }
                }
            }
        }
    } else {
        Column(
            modifier = Modifier
                .background(backgroundColor)
                .fillMaxSize()
        ){
            BackButton(navController = navController, title = "ATTENDANCE", isDarkTheme = isDarkTheme)

            if (currentUserId?.startsWith("A") == true) {
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, end = 16.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clickable { gotoAdminScreen() }
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.more_vertical),
                            contentDescription = "more for admin",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.fillMaxSize(),
                            colorFilter = ColorFilter.tint(
                                if (isDarkTheme) Color.White else Color.Black
                            )
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()), // <-- Make it scrollable,
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Row {
                    Box(
                        modifier = Modifier
                            .padding(5.dp)
                            .size(100.dp)
                            .clip(RoundedCornerShape(32.dp))
                            .background(colorResource(id = R.color.teal_200))
                    ) {
                        Text(
                            text = "%02d".format(hour),
                            color = Color.Black,
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.align(Alignment.Center) // Add padding here (change value as needed)
                        )
                    }

                    Text(
                        text = ":",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 32.dp) // Add padding here (change value as needed)
                    )

                    Box(
                        modifier = Modifier
                            .padding(5.dp)
                            .size(100.dp)
                            .clip(RoundedCornerShape(32.dp))
                            .background(colorResource(id = R.color.teal_200))
                    ) {
                        Text(
                            text = "%02d".format(minute),
                            color = Color.Black,
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }

                    Text(
                        text = amPm,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 40.dp) // Add padding here (change value as needed)
                    )
                }

                Text(
                    text = currentDate,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 32.dp) // Add padding here (change value as needed)
                )

                Spacer(modifier = Modifier.height(40.dp))

                //choose clock in clock out

                Row {
                    // Clock-In
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable { gotoClockInScreen() }
                            .padding(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(top = 16.dp)
                                .size(150.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.baseline_access_time_24),
                                contentDescription = "clock-in",
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.fillMaxSize(),
                                colorFilter = ColorFilter.tint(
                                    if (isDarkTheme) Color.White else Color.Black
                                )
                            )
                        }

                        Text(
                            text = "Clock-IN",
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }

                    // Clock-Out
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable { gotoClockOutScreen() }
                            .padding(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(top = 16.dp)
                                .size(150.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.clock_out_logo),
                                contentDescription = "clock-out",
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.fillMaxSize(),
                                colorFilter = ColorFilter.tint(
                                    if (isDarkTheme) Color.White else Color.Black
                                )
                            )
                        }

                        Text(
                            text = "Clock-OUT",
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(50.dp))

                Button(
                    onClick = gotoHistoryScreen
                ) {
                    Text(text = "History")
                }

            }
        }
    }

}






