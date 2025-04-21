package com.hermen.ass1.Attendance

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hermen.ass1.R
import kotlinx.coroutines.delay
import java.util.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hermen.ass1.User.SessionManager

@Composable
fun ClockOut(
    onBackButtonClicked: () -> Unit,
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

    var clockOutTimeString by remember { mutableStateOf("") }

    val clockInTime = clockOutTimeString.toIntOrNull() ?: 0
    val clockOutTime = clockOutTimeString.toIntOrNull()?.plus(9) ?: ""

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
            //test
            //test

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

        Spacer(modifier = Modifier.height(80.dp))


        Text(
            text = "Today's clock-in: $clockInTime",
        )

        Spacer(modifier = Modifier.height(20.dp))

        TextButton(
            onClick = onBackButtonClicked // 🔹 Now it correctly goes back
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Back",
                    color = Color.Black,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        SessionManager.currentUser?.let { ClockOutScreen(employeeID = it.id) }
    }
}

@Composable
fun ClockOutScreen(
    employeeID: String,
    viewModel: AttendanceViewModel = viewModel()
) {
    var message by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Clock Out", fontSize = 24.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            viewModel.clockOut(
                employeeID = employeeID,
                onSuccess = {
                    message = "Clocked out successfully"
                },
                onError = {
                    message = "Error: ${it.message}"
                }
            )
        }) {
            Text("Clock Out")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (message.isNotEmpty()) {
            Text(text = message, color = Color.Red)
        }
    }
}

