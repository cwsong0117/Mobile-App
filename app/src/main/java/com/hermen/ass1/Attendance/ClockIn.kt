package com.hermen.ass1.Attendance

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.hermen.ass1.R
import kotlinx.coroutines.delay
import java.util.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.location.LocationServices
import com.google.firebase.Timestamp

@Composable
fun ClockIn(
    onBackButtonClicked: () -> Unit,
    modifier: Modifier = Modifier
) {

    val context = LocalContext.current
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }

// Request permission when UI launches
    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }
    var currentLocation by remember { mutableStateOf("Fetching...") }
    LaunchedEffect(Unit) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    currentLocation = "Lat: ${it.latitude}, Lng: ${it.longitude}"
                } ?: run {
                    currentLocation = "Location not available"
                }
            }
        } else {
            currentLocation = "Permission not granted"
        }
    }

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

        Spacer(modifier = Modifier.height(30.dp))

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
            text = "Your expected clock-out time: $clockOutTime",
        )

        Text(text = "Current Location: $currentLocation")

        Spacer(modifier = Modifier.height(30.dp))

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

        AddAttendanceScreen()
    }
}

@Composable
fun AddAttendanceScreen(viewModel: AttendanceViewModel = viewModel()) {
    Column(modifier = Modifier.padding(16.dp)) {
        Button(onClick = {
            val newAttendance = Attendance(
                attendanceID = "ATD1234-123",
                clockInTime = Timestamp.now(),
                clockOutTime = null,
                employeeID = "S123",
                status = "Clocked In"
            )
            viewModel.addAttendance(newAttendance)
        }) {
            Text("Add Attendance")
        }
    }
}

