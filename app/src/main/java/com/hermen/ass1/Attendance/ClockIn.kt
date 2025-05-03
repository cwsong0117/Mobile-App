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
import androidx.compose.material3.Button
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.hermen.ass1.R
import kotlinx.coroutines.delay
import java.util.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.location.LocationServices
import android.location.Geocoder
import android.location.Location
import android.os.Looper
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.hermen.ass1.User.SessionManager
import java.text.SimpleDateFormat
import java.util.Locale
import com.google.android.gms.location.Priority
import com.google.firebase.firestore.FirebaseFirestore
import com.hermen.ass1.ui.theme.LeaveRequest
import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import com.hermen.ass1.BackButton

@Composable
fun ClockIn(
    navController: NavController,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    val landscape = isLandscape()

    val backgroundColor = if (isDarkTheme) Color.Black else Color(0xFFE5FFFF)

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

    // Format date (e.g., sat, 19/04/2025)
    val dateFormat = remember {
        SimpleDateFormat("EEEE, dd/MM/yyyy", Locale.getDefault()).apply {
            timeZone = malaysiaTimeZone
        }
    }
    val currentDate = dateFormat.format(calendar.time)
    //check if its weekend
    val currentDay = remember {
        Calendar.getInstance(malaysiaTimeZone).get(Calendar.DAY_OF_WEEK)
    }

    // Extract hour, minute, AM/PM
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)
    val amPm = if (calendar.get(Calendar.AM_PM) == Calendar.AM) "AM" else "PM"
    //Get current time function

    if (landscape) {
        Column(
            modifier = Modifier
                .background(backgroundColor)
                .fillMaxSize()
        ){
            BackButton(navController = navController, title = "CLOCK IN", isDarkTheme = isDarkTheme)
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
                }

                if ( currentDay != Calendar.SATURDAY) {
                    AddAttendanceScreen()
                } else {
                    // Optional: Show a message if it's weekend
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Hooray! It's the weekend.",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        )
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
            BackButton(navController = navController, title = "CLOCK IN", isDarkTheme = isDarkTheme)
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

                Spacer(modifier = Modifier.height(30.dp))

                if ( currentDay != Calendar.SATURDAY) {
                    AddAttendanceScreen()
                } else {
                    // Optional: Show a message if it's weekend
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Hooray! It's the weekend.",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }

}

@Composable
fun AddAttendanceScreen(viewModel: AttendanceViewModel = viewModel()) {

    var isCheckingAttendance by rememberSaveable { mutableStateOf(true) }
    val lifecycleOwner = LocalLifecycleOwner.current

    var showSuccessDialog by rememberSaveable { mutableStateOf(false) }
    var showNotAtWorkplaceDialog by rememberSaveable { mutableStateOf(false) }
    var clockedInToday by remember { mutableStateOf<Attendance?>(null) }

    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

// tarumt location
//    val workplaceLat = 3.2154587237369303
//    val workplaceLng = 101.72655709533397

    //kajang location
    val workplaceLat = 2.9935
    val workplaceLng = 101.7870
    val allowedRadius = 200f // meters

    var userLocation by remember { mutableStateOf<Location?>(null) }
    var userAddress by rememberSaveable { mutableStateOf("Fetching address...") }
    var permissionGranted by rememberSaveable { mutableStateOf(false) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        permissionGranted = isGranted
        if (!isGranted) {
            Toast.makeText(context, "Location permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    // 1. Request location permission
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

    // 2. Fetch latest attendance for validation
    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            SessionManager.currentUser?.let { user ->
                isCheckingAttendance = true
                val attendance = viewModel.getLatestAttendanceForToday(user.id)
                clockedInToday = attendance  // this will be null if no record
                isCheckingAttendance = false // ✅ ensure it's always turned off
            } ?: run {
                isCheckingAttendance = false // ✅ handle if user is null
            }
        }
    }


    // 3. Get current location if permission is granted
    LaunchedEffect(permissionGranted) {
        if (permissionGranted && userLocation == null) {
            val locationRequest = LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,
                1000L
            )
                .setWaitForAccurateLocation(true)
                .setMaxUpdates(1)
                .build()

            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    val location = result.lastLocation
                    if (location != null) {
                        userLocation = location

                        val geocoder = Geocoder(context, Locale.getDefault())
                        val addresses = geocoder.getFromLocation(
                            location.latitude,
                            location.longitude,
                            1
                        )

                        userAddress = if (!addresses.isNullOrEmpty()) {
                            addresses[0].getAddressLine(0)
                        } else {
                            "Address not found"
                        }

                        // ✅ Clean up location callback after getting location
                        fusedLocationClient.removeLocationUpdates(this)
                    }
                }
            }

            // 🔁 Start receiving location updates
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }


    //check leaves list
    val firestore = FirebaseFirestore.getInstance()
    val leaveList = remember{ mutableStateListOf<LeaveRequest>() }
    var onLeaveToday by rememberSaveable { mutableStateOf(false) } // <-- add this to store leave status

    LaunchedEffect(Unit) {
        firestore.collection("Leave")
            .whereEqualTo("status", "approve")
            .whereEqualTo("id", SessionManager.currentUser?.id)
            .get()
            .addOnSuccessListener { result ->
                leaveList.clear()
                onLeaveToday = false

                val malaysiaTimeZone = TimeZone.getTimeZone("Asia/Kuala_Lumpur")
                val calendar = Calendar.getInstance(malaysiaTimeZone).apply {
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }

                val formatter = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).apply {
                    timeZone = malaysiaTimeZone
                }

                val todayMalaysia = formatter.format(calendar.time)
                Log.d("LeaveCheck", "Today's Malaysia Date: $todayMalaysia") // 👈 Log today's date

                for (document in result) {
                    val leave = document.toObject(LeaveRequest::class.java)
                    leaveList.add(leave)

                    Log.d("LeaveCheck", "Leave Dates from Firestore: ${leave.leaveDates.joinToString()}") // 👈 Log leave dates

                    if (leave.leaveDates.any { it.trim() == todayMalaysia }) {
                        Log.d("LeaveCheck", "Match found! Today is a leave day.") // 👈 Log if matched
                        onLeaveToday = true
                        break
                    }
                }
            }

    }
//    check leaves list


    val timeFormat = remember {
        SimpleDateFormat("HH:mm", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("Asia/Kuala_Lumpur")
        }
    }

    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally) {

        Text(
            text = "Your current location"
        )

        Text(
            text = userLocation?.let {
                "Lat: ${it.latitude}, Lng: ${it.longitude}"
            } ?: "Fetching location..."
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = userAddress,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        when {
            isCheckingAttendance -> {
                CircularProgressIndicator()
            }
            onLeaveToday -> {
                Text(
                    text = "You are on approved leave today. No clock-in required.",
                    color = Color.Red
                )
            }
            clockedInToday != null -> {
                val clockInTime = clockedInToday!!.clockInTime?.toDate()
                val shiftEndTime = Calendar.getInstance().apply {
                    time = clockInTime!!
                    add(Calendar.HOUR_OF_DAY, 8)
                }

                Text("You have already clocked in.")
                Text("Shift: ${timeFormat.format(clockInTime)} - ${timeFormat.format(shiftEndTime.time)}")
            }
            else -> {
                Button(onClick = {
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
                                    showSuccessDialog = true
                                    clockedInToday = newAttendance // Update UI immediately
                                }
                            }
                        } else {
                            showNotAtWorkplaceDialog = true
                        }
                    } else {
                        Toast.makeText(context, "Location not ready yet", Toast.LENGTH_SHORT).show()
                    }
                }) {
                    Text("Clock-IN")
                }
            }
        }


        if (showSuccessDialog) {
            SuccessDialog(onDismiss = { showSuccessDialog = false })
        } else if (showNotAtWorkplaceDialog) {
            NotAtWorkPlaceDialog(onDismiss = { showNotAtWorkplaceDialog = false })
        }
    }
}


@Composable
fun SuccessDialog(onDismiss: () -> Unit) {
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
                    text = "Clock-In Successful",
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

                Text("You have successfully clocked in at your workplace.")

                Spacer(modifier = Modifier.height(24.dp))

                Button(onClick = onDismiss) {
                    Text("OK")
                }
            }
        }
    }
}


@Composable
fun NotAtWorkPlaceDialog(onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .width(320.dp)
                .wrapContentHeight()
                .background(Color.White, shape = RoundedCornerShape(16.dp))
                .padding(24.dp),
            contentAlignment = Alignment.Center // 👈 THIS LINE CENTERS THE COLUMN
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Not at workplace!",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Image(
                    painter = painterResource(id = R.drawable.location_logo),
                    contentDescription = "Not at workplace",
                    modifier = Modifier.size(150.dp),
                    contentScale = ContentScale.Fit
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("You are not at your workplace.")
                Text("Unable to clock in now.")

                Spacer(modifier = Modifier.height(24.dp))

                Button(onClick = onDismiss) {
                    Text("OK")
                }
            }
        }
    }
}



