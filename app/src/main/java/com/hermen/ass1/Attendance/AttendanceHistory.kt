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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import java.text.SimpleDateFormat
import java.util.*


@Composable
fun AttendanceHistory(onBackButtonClicked: () -> Unit){
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
                        text = "HISTORY",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
        }
    ) { innerPadding ->
        HistoryList(modifier = Modifier.padding(innerPadding))
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
                val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault())
                val dateStr = clockInDate?.let { dateFormat.format(it) } ?: "Unknown Date"
                val dayStr = clockInDate?.let { dayFormat.format(it) } ?: ""

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
                                Text(text = dayStr, fontWeight = FontWeight.Bold)
                                Text(text = dateStr, style = MaterialTheme.typography.bodyMedium)
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
                                Text("Clock In: ${item.clockInTime?.toDate()}")
                                Text("Clock Out: ${item.clockOutTime?.toDate() ?: "Still working"}")
                                Text("Status: ${item.status}")
                            }
                        }
                    }
                }
            }
        }
    }
}
