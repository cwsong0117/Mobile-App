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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable

@Composable
fun ApproveLeave(navController: NavController, isDarkTheme: Boolean) {
    val backgroundColor = if (isDarkTheme) Color.Transparent else Color(0xFFE5FFFF)
    val firestore = FirebaseFirestore.getInstance()
    val leaveList = remember { mutableStateListOf<LeaveRequest>() }
    val scrollState = rememberScrollState()
    val user = SessionManager.currentUser
    val approveStatusMap = remember { mutableStateMapOf<LeaveRequest, String?>() }
    var refreshTrigger by remember { mutableStateOf(false) }

    // 获取数据
    LaunchedEffect(refreshTrigger) {
        firestore.collection("Leave")
            .whereEqualTo("status", "pending") // ✅ 只拿 status = "pending" 的
            .get()
            .addOnSuccessListener { result ->
                leaveList.clear()
                for (document in result) {
                    val leave = document.toObject(LeaveRequest::class.java)
                    leaveList.add(leave)
                }
            }
    }

    val context = LocalContext.current
    val db = Firebase.firestore

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
            leaveList.forEach { leave ->
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .background(Color.White)
                        .padding(16.dp)
                ) {
                    Column {
                        Text("Name: ${leave.name}")
                        Text("Leave Type: ${leave.leaveType}")
                        Text("Reason: ${leave.reason}")
                        Text("Status: ${leave.status}")
                        Text("Dates: ${leave.leaveDates.joinToString(", ")}")

                        AsyncImage(
                            model = leave.evidenceUrl,
                            contentDescription = "Leave Evidence Image",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .padding(top = 8.dp),
                            onError = {
                                Log.e("Coil", "Image load failed: ${it.result.throwable}")
                            }
                        )

                        Text(
                            text = "Evidence: Click to open",
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .padding(top = 8.dp)
                                .clickable {
                                    val intent = Intent(Intent.ACTION_VIEW).apply {
                                        data = Uri.parse(leave.evidenceUrl)
                                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    }
                                    context.startActivity(intent)
                                }
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        val currentStatus = approveStatusMap[leave]

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Button(
                                onClick = {
                                    approveStatusMap[leave] = if (currentStatus == "approve") null else "approve"
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (currentStatus == "approve") Color(0xFF4CAF50) else MaterialTheme.colorScheme.primary
                                ),
                                modifier = Modifier.padding(end = 8.dp)
                            ) {
                                Text("Approve")
                            }

                            Button(
                                onClick = {
                                    approveStatusMap[leave] = if (currentStatus == "reject") null else "reject"
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (currentStatus == "reject") Color(0xFFFF5252) else MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Text("Reject")
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            Button(
                onClick = {
                    var totalToUpdate = approveStatusMap.count { it.value != null }
                    var updatedCount = 0
                    approveStatusMap.forEach { (leave, decision) ->
                        if (decision != null) {
                            db.collection("Leave")
                                .whereEqualTo("evidenceUrl", leave.evidenceUrl)
                                .get()
                                .addOnSuccessListener { querySnapshot ->
                                    for (document in querySnapshot) {
                                        db.collection("Leave").document(document.id)
                                            .update("status", decision)
                                            .addOnSuccessListener {
                                                Log.d("Confirm", "Status updated to $decision for ${leave.reason}")
                                                updatedCount++
                                                if (updatedCount == totalToUpdate) {
                                                    approveStatusMap.clear()
                                                    Toast.makeText(context, "All selections confirmed.", Toast.LENGTH_SHORT).show()
                                                    refreshTrigger = !refreshTrigger // ⬅️ 触发重新加载
                                                }
                                            }
                                            .addOnFailureListener {
                                                Log.e("Confirm", "Failed to update $decision for ${leave.reason}")
                                            }
                                    }
                                }
                        }
                    }
                    approveStatusMap.clear()
                    Toast.makeText(context, "All selections confirmed.", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(16.dp)
            ) {
                Text("Confirm")
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}


@Preview(showBackground = true)
@Composable
fun ApproveLeavePreview() {
    val navController = androidx.navigation.compose.rememberNavController()
    ApproveLeave(navController, isDarkTheme = false)
}