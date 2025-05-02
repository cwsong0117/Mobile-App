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

@Composable
fun ShowLeave(navController: NavController, isDarkTheme: Boolean) {
    val backgroundColor = if (isDarkTheme) Color.Transparent else Color(0xFFE5FFFF)
    val firestore = FirebaseFirestore.getInstance()
    val leaveList = remember { mutableStateListOf<LeaveRequest>() }
    val scrollState = rememberScrollState()
    val user = SessionManager.currentUser
    val approveStatusMap = remember { mutableStateMapOf<LeaveRequest, String?>() }

    // 获取数据
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

                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

        }
    }
}


@Preview(showBackground = true)
@Composable
fun ShowLeavePreview() {
    val navController = androidx.navigation.compose.rememberNavController()
    ShowLeave(navController, isDarkTheme = false)
}