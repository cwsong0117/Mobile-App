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
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hermen.ass1.R
import com.hermen.ass1.User.SessionManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
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
        HistoryList()
    }
}

@Composable
fun HistoryList(
    viewModel: AttendanceViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val attendanceList = viewModel.attendance
    var isLoading by remember { mutableStateOf(true) }
    val currentUserId = SessionManager.currentUser?.id
    var expandedId by remember { mutableStateOf<String?>(null) }

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
        LazyColumn(modifier = modifier.padding(8.dp)) {
            items(attendanceList.filter { it.employeeID == currentUserId }) { item ->
                val clockInDate = item.clockInTime?.toDate()
                val clockOutDate = item.clockOutTime?.toDate()

                // Set timezone to Malaysia (Asia/Kuala_Lumpur)
                val dateFormat = SimpleDateFormat("dd MMM yyyy HH:mm:ss", Locale.getDefault()).apply {
                    timeZone = TimeZone.getTimeZone("Asia/Kuala_Lumpur")
                }
                val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault()).apply {
                    timeZone = TimeZone.getTimeZone("Asia/Kuala_Lumpur")
                }

                // Format clock-in and clock-out times
                val clockInStr = clockInDate?.let { dateFormat.format(it) } ?: "Unknown Date"
                val clockOutStr = clockOutDate?.let { dateFormat.format(it) } ?: "Still working"

                val isExpanded = expandedId == item.attendanceID

                Card(
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clickable {
                            expandedId = if (isExpanded) null else item.attendanceID
                        }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Header (collapsed view)
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

                        // Expanded view
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


