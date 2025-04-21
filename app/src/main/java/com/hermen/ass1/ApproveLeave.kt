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
import android.util.Log
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import com.hermen.ass1.User.SessionManager
import androidx.compose.foundation.clickable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.widget.Toast
import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.material3.ButtonDefaults

@Composable
fun ApproveLeave(navController: NavController, isDarkTheme: Boolean) {
    val backgroundColor = if (isDarkTheme) Color.Transparent else Color(0xFFE5FFFF)
    val firestore = FirebaseFirestore.getInstance()
    val leaveList = remember { mutableStateListOf<LeaveRequest>() }
    val scrollState = rememberScrollState()
    val user = SessionManager.currentUser
    val approvedLeaves = remember { mutableStateListOf<LeaveRequest>() }

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
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundColor),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
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
                                Text("Leave Type: ${leave.name}")
                                Text("Leave Type: ${leave.leaveType}")
                                Text("Reason: ${leave.reason}")
                                Text("Status: ${leave.status}")
                                Text("Dates: ${leave.leaveDates.joinToString(", ")}")

                                val context = LocalContext.current

                                AsyncImage(
                                    model = leave.evidenceUrl,
                                    contentDescription = "Leave Evidence Image",
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp)
                                        .padding(top = 8.dp),
                                    onError = {
                                        Log.e(
                                            "Coil",
                                            "Image load failed: ${it.result.throwable}"
                                        )
                                    }
                                )
                                Text(
                                    text = "Evidence: Click to open",
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier
                                        .padding(top = 8.dp)
                                        .clickable {
                                            val intent =
                                                android.content.Intent(android.content.Intent.ACTION_VIEW)
                                                    .apply {
                                                        data =
                                                            android.net.Uri.parse(leave.evidenceUrl)
                                                        addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                                                    }
                                            context.startActivity(intent)
                                        }
                                )
                            }
                            val isMarked = remember { mutableStateOf(approvedLeaves.contains(leave)) }

                            Button(
                                onClick = {
                                    if (!isMarked.value) {
                                        approvedLeaves.add(leave)
                                        isMarked.value = true
                                        Log.d("Approve", "Marked for approval: ${leave.reason}")
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isMarked.value) Color(0xFF4CAF50) else MaterialTheme.colorScheme.primary
                                ),
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(16.dp)
                            ) {
                                Text("Approve")
                            }

                        }
                    }
                }

                val context = LocalContext.current
                val db = Firebase.firestore
                Spacer(modifier = Modifier.height(30.dp))
                Button(
                    onClick = {
                        approvedLeaves.forEach { leave ->
                            db.collection("Leave")
                                .whereEqualTo("evidenceUrl", leave.evidenceUrl) // 假设 evidenceUrl 是唯一的
                                .get()
                                .addOnSuccessListener { querySnapshot ->
                                    for (document in querySnapshot) {
                                        db.collection("Leave").document(document.id)
                                            .update("status", "approve")
                                            .addOnSuccessListener {
                                                Log.d("Confirm", "Status updated for ${leave.reason}")
                                            }
                                            .addOnFailureListener {
                                                Log.e("Confirm", "Failed to update for ${leave.reason}")
                                            }
                                    }
                                }
                        }
                        approvedLeaves.clear()
                        Toast.makeText(context, "All approved leaves confirmed.", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                ) {
                    Text("Confirm")
                }
                Spacer(modifier = Modifier.height(30.dp))
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