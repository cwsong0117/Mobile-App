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

@Composable
fun ClockIn(
    onBackButtonClicked: () -> Unit,
    onBackToHomeClicked: () -> Unit, // 🔹 Function to go back
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

    var clockInTimeString by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }

    val clockInTime = clockInTimeString.toIntOrNull() ?: 0
    val clockOutTime = clockInTimeString.toIntOrNull()?.plus(9) ?: ""

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

        TextField(
            value = name,
            onValueChange = {name = it},
            label = { Text("Enter your name") }
        )

        TextField(
            value = clockInTimeString,
            onValueChange = { newText ->
                // Only allow digits
                if (newText.all { it.isDigit() }) {
                    clockInTimeString = newText
                }
            },
            label = { Text("Enter clock-in time") } ,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Spacer(modifier = Modifier.height(50.dp))

        Text(
            text = "Your name: $name",
        )

        Text(
            text = "Your clock-in time: $clockInTime",
        )

        Text(
            text = "Your clock-out time: $clockOutTime",
        )

        Spacer(modifier = Modifier.height(50.dp))

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

        Spacer(modifier = Modifier.height(40.dp))

        TextButton(
            onClick = onBackToHomeClicked // 🔹 Now it correctly goes back
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Back to Home",
                    color = colorResource(id = R.color.teal_200),
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
