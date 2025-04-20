package com.hermen.ass1

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import coil.compose.AsyncImage
import com.hermen.ass1.ui.theme.LeaveRequest
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.layout.ContentScale


@Composable
fun ApproveLeave(navController: NavController, isDarkTheme: Boolean) {
    val backgroundColor = if (isDarkTheme) Color.Transparent else Color(0xFFE5FFFF)
    val firestore = FirebaseFirestore.getInstance()
    val leaveList = remember { mutableStateListOf<LeaveRequest>() }

    // 读取 Firestore 数据
    LaunchedEffect(Unit) {
        firestore.collection("Leave")
            .get()
            .addOnSuccessListener { result ->
                leaveList.clear()
                for (document in result) {
                    val leave = document.toObject(LeaveRequest::class.java)
                    leaveList.add(leave)
                }
            }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            leaveList.forEach { leave ->
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .background(Color.White)
                        .padding(16.dp)
                ) {
                    Column {
                        Text("Leave Type: ${leave.leaveType}")
                        Text("Reason: ${leave.reason}")
                        Text("Status: ${leave.status}")
                        Text("Dates: ${leave.leaveDates.joinToString(", ")}")

                        val isImage = leave.evidenceUrl.endsWith(".jpg", ignoreCase = true) ||
                                leave.evidenceUrl.endsWith(".jpeg", ignoreCase = true) ||
                                leave.evidenceUrl.endsWith(".png", ignoreCase = true)

                        val imageUrl = if (isImage) leave.evidenceUrl else "https://upload.wikimedia.org/wikipedia/commons/8/87/PDF_file_icon.svg"

                        AsyncImage(
                            model = imageUrl,
                            contentDescription = "Evidence Preview",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                                .height(200.dp)
                        )

                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ApproveLeavePreview() {
    val navController = androidx.navigation.compose.rememberNavController()
    ApproveLeave(navController, isDarkTheme = false)
}
