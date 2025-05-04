package com.hermen.ass1.Attendance

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hermen.ass1.User.SessionManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.hermen.ass1.BackButton
import java.text.SimpleDateFormat
import java.util.*


@Composable
fun AttendanceHistory(
    navController: NavController,
    isDarkTheme: Boolean
    ){
    val backgroundColor = if (isDarkTheme) Color.Black else Color(0xFFE5FFFF)

    Column(
        modifier = Modifier
            .background(backgroundColor)
            .fillMaxSize()
    ){
        BackButton(navController = navController, title = "HISTORY", isDarkTheme = isDarkTheme)
        HistoryList(isDarkTheme = isDarkTheme)
    }
}

@Composable
fun HistoryList(
    viewModel: AttendanceViewModel = viewModel(),
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isDarkTheme) Color.Black else Color.White
    //text color
    val textColor = if (isDarkTheme) Color.White else Color.Black

    val isTablet = isTablet()
    val selectedAttendance = viewModel.selectedHistoryAttendance

    val attendanceList = viewModel.attendance
    val currentUserId = SessionManager.currentUser?.id
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        isLoading = true
        viewModel.getAttendance {
            isLoading = false
        }
    }

    if (isLoading) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        if(isTablet){
            Row(modifier = Modifier.fillMaxSize()) {
                // Left: List
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp)
                ) {
                    items(
                        attendanceList
                            .filter { it.employeeID == currentUserId }
                            .sortedByDescending { it.clockInTime?.toDate() }
                    ) { item ->
                        val clockInDate = item.clockInTime?.toDate()
                        val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault()).apply {
                            timeZone = TimeZone.getTimeZone("Asia/Kuala_Lumpur")
                        }
                        val clockInStr = clockInDate?.let {
                            SimpleDateFormat("dd MMM yyyy HH:mm:ss", Locale.getDefault()).apply {
                                timeZone = TimeZone.getTimeZone("Asia/Kuala_Lumpur")
                            }.format(it)
                        } ?: "Unknown Date"

                        Card(
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .clickable { viewModel.selectAttendance(item) }
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(text = dayFormat.format(clockInDate ?: Date()), fontWeight = FontWeight.Bold)
                                Text(text = clockInStr, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }

                // Right: Details of selected attendance
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(16.dp)
                ) {
                    selectedAttendance?.let { item ->
                        val clockInDate = item.clockInTime?.toDate()
                        val clockOutDate = item.clockOutTime?.toDate()

                        val formatter = SimpleDateFormat("dd MMM yyyy HH:mm:ss", Locale.getDefault()).apply {
                            timeZone = TimeZone.getTimeZone("Asia/Kuala_Lumpur")
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                                .background(backgroundColor),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text ="Attendance ID: ${item.attendanceID}",
                                color = textColor,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text ="Clock In: ${clockInDate?.let { formatter.format(it) } ?: "Unknown"}",
                                color = textColor,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Clock Out: ${clockOutDate?.let { formatter.format(it) } ?: "Still working"}",
                                color = textColor,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                                )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text ="Status: ${item.status}",
                                color = textColor,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } ?: Text(
                        text="Select a record to see details.",
                        color = textColor,
                        )
                }
            }

        }else{
            LazyColumn(modifier = modifier.padding(8.dp)) {
                items(
                    attendanceList
                        .filter { it.employeeID == currentUserId }
                        .sortedByDescending { it.clockInTime?.toDate() }
                ) { item ->
                    val clockInDate = item.clockInTime?.toDate()
                    val clockOutDate = item.clockOutTime?.toDate()

                    val dateFormat = SimpleDateFormat("dd MMM yyyy HH:mm:ss", Locale.getDefault()).apply {
                        timeZone = TimeZone.getTimeZone("Asia/Kuala_Lumpur")
                    }
                    val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault()).apply {
                        timeZone = TimeZone.getTimeZone("Asia/Kuala_Lumpur")
                    }

                    val clockInStr = clockInDate?.let { dateFormat.format(it) } ?: "Unknown Date"
                    val clockOutStr = clockOutDate?.let { dateFormat.format(it) } ?: "Still working"

                    val isExpanded = viewModel.isHistoryExpanded(item.attendanceID)

                    Card(
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .clickable {
                                viewModel.toggleHistoryExpansion(item.attendanceID)
                            }
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Header
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column {
                                    val dayStr = clockInDate?.let { dayFormat.format(it) } ?: ""
                                    Text(text = dayStr, fontWeight = FontWeight.Bold)
                                    Text(text = clockInStr, style = MaterialTheme.typography.bodyMedium)
                                }
                                Icon(
                                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    contentDescription = null
                                )
                            }

                            // Expanded details
                            AnimatedVisibility(visible = isExpanded) {
                                Column(modifier = Modifier.padding(top = 12.dp)) {
                                    Text("Attendance ID: ${item.attendanceID}")
                                    Text("Clock In: $clockInStr")
                                    Text("Clock Out: $clockOutStr")
                                    Text("Status: ${item.status}")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}



