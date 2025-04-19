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
import android.location.Geocoder
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult

import com.google.firebase.auth.FirebaseAuth
import com.hermen.ass1.User.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale
import com.google.android.gms.location.Priority

@Composable
fun ClockIn(
    onBackButtonClicked: () -> Unit,
    modifier: Modifier = Modifier
) {

    val context = LocalContext.current

    // Define workplace location (e.g., Kajang home)
    val workplaceLat = 2.981085096711732
    val workplaceLng = 101.79936946524971
    val allowedRadius = 100f // meters

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        Toast.makeText(context, if (isGranted) "Permission Granted" else "Permission Denied", Toast.LENGTH_SHORT).show()
    }

    // Request permission on launch
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
    var currentAddress by remember { mutableStateOf("Fetching address...") }
    var isAtWorkplace by remember { mutableStateOf<Boolean?>(null) }

    // Get user's location
    LaunchedEffect(Unit) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    val lat = it.latitude
                    val lng = it.longitude
                    currentLocation = "Lat: $lat, Lng: $lng"

                    // Distance check
                    val result = FloatArray(1)
                    Location.distanceBetween(lat, lng, workplaceLat, workplaceLng, result)
                    val distanceInMeters = result[0]

                    isAtWorkplace = distanceInMeters <= allowedRadius

                    Toast.makeText(
                        context,
                        if (isAtWorkplace == true) "You are at the workplace" else "You are not at the workplace",
                        Toast.LENGTH_SHORT
                    ).show()

                    // Reverse geocode
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val geocoder = Geocoder(context, Locale.getDefault())
                            val addresses = geocoder.getFromLocation(lat, lng, 1)

                            withContext(Dispatchers.Main) {
                                currentAddress = addresses?.firstOrNull()?.getAddressLine(0)
                                    ?: "Address not found"
                            }
                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                currentAddress = "Geocoder failed: ${e.message}"
                            }
                        }
                    }
                } ?: run {
                    currentLocation = "Location not available"
                    currentAddress = "No address (location null)"
                }
            }
        } else {
            currentLocation = "Permission not granted"
            currentAddress = "No address (permission missing)"
        }
    }


    //Get current time function
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

    // Extract hour, minute, AM/PM
    val hour = calendar.get(Calendar.HOUR)
    val minute = calendar.get(Calendar.MINUTE)
    val amPm = if (calendar.get(Calendar.AM_PM) == Calendar.AM) "AM" else "PM"
    //Get current time function

    var clockInTimeString by remember { mutableStateOf("") }

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

            Text(
                text = amPm,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 32.dp) // Add padding here (change value as needed)
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

//        TextField(
//            value = clockInTimeString,
//            onValueChange = { newText ->
//                // Only allow digits
//                if (newText.all { it.isDigit() }) {
//                    clockInTimeString = newText
//                }
//            },
//            label = { Text("Enter clock-in time") } ,
//            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
//        )

        Spacer(modifier = Modifier.height(50.dp))

        Text(
            text = "Expected clock-out time: $clockOutTime",
        )

        Text(text = "Current Location: $currentLocation")
        Text(text = "Current Address: $currentAddress")

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
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val workplaceLat = 2.981085096711732
    val workplaceLng = 101.79936946524971
    val allowedRadius = 100f // meters

    var userLocation by remember { mutableStateOf<Location?>(null) }
    var permissionGranted by remember { mutableStateOf(false) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        permissionGranted = isGranted
        if (!isGranted) {
            Toast.makeText(context, "Location permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    // Request permission once
    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            permissionGranted = true
        }
    }

    // Get accurate location when permission is granted
    LaunchedEffect(permissionGranted) {
        if (permissionGranted) {
            val locationRequest = LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,
                1000L // every 1 second if needed
            )
                .setWaitForAccurateLocation(true)
                .setMaxUpdates(1)
                .build()

            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    super.onLocationResult(result)
                    userLocation = result.lastLocation
                    // Optionally stop updates (but setMaxUpdates(1) already handles it)
                }
            }

            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = userLocation?.let {
                "Lat: ${it.latitude}, Lng: ${it.longitude}"
            } ?: "Fetching location..."
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val location = userLocation
                if (location != null) {
                    val result = FloatArray(1)
                    Location.distanceBetween(
                        location.latitude,
                        location.longitude,
                        workplaceLat,
                        workplaceLng,
                        result
                    )
                    val distanceInMeters = result[0]

                    if (distanceInMeters <= allowedRadius) {
                        SessionManager.currentUser?.let { user ->
                            viewModel.generateAttendanceID { generatedID ->
                                val newAttendance = Attendance(
                                    attendanceID = generatedID,
                                    clockInTime = getMalaysiaTime(),
                                    clockOutTime = null,
                                    employeeID = user.id,
                                    status = "Clocked In"
                                )
                                viewModel.addAttendance(newAttendance)
                                Toast.makeText(context, "Clock-in successful", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(context, "You are not at the workplace!", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(context, "Location not ready yet", Toast.LENGTH_SHORT).show()
                }
            }
        ) {
            Text("Clock-IN")
        }
    }
}


@Composable
fun SuccessDialog(onDismiss: () -> Unit) {}


