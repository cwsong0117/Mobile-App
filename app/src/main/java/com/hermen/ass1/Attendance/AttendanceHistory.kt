package com.hermen.ass1.Attendance

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hermen.ass1.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceHistory(onBackButtonClicked: () -> Unit){
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("HISTORY")
                },
                navigationIcon = {
                    IconButton(onClick = onBackButtonClicked) {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.baseline_arrow_back_ios_new_24),
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ),
                modifier = Modifier.shadow(
                    elevation = 4.dp,
                    spotColor = Color.Black
                ),
                windowInsets = WindowInsets(0) // Removes default top padding
            )
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