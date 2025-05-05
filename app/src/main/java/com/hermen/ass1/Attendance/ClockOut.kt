package com.hermen.ass1.Attendance

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.hermen.ass1.R
import kotlinx.coroutines.delay
import java.util.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.hermen.ass1.BackButton
import com.hermen.ass1.User.SessionManager
import java.text.SimpleDateFormat

@Composable
fun ClockOut(
    navController: NavController,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
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

    if(landscape){
        Column(
            modifier = Modifier
                .background(backgroundColor)
                .fillMaxSize()
        ){
            BackButton(navController = navController, title = "CLOCK OUT", isDarkTheme = isDarkTheme)

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()), // <-- Make it scrollable,
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
            ){
                Column(
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
                ClockOutScreen(isDarkTheme = isDarkTheme)
            }
        }

    }else{
        Column(
            modifier = Modifier
                .background(backgroundColor)
                .fillMaxSize()
        ){
            BackButton(navController = navController, title = "CLOCK OUT", isDarkTheme = isDarkTheme)
            Column(
                modifier = Modifier.fillMaxSize(),
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

                Spacer(modifier = Modifier.height(16.dp))

                ClockOutScreen(isDarkTheme = isDarkTheme)

            }
        }
    }

}

@Composable
fun ClockOutScreen(
    viewModel: AttendanceViewModel = viewModel(),
    isDarkTheme: Boolean
) {
    val employeeID = SessionManager.currentUser?.id

    //text color
    val textColor = if (isDarkTheme) Color.White else Color.Black

    var isEarlyLeaveConfirmed by rememberSaveable(employeeID) { mutableStateOf(false) }
    var message by rememberSaveable(employeeID) { mutableStateOf("") }
    val showEarlyLeaveDialog = viewModel.showEarlyLeaveDialog
    val showSuccessDialog = viewModel.showSuccessDialog

    // Fetch latest clock-in when screen is first shown
    LaunchedEffect(Unit) {
        if (employeeID != null) {
            viewModel.fetchLatestClockIn(employeeID)
        }
    }

    val latestClockIn = viewModel.latestClockIn.value
    val malaysiaTimeZone = TimeZone.getTimeZone("Asia/Kuala_Lumpur")

    val timeFormat = remember {
        SimpleDateFormat("HH:mm:ss dd-MM-yyyy", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("Asia/Kuala_Lumpur")
        }
    }

    val now = Calendar.getInstance(TimeZone.getTimeZone("Asia/Kuala_Lumpur"))
    val shiftEnd: Calendar? = latestClockIn?.toDate()?.let { clockInDate ->
        Calendar.getInstance(TimeZone.getTimeZone("Asia/Kuala_Lumpur")).apply {
            time = clockInDate
            add(Calendar.HOUR_OF_DAY, 8)
        }
    }

    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        ) {
        Text(text = "Clock Out", fontSize = 24.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(8.dp))

        if (latestClockIn != null && shiftEnd != null) {
            Text(
                text = "Last Clock-In: ${timeFormat.format(latestClockIn.toDate())}",
                color = textColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Shift Ends At: ${timeFormat.format(shiftEnd.time)}",
                color = textColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            if (latestClockIn != null && shiftEnd != null) {
                val clockInCal = Calendar.getInstance(malaysiaTimeZone).apply {
                    time = latestClockIn.toDate()
                }
                val todayCal = Calendar.getInstance(malaysiaTimeZone)

                val sameDay = clockInCal.get(Calendar.YEAR) == todayCal.get(Calendar.YEAR) &&
                        clockInCal.get(Calendar.DAY_OF_YEAR) == todayCal.get(Calendar.DAY_OF_YEAR)

                if (!sameDay) {
                    message = "No clock-in found for today. Nothing to clock out from."
                    return@Button
                }

                if (now.before(shiftEnd)) {
                    viewModel.showEarlyLeaveDialog = true
                } else {
                    // Normal clock out
                    if (employeeID != null) {
                        viewModel.clockOut(
                            employeeID = employeeID,
                            isEarlyLeave = false,
                            onSuccess = {
                                message = "Clocked out successfully"
                                viewModel.fetchLatestClockIn(employeeID)
                                viewModel.showSuccessDialog = true
                            },
                            onError = {
                                message = "Error: ${it.message}"
                            }
                        )
                    }
                }
            } else {
                message = "Clock-in record not found."
            }
        }) {
            Text("Clock Out")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (message.isNotEmpty()) {
            Text(text = message, color = Color.Red)
        }
    }

    if (showEarlyLeaveDialog) {

        LeaveEarlyDialog(
            onConfirm = {
                viewModel.showEarlyLeaveDialog = false
                isEarlyLeaveConfirmed = true
                if (employeeID != null) {
                    viewModel.clockOut(
                        employeeID = employeeID,
                        isEarlyLeave = true,
                        onSuccess = {
                            message = "Clocked out early successfully"
                            viewModel.fetchLatestClockIn(employeeID)
                        },
                        onError = {
                            message = "Error: ${it.message}"
                        }
                    )
                }
            },
            onDismiss = { viewModel.showEarlyLeaveDialog = false },
            isDarkTheme = isDarkTheme
        )
    }

    if (showSuccessDialog) {
        ClockOutSuccessDialog(onDismiss = { viewModel.showSuccessDialog = false }, isDarkTheme = isDarkTheme)
    }
}


@Composable
fun ClockOutSuccessDialog(
    onDismiss: () -> Unit,
    isDarkTheme: Boolean)
{
    val backgroundColor = if (isDarkTheme) Color.DarkGray else Color.White
    //text color
    val textColor = if (isDarkTheme) Color.White else Color.Black

    val landscape = isLandscape()

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .width(320.dp) // 👈 Adjust the width as needed
                .wrapContentHeight()
                .background(backgroundColor, shape = RoundedCornerShape(16.dp))
                .padding(24.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Clock-Out Successful",
                    color = textColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )

                Spacer(modifier = Modifier.height(if (landscape) 8.dp else 16.dp))

                Image(
                    painter = painterResource(id = R.drawable.tick),
                    contentDescription = "success",
                    modifier = Modifier.size(if (landscape) 100.dp else 150.dp),
                    contentScale = ContentScale.Fit
                )

                Spacer(modifier = Modifier.height(if (landscape) 8.dp else 16.dp))

                Text("You have successfully clock-out.", color = textColor,)
                Text("Goodbye", color = textColor,)

                Spacer(modifier = Modifier.height(24.dp))

                Button(onClick = onDismiss) {
                    Text("OK")
                }
            }
        }
    }
}

@Composable
fun LeaveEarlyDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    isDarkTheme: Boolean
) {

    val landscape = isLandscape()

    val backgroundColor = if (isDarkTheme) Color.DarkGray else Color.White
    //text color
    val textColor = if (isDarkTheme) Color.White else Color.Black

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .width(320.dp)
                .wrapContentHeight()
                .background(backgroundColor, shape = RoundedCornerShape(16.dp))
                .padding(24.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Leave Early?",
                    color = textColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )

                Spacer(modifier = Modifier.height(if (landscape) 8.dp else 16.dp))

                Image(
                    painter = painterResource(id = R.drawable.exit),
                    contentDescription = "Leave Early",
                    modifier = Modifier.size(if (landscape) 100.dp else 150.dp),
                    contentScale = ContentScale.Fit
                )

                Spacer(modifier = Modifier.height(if (landscape) 8.dp else 16.dp))

                Text(
                    text = "Your shift hasn't ended yet.",
                    color = textColor,
                    textAlign = TextAlign.Center
                    )
                Text(
                    text ="Are you sure you want to clock out early?",
                    color = textColor,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(if (landscape) 16.dp else 24.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(onClick = onConfirm) {
                        Text("Yes, Leave Early")
                    }
                    OutlinedButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                }
            }
        }
    }
}




