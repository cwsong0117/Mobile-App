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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import com.google.firebase.Timestamp

fun getMalaysiaTime(): Timestamp {
    val malaysiaTimeZone = TimeZone.getTimeZone("Asia/Kuala_Lumpur")
    val calendar = Calendar.getInstance(malaysiaTimeZone)
    return Timestamp(calendar.time)
}

@Composable
fun AttendanceOverview(
    gotoHistoryScreen: () -> Unit,
    gotoClockInScreen: () -> Unit,
    gotoClockOutScreen: () -> Unit,
    onBackButtonClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {

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

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth() // makes the box span full width
                    .drawBehind {
                        // draw a shadow-like effect only at the bottom
                        val shadowHeight = 4.dp.toPx()
                        drawRect(
                            color = Color(0x33000000), // translucent black
                            topLeft = Offset(0f, size.height - shadowHeight),
                            size = Size(size.width, shadowHeight)
                        )
                    }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp, top = 12.dp, bottom = 12.dp)
                ) {
                    IconButton(onClick = onBackButtonClicked) {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.baseline_arrow_back_ios_new_24),
                            contentDescription = "Back"
                        )
                    }

                    Text(
                        text = "ATTENDANCE",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
        }

    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row {
                Box(
                    modifier = Modifier
                        .padding(5.dp)
                        .size(100.dp)
                        .clip(RoundedCornerShape(32.dp))
                        .background(colorResource(id = R.color.teal_200))
                ){
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
                    color = Color.Black,
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
                ){
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
                    modifier = Modifier.padding(top = 32.dp) // Add padding here (change value as needed)
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
                            modifier = Modifier.fillMaxSize()
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
                            modifier = Modifier.fillMaxSize()
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
            ){
                Text(text = "History")
            }

        }
    }
}





