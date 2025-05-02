package com.hermen.ass1.Attendance

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.LocalContentColor
import androidx.compose.material.TextButton
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.hermen.ass1.BackButton
import com.hermen.ass1.R
import com.hermen.ass1.User.SessionManager
import com.hermen.ass1.User.User
import com.hermen.ass1.User.toUser
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

@Composable
fun AdminScreen(
    viewModel: AttendanceViewModel = viewModel(),
    isDarkTheme: Boolean,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isDarkTheme) Color.Black else Color(0xFFE5FFFF)

    val attendanceList = viewModel.attendance
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        isLoading = true
        viewModel.getAttendance {
            isLoading = false
        }
    }

    val usersMap = remember { mutableStateMapOf<String, User>() }

    LaunchedEffect(Unit) {
        Firebase.firestore.collection("User")
            .get()
            .addOnSuccessListener { result ->
                result.documents.forEach { doc ->
                    val user = doc.toUser()
                    usersMap[user.id] = user
                }
            }
    }

    val groupedAttendance = attendanceList.groupBy { it.employeeID }

    val malaysiaTimeZone = TimeZone.getTimeZone("Asia/Kuala_Lumpur")
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).apply {
        timeZone = malaysiaTimeZone
    }
    val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault()).apply {
        timeZone = malaysiaTimeZone
    }

    var showEditDialog by remember { mutableStateOf(false) }
    var showRemoveDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .background(backgroundColor)
            .fillMaxSize()
    ) {
        BackButton(navController = navController, title = "ADMIN PANEL", isDarkTheme = isDarkTheme)

        if (isLoading) {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(modifier = modifier.padding(8.dp)) {
                groupedAttendance.forEach { (employeeID, records) ->
                    item {
                        var expanded by remember { mutableStateOf(false) }
                        val userName = usersMap[employeeID]?.name ?: "Unknown User"

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .clickable { expanded = !expanded },
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = userName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp
                                )

                                AnimatedVisibility(visible = expanded) {
                                    Column {
                                        records.sortedByDescending { it.clockInTime?.toDate() }.forEach { attendance ->
                                            val clockInDate = attendance.clockInTime?.toDate()
                                            val clockOutDate = attendance.clockOutTime?.toDate()

                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 8.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                // Left: Attendance details
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(
                                                        text = "Date: ${clockInDate?.let { dateFormat.format(it) } ?: "N/A"}",
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                    Text("Clock In: ${clockInDate?.let { timeFormat.format(it) } ?: "N/A"}")
                                                    Text("Clock Out: ${clockOutDate?.let { timeFormat.format(it) } ?: "N/A"}")
                                                    Text(
                                                        text = "Status: ${attendance.status ?: "-"}",
                                                        color = if (attendance.status == "Left Early") Color.Red else LocalContentColor.current
                                                    )
                                                }

                                                // Right: Edit & Remove buttons
                                                Column(
                                                    horizontalAlignment = Alignment.End,
                                                    verticalArrangement = Arrangement.Center,
                                                ) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(32.dp)
                                                            .clickable { showEditDialog = true }
                                                    ) {
                                                        Image(
                                                            painter = painterResource(id = R.drawable.edit),
                                                            contentDescription = "edit",
                                                            contentScale = ContentScale.Fit,
                                                            modifier = Modifier.fillMaxSize(),
                                                            colorFilter = ColorFilter.tint(
                                                                if (isDarkTheme) Color.White else Color.Black
                                                            )
                                                        )
                                                    }

                                                    Spacer(modifier = Modifier.height(16.dp))

                                                    Box(
                                                        modifier = Modifier
                                                            .size(32.dp)
                                                            .clickable { showRemoveDialog = true }
                                                    ) {
                                                        Image(
                                                            painter = painterResource(id = R.drawable.delete),
                                                            contentDescription = "remove",
                                                            contentScale = ContentScale.Fit,
                                                            modifier = Modifier.fillMaxSize(),
                                                            colorFilter = ColorFilter.tint(
                                                                if (isDarkTheme) Color.Red else Color.Red
                                                            )
                                                        )
                                                    }
                                                }
                                            }

                                            Divider(thickness = 1.5.dp)
                                        }
                                    }

                                }

                            }
                        }
                    }
                }
            }
        }
    }

    // Show Dialogs
    if (showEditDialog) {
        EditDialog(onDismiss = { showEditDialog = false })
    }

    if (showRemoveDialog) {
        RemoveDialog(onDismiss = { showRemoveDialog = false })
    }
}

@Composable
fun EditDialog(onDismiss: () -> Unit) {
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


                Button(onClick = onDismiss) {
                    Text("Confirm")
                }
            }
        }
    }
}

@Composable
fun RemoveDialog(onDismiss: () -> Unit) {
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


                Button(onClick = onDismiss) {
                    Text("Delete")
                }
            }
        }
    }
}
