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
        AttendanceScreen(modifier = Modifier.padding(innerPadding))
    }
}

@Composable
fun AttendanceScreen(
    viewModel: AttendanceViewModel = viewModel(),
    modifier: Modifier = Modifier) {
    val attendanceList = viewModel.attendance
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        isLoading = true
        viewModel.getAttendance {
            isLoading = false
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            LazyColumn {
                items(attendanceList) { item ->
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(text = "Attendance ID: ${item.attendanceID}")
                        Text(text = "Employee ID: ${item.employeeID}")
                        Text(text = "Clock In: ${item.clockInTime?.toDate()}")
                        Text(text = "Clock Out: ${item.clockOutTime?.toDate()}")
                        Text(text = "Status: ${item.status}")
                    }
                }
            }
        }
    }
}