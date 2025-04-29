package com.hermen.ass1.Attendance

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.hermen.ass1.R
import kotlinx.coroutines.delay
import java.util.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.hermen.ass1.User.SessionManager
import java.text.SimpleDateFormat

@Composable
fun ClockOut(
    navController: NavController,
    isDarkTheme: Boolean,
//    onBackButtonClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    //Get current time function
    var currentTime by remember { mutableStateOf(Calendar.getInstance()) }

    // Update every second
    LaunchedEffect(Unit) {
        while (true) {
            currentTime = Calendar.getInstance()
            delay(1000L)
        }
    }

    val hour = (currentTime.get(Calendar.HOUR_OF_DAY) + 8) % 24
    val minute = currentTime.get(Calendar.MINUTE)
    //Get current time function

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
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 32.dp) // Add padding here (change value as needed)
            )

            Box(
                modifier = Modifier
                    .padding(5.dp)
                    .size(100.dp)
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
        }

        Spacer(modifier = Modifier.height(20.dp))

        SessionManager.currentUser?.let { ClockOutScreen(employeeID = it.id) }

        Spacer(modifier = Modifier.height(20.dp))

        TextButton(
            onClick = { navController.popBackStack() } // 🔹 Now it correctly goes back
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Back",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun ClockOutScreen(
    employeeID: String,
    viewModel: AttendanceViewModel = viewModel()
) {
    var showSuccessDialog by remember { mutableStateOf(false) }

    var message by remember { mutableStateOf("") }
    var showEarlyLeaveDialog by remember { mutableStateOf(false) }
    var isEarlyLeaveConfirmed by remember { mutableStateOf(false) }

    // Fetch latest clock-in when screen is first shown
    LaunchedEffect(Unit) {
        viewModel.fetchLatestClockIn(employeeID)
    }

    val latestClockIn = viewModel.latestClockIn.value
    val malaysiaTimeZone = TimeZone.getTimeZone("Asia/Kuala_Lumpur")
    val todayFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).apply {
        timeZone = malaysiaTimeZone
    }

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

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Clock Out", fontSize = 24.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (latestClockIn != null)
                "Latest Clock-In: ${timeFormat.format(latestClockIn.toDate())}"
            else "Fetching clock-in info...",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            if (latestClockIn != null && shiftEnd != null) {
                val clockInDateStr = todayFormatter.format(latestClockIn!!.toDate())
                val todayDateStr = todayFormatter.format(Date())

                if (clockInDateStr != todayDateStr) {
                    message = "No clock-in found for today. Nothing to clock out from."
                    return@Button
                }

                if (now.before(shiftEnd)) {
                    showEarlyLeaveDialog = true
                } else {
                    // Normal clock out
                    viewModel.clockOut(
                        employeeID = employeeID,
                        isEarlyLeave = false,
                        onSuccess = {
                            message = "Clocked out successfully"
                            viewModel.fetchLatestClockIn(employeeID)
                        },
                        onError = {
                            message = "Error: ${it.message}"
                        }
                    )
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
                showEarlyLeaveDialog = false
                isEarlyLeaveConfirmed = true
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
            },
            onDismiss = { showEarlyLeaveDialog = false }
        )
    }
}


@Composable
fun ClockOutSuccessDialog(onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .width(320.dp) // 👈 Adjust the width as needed
                .wrapContentHeight()
                .background(Color.White, shape = RoundedCornerShape(16.dp))
                .padding(24.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Clock-Out Successful",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Image(
                    painter = painterResource(id = R.drawable.tick),
                    contentDescription = "success",
                    modifier = Modifier.size(150.dp),
                    contentScale = ContentScale.Fit
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("You have successfully clock-out.")
                Text("Goodbye")

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
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .width(320.dp)
                .wrapContentHeight()
                .background(Color.White, shape = RoundedCornerShape(16.dp))
                .padding(24.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Leave Early?",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Image(
                    painter = painterResource(id = R.drawable.exit),
                    contentDescription = "Leave Early",
                    modifier = Modifier.size(150.dp),
                    contentScale = ContentScale.Fit
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("Your shift hasn't ended yet.")
                Text("Are you sure you want to clock out early?")

                Spacer(modifier = Modifier.height(24.dp))

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




