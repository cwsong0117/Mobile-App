package com.hermen.ass1.Attendance

import android.util.Log
import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.hermen.ass1.BackButton
import com.hermen.ass1.R
import com.hermen.ass1.User.User
import com.hermen.ass1.User.toUser
import java.util.Locale
import java.util.TimeZone
import android.app.TimePickerDialog
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*


@Composable
fun AdminScreen(
    viewModel: AttendanceViewModel = viewModel(),
    isDarkTheme: Boolean,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isDarkTheme) Color.Black else Color(0xFFE5FFFF)
    //text color
    val textColor = if (isDarkTheme) Color.White else Color.Black

    val isTablet = isTablet()

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

    val showEditDialog by remember { derivedStateOf { viewModel.showEditDialog } }
    val showRemoveDialog = viewModel.showRemoveDialog
    val selectedAttendance = viewModel.selectedAttendance

    //for tablet
    val selectedEmployeeId = viewModel.selectedEmployeeId

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
            if(isTablet){
                Row(modifier = modifier.fillMaxSize()) {
                    // LEFT PANE - List of employees
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(1f)
                            .padding(8.dp)
                    ) {
                        groupedAttendance.keys.forEach { employeeID ->
                            item {
                                val userName = usersMap[employeeID]?.name ?: "Unknown User"
                                val isSelected = employeeID == selectedEmployeeId

                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(4.dp)
                                        .clickable { viewModel.selectEmployee(employeeID) },
                                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer
                                        else MaterialTheme.colorScheme.surface
                                    )
                                ) {
                                    Text(
                                        text = userName,
                                        color = textColor,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                        modifier = Modifier.padding(16.dp)
                                    )
                                }
                            }
                        }
                    }

                    // RIGHT PANE - Attendance for selected employee
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(2f)
                            .padding(8.dp)
                    ) {
                        selectedEmployeeId?.let { employeeID ->
                            val records = groupedAttendance[employeeID] ?: emptyList()
                            val userName = usersMap[employeeID]?.name ?: "Unknown User"

                            Column {
                                Text(
                                    text = "Attendance for $userName",
                                    color = textColor,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 22.sp,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )

                                LazyColumn {
                                    items(records.sortedByDescending { it.clockInTime?.toDate() }) { attendance ->
                                        val clockInDate = attendance.clockInTime?.toDate()
                                        val clockOutDate = attendance.clockOutTime?.toDate()

                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 6.dp),
                                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .padding(16.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text("Date: ${clockInDate?.let { dateFormat.format(it) } ?: "N/A"}",
                                                        color = textColor,fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                                    Text("Clock In: ${clockInDate?.let { timeFormat.format(it) } ?: "N/A"}",
                                                        color = textColor,fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                                    Text("Clock Out: ${clockOutDate?.let { timeFormat.format(it) } ?: "N/A"}",
                                                        color = textColor,fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                                    Text(
                                                        text = "Status: ${attendance.status ?: "-"}",
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 18.sp,
                                                        color = if (attendance.status == "Left Early") Color.Red else LocalContentColor.current
                                                    )
                                                }

                                                Column(horizontalAlignment = Alignment.End) {
                                                    // Edit
                                                    IconButton(onClick = {
                                                        viewModel.selectedAttendance = attendance
                                                        viewModel.setEditableState(attendance)
                                                        viewModel.showEditDialog = true
                                                    }) {
                                                        Icon(
                                                            painter = painterResource(R.drawable.edit),
                                                            contentDescription = "Edit",
                                                            tint = if (isDarkTheme) Color.White else Color.Black
                                                        )
                                                    }

                                                    // Delete
                                                    IconButton(onClick = {
                                                        viewModel.selectedAttendance = attendance
                                                        viewModel.showRemoveDialog = true
                                                    }) {
                                                        Icon(
                                                            painter = painterResource(R.drawable.delete),
                                                            contentDescription = "Delete",
                                                            tint = Color.Red
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        } ?: Text("Select an employee from the list.",
                            color = textColor,
                            fontSize = 18.sp)
                    }
                }

            }else{
                LazyColumn(modifier = modifier.padding(8.dp)) {
                    groupedAttendance.forEach { (employeeID, records) ->
                        item {
                            val expanded = viewModel.isExpanded(employeeID)
                            val userName = usersMap[employeeID]?.name ?: "Unknown User"

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                                    .clickable { viewModel.toggleExpanded(employeeID) },
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
                                                    // Attendance info
                                                    Column(modifier = Modifier.weight(1f)) {
                                                        Text("Date: ${clockInDate?.let { dateFormat.format(it) } ?: "N/A"}", fontWeight = FontWeight.Bold)
                                                        Text("Clock In: ${clockInDate?.let { timeFormat.format(it) } ?: "N/A"}")
                                                        Text("Clock Out: ${clockOutDate?.let { timeFormat.format(it) } ?: "N/A"}")
                                                        Text(
                                                            text = "Status: ${attendance.status ?: "-"}",
                                                            color = if (attendance.status == "Left Early") Color.Red else LocalContentColor.current
                                                        )
                                                    }

                                                    // Edit & Delete buttons
                                                    Column(
                                                        horizontalAlignment = Alignment.End,
                                                        verticalArrangement = Arrangement.Center,
                                                    ) {
                                                        Box(
                                                            modifier = Modifier
                                                                .size(32.dp)
                                                                .clickable {
                                                                    viewModel.selectedAttendance = attendance
                                                                    viewModel.setEditableState(attendance)
                                                                    viewModel.showEditDialog = true
                                                                }
                                                        ) {
                                                            Image(
                                                                painter = painterResource(id = R.drawable.edit),
                                                                contentDescription = "edit",
                                                                contentScale = ContentScale.Fit,
                                                                modifier = Modifier.fillMaxSize(),
                                                                colorFilter = ColorFilter.tint(if (isDarkTheme) Color.White else Color.Black)
                                                            )
                                                        }

                                                        Spacer(modifier = Modifier.height(16.dp))

                                                        Box(
                                                            modifier = Modifier
                                                                .size(32.dp)
                                                                .clickable {
                                                                    viewModel.selectedAttendance = attendance
                                                                    viewModel.showRemoveDialog = true
                                                                }
                                                        ) {
                                                            Image(
                                                                painter = painterResource(id = R.drawable.delete),
                                                                contentDescription = "remove",
                                                                contentScale = ContentScale.Fit,
                                                                modifier = Modifier.fillMaxSize(),
                                                                colorFilter = ColorFilter.tint(Color.Red)
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
    }

    // Show Dialogs
    if (showEditDialog && selectedAttendance != null) {
        EditDialog(
            attendance = selectedAttendance,
            onDismiss = {
                viewModel.clearEditableState()
                viewModel.selectedAttendance = null
                viewModel.showEditDialog = false
            },
            isDarkTheme = isDarkTheme
        )
    }

    if (showRemoveDialog && selectedAttendance != null) {
        RemoveDialog(
            attendance = selectedAttendance!!,
            onDismiss = {
                viewModel.selectedAttendance = null
                viewModel.showRemoveDialog = false
            },
            isDarkTheme = isDarkTheme
        )
    }
}

@Composable
fun EditDialog(
    attendance: Attendance,
    onDismiss: () -> Unit,
    viewModel: AttendanceViewModel = viewModel(),
    isDarkTheme: Boolean
) {
    val backgroundColor = if (isDarkTheme) Color.DarkGray else Color.White
    //text color
    val textColor = if (isDarkTheme) Color.White else Color.Black

    val context = LocalContext.current

    val editable = viewModel.editableState ?: return  // Prevent crash if null
    val clockIn = editable.clockIn
    val clockOut = editable.clockOut
    val status = editable.status

    val statusOptions = listOf("Clocked In", "OUT", "Left Early")
    var expanded by rememberSaveable { mutableStateOf(false) } // survives orientation change

    val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    // Malaysia timezone
    dateFormat.timeZone = TimeZone.getTimeZone("Asia/Kuala_Lumpur")
    timeFormat.timeZone = TimeZone.getTimeZone("Asia/Kuala_Lumpur")

    val openTimePicker = remember { mutableStateOf(false) }
    val targetTime = remember { mutableStateOf<Date?>(null) }

    val editingClockIn = remember { mutableStateOf(true) }

    if (openTimePicker.value && targetTime.value != null) {
        val calendar = Calendar.getInstance().apply {
            timeZone = TimeZone.getTimeZone("Asia/Kuala_Lumpur")
            time = targetTime.value!!
        }

        TimePickerDialog(context, { _, hour, minute ->
            val cal = Calendar.getInstance().apply {
                timeZone = TimeZone.getTimeZone("Asia/Kuala_Lumpur")
                time = targetTime.value!!
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
            }

            targetTime.value = cal.time

            if (editingClockIn.value) {
                viewModel.updateClockIn(cal.time)
            } else {
                viewModel.updateClockOut(cal.time)
            }

            openTimePicker.value = false
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
    }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .width(320.dp)
                .wrapContentHeight()
                .background(backgroundColor, shape = RoundedCornerShape(16.dp))
                .padding(24.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Edit Attendance", color = textColor, fontWeight = FontWeight.Bold, fontSize = 18.sp)

                Spacer(Modifier.height(16.dp))

                Text("Clock In: ${dateFormat.format(clockIn)} ${timeFormat.format(clockIn)}"
                , color = textColor)
                Button(onClick = {
                    editingClockIn.value = true
                    targetTime.value = clockIn
                    openTimePicker.value = true

                }) { Text("Edit" , color = textColor) }


                Spacer(Modifier.height(8.dp))

                Text("Clock Out: ${dateFormat.format(clockOut)} ${timeFormat.format(clockOut)}"
                    , color = textColor)
                Button(onClick = {
                    editingClockIn.value = false
                    targetTime.value = clockOut
                    openTimePicker.value = true
                }) { Text("Edit" , color = textColor) }

                Spacer(Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ){
                    Text("Status: ${status}", color = textColor)

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(onClick = { expanded = true }) {
                        Text("edit", color = textColor)
                    }
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    statusOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                viewModel.updateStatus(option)
                                expanded = false
                            }
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ){
                    Button(onClick = onDismiss) {
                        Text("Cancel" , color = textColor)
                    }

                    Spacer(Modifier.width(8.dp))

                    Button(
                        onClick = {
                        // Recombine original date with edited time
                        fun combineDateTime(originalDate: Date, selectedTime: Date): Date {
                            val dateCal = Calendar.getInstance().apply {
                                timeZone = TimeZone.getTimeZone("Asia/Kuala_Lumpur")
                                time = originalDate
                            }
                            val timeCal = Calendar.getInstance().apply {
                                timeZone = TimeZone.getTimeZone("Asia/Kuala_Lumpur")
                                time = selectedTime
                            }

                            dateCal.set(Calendar.HOUR_OF_DAY, timeCal.get(Calendar.HOUR_OF_DAY))
                            dateCal.set(Calendar.MINUTE, timeCal.get(Calendar.MINUTE))
                            return dateCal.time
                        }


                        val finalClockIn = combineDateTime(attendance.clockInTime?.toDate() ?: Date(), clockIn)
                        val clockOutBaseDate = attendance.clockOutTime?.toDate() ?: attendance.clockInTime?.toDate() ?: Date()
                        val finalClockOut = combineDateTime(clockOutBaseDate, clockOut)

                        if (finalClockOut.before(finalClockIn)) {
                            Toast.makeText(context, "Clock-out time cannot be before clock-in time.", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        val updatedData = mapOf(
                            "clockInTime" to Timestamp(finalClockIn),
                            "clockOutTime" to Timestamp(finalClockOut),
                            "status" to status
                        )
                        viewModel.editAttendance(attendance.attendanceID, updatedData) {
                            Toast.makeText(context, "Attendance updated", Toast.LENGTH_SHORT).show()
                            viewModel.getAttendance {
                                onDismiss()
                            }
                        }
                    },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))

                    ) {
                        Text("Save Changes" , color = textColor)
                    }

                }

            }
        }
    }
}


@Composable
fun RemoveDialog(
    attendance: Attendance,
    onDismiss: () -> Unit,
    viewModel: AttendanceViewModel = viewModel(),
    isDarkTheme: Boolean
) {
    val backgroundColor = if (isDarkTheme) Color.DarkGray else Color.White
    //text color
    val textColor = if (isDarkTheme) Color.White else Color.Black

    val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("Asia/Kuala_Lumpur")
    }

    val clockIn = attendance.clockInTime?.toDate()
    val clockOut = attendance.clockOutTime?.toDate()

    Log.d("RemoveDialog", "Deleting ID: ${attendance.attendanceID}")

    val context = LocalContext.current

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .width(320.dp)
                .wrapContentHeight()
                .background(backgroundColor, shape = RoundedCornerShape(16.dp))
                .padding(24.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Are you sure you want to delete ${attendance.employeeID}'s record?",
                    color = textColor,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = attendance.attendanceID,
                    color = Color.Blue
                )

                Text(
                    text = "Clock In Time: ${clockIn?.let { timeFormat.format(it) } ?: "-"}",
                    color = Color.Blue
                )

                Text(
                    text = "Clock Out Time: ${clockOut?.let { timeFormat.format(it) } ?: "-"}",
                    color = Color.Blue
                )

                Spacer(Modifier.height(16.dp))

                Button(onClick = {
                    viewModel.deleteAttendance(attendance.attendanceID) {
                        viewModel.getAttendance {
                            Toast.makeText(context, "Record removed successfully", Toast.LENGTH_SHORT).show()
                            onDismiss()
                        }
                    }
                }) {
                    Text("Remove" , color = textColor)
                }

                Spacer(Modifier.height(8.dp))

                Button(onClick = onDismiss) {
                    Text("Cancel" , color = textColor)
                }
            }
        }
    }
}

