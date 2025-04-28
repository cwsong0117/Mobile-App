package com.hermen.ass1

import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import android.app.DatePickerDialog
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hermen.ass1.MeetingRoom.MeetingRoomViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import com.hermen.ass1.User.SessionManager
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun RoomDetail(navController: NavController, roomName: String, isDarkTheme: Boolean) {
    // Local state for each field
    val name = remember { mutableStateOf("") }
    val date = remember { mutableStateOf("") }
    val startTime = remember { mutableStateOf("") }
    val endTime = remember { mutableStateOf("") }
    val purpose = remember { mutableStateOf("") }
    val customPurpose = remember { mutableStateOf("") }

    //later need to read the user id that user used to login
    val user = SessionManager.currentUser!!
    val userId = user.id

    val context = LocalContext.current
    val backgroundColor = if (isDarkTheme) Color.Transparent else Color(0xFFE5FFFF)
    // Show the room details of the meeting room based on the meetingRoomId
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {

        Column(
            modifier = Modifier
                .background(backgroundColor)
        ) {
            BackButton(navController, title = roomName, isDarkTheme = isDarkTheme)
            ApplyDetails(
                name = name.value, onNameChange = { name.value = it },
                date = date.value, onDateChange = { date.value = it },
                startTime = startTime.value, onStartTimeChange = { startTime.value = it },
                endTime = endTime.value, onEndTimeChange = { endTime.value = it },
                purpose = purpose.value, onPurposeChange = { purpose.value = it },
                customPurpose = customPurpose.value, onCustomPurposeChange = { customPurpose.value = it },
                status = "Pending", roomName = roomName,userId = userId,
                isDarkTheme = isDarkTheme, onSuccess = {
                    // Clear all input fields after successful submission
                    name.value = ""
                    date.value = ""
                    startTime.value = ""
                    endTime.value = ""
                    purpose.value = ""
                    customPurpose.value = ""

                    Toast.makeText(context, "Application submitted successfully!", Toast.LENGTH_SHORT).show()
                },
                navController = navController
            )
        }
    }
}

@Composable
fun ApplyDetails(name:String, onNameChange: (String) -> Unit,
                 date:String, onDateChange:(String) -> Unit,
                 startTime:String, onStartTimeChange: (String) -> Unit,
                 endTime:String, onEndTimeChange: (String) -> Unit,
                 purpose:String, onPurposeChange: (String) -> Unit,
                 customPurpose: String, onCustomPurposeChange: (String) -> Unit,
                 roomName: String, onSuccess: () -> Unit, status: String,
                 userId: String, isDarkTheme: Boolean, navController: NavController) {

    val cyanInTitle = if (isDarkTheme) Color(0xFFAFEEEE) else Color(0xFF00cccc)
    val cyanInButton = if (isDarkTheme) Color(0xFF00ced1) else Color(0xFF0099cc)
    val scrollState = rememberScrollState()
    val viewModel: MeetingRoomViewModel = viewModel()
    val context = LocalContext.current
    val backgroundColor = if (isDarkTheme) Color.Transparent else Color(0xFFE5FFFF)
    var isDialogOpen by remember { mutableStateOf(false) }

    Divider(color = Color.LightGray, thickness = 1.dp)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .background(backgroundColor),
    ) {
        //input field for name
        Row(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            Text(
                text = "Name",
                modifier = Modifier
                    .padding(start = 40.dp, top = 20.dp),
                color = cyanInTitle
            )
        }

        // call function to input the name
        NameInput(name, onNameChange)

        //input field for date
        Row(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            Text(
                text = "Date Of Apply",
                modifier = Modifier
                    .padding(start = 40.dp, top = 10.dp),
                color = cyanInTitle
            )
        }

        DateInput(date, onDateChange)

        //input field for start time
        Row(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            Text(
                text = "Start Time",
                modifier = Modifier
                    .padding(start = 40.dp, top = 10.dp),
                color = cyanInTitle
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center

        ) {

            StartTimeInput(startTime, onStartTimeChange)

        }

        //input field for end time
        Row(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            Text(
                text = "End Time",
                modifier = Modifier
                    .padding(start = 40.dp, top = 10.dp),
                color = cyanInTitle
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center

        ) {

            EndTimeInput(endTime, onEndTimeChange)
        }

        //input field for person
        Row(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            Text(
                text = "Purpose",
                modifier = Modifier
                    .padding(start = 40.dp, top = 10.dp),
                color = cyanInTitle
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            PurposeInput(purpose, onPurposeChange,  customPurpose = customPurpose, onCustomPurposeChange = onCustomPurposeChange, isDarkTheme = isDarkTheme)
        }
        //submit button
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomEnd),
                horizontalArrangement = Arrangement.End,
            ) {
                Button(
                    modifier = Modifier
                        .padding(top = 50.dp, bottom = 20.dp, end = 40.dp),
                    onClick = {
                        if (name.isBlank() || date.isBlank() || startTime.isBlank() || endTime.isBlank() || purpose.isBlank()) {
                            Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        // Check if the start time is before the end time
                        val formatter = DateTimeFormatter.ofPattern("HH : mm")
                        val start= LocalTime.parse(startTime.uppercase(), formatter)
                        val end = LocalTime.parse(endTime.uppercase(), formatter)
                        val now = LocalTime.now()

                        try {
                            if(start.isAfter(end) || start == end) {
                                Toast.makeText(context, "Start time must be before end time", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            else if(start.isBefore(now)){
                                 Toast.makeText(context, "Start time must be after current time", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                        } catch (e: Exception) {
                            Toast.makeText(context, "Invalid time format", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        isDialogOpen = true

                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = cyanInButton,
                        contentColor = Color.White
                    )
                ) {
                    Text("Submit", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            }
        }
    }
    if (isDialogOpen) {
        AlertDialog(
            onDismissRequest = { isDialogOpen = false },
            title = { Text("Submit Application", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
            text = { Text("Are you sure you want to submit this application?") },
            confirmButton = {
                Button(onClick = {
                    isDialogOpen = false
                    //submit the info to firebase
                    viewModel.submitApplication(
                        name = name,
                        date = date,
                        startTime = startTime,
                        endTime = endTime,
                        purpose = purpose,
                        type = roomName,
                        status = status,
                        userId = userId,
                        onSuccess = {
                            // Pop back stack after successful submission
                            navController.popBackStack()
                            onSuccess()
                        },
                        onError = { e ->
                            Log.e("Submit", "Error submitting application", e)
                        },
                    )
                },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = cyanInButton,
                        contentColor = Color.White
                    )) {
                    Text("Confirm", )
                }
            },
            dismissButton = {
                Button(onClick = { isDialogOpen = false },
                    colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFAA4A44),
                    contentColor = Color.White
                )) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun NameInput(name:String, onNameChange:(String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ){
        TextField(
            value = name,
            onValueChange = onNameChange,
            placeholder = { Text("ex: John") },
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.White, // Set the background color
                textColor = Color.Black // Set text color
            ),
            modifier = Modifier
                .width(380.dp)
                .padding(10.dp)
                .background(Color.White, RoundedCornerShape(36.dp))
                .clip(RoundedCornerShape(50.dp)),
        )}
}

@Composable
fun DateInput(date: String, onDateChange: (String) -> Unit) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    val currentDate = dateFormat.format(calendar.time)

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val selectedCalendar = Calendar.getInstance().apply {
                set(year, month, dayOfMonth)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val selectedDate = dateFormat.format(selectedCalendar.time)

            onDateChange(selectedDate)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    datePickerDialog.datePicker.minDate = calendar.timeInMillis

    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        BasicTextField(
            value = date,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .width(380.dp)
                .padding(10.dp)
                .background(Color.White, RoundedCornerShape(36.dp))
                .padding(10.dp)
                .height(40.dp),
            decorationBox = { innerTextField ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp)
                ) {
                    if (date.isEmpty()) {
                        Text(currentDate, color = Color.Gray)
                    } else {
                        innerTextField()
                    }
                    IconButton(onClick = {
                        datePickerDialog.show()
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_calendar_month_24),
                            contentDescription = "Select Date"
                        )
                    }
                }
            }
        )
    }
}

@Composable
fun StartTimeInput(startTime: String, onStartTimeChange:(String) -> Unit) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val currentTime = SimpleDateFormat("HH : mm", Locale.getDefault()).format(calendar.time)

    val timePickerDialog = TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            val formattedTime = String.format("%02d : %02d", hourOfDay, minute)
            onStartTimeChange(formattedTime)
            // Handle the selected time here
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        true
    )
    BasicTextField(
        value = startTime,
        onValueChange = { },
        readOnly = true,
        modifier = Modifier
            .width(380.dp)
            .padding(10.dp)
            .background(Color.White, RoundedCornerShape(36.dp))
            .padding(10.dp)
            .height(40.dp),
        decorationBox = { innerTextField ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp)
            ) {
                if (startTime.isEmpty()) {
                    Text(currentTime, color = Color.Gray)
                } else {
                    innerTextField()
                }
                IconButton(onClick = {timePickerDialog.show() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_timer_24),
                        contentDescription = "Select Start Time"
                    )
                }
            }
        }
    )
}

@Composable
fun EndTimeInput(endTime: String, onEndTimeChange: (String) -> Unit) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val currentTime = SimpleDateFormat("HH : mm", Locale.getDefault()).format(calendar.time)

    val timePickerDialog = TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            val formattedTime = String.format("%02d : %02d", hourOfDay, minute)
            onEndTimeChange(formattedTime)
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        true
    )

    BasicTextField(
        value = endTime,
        onValueChange = { },
        readOnly = true,
        modifier = Modifier
            .width(380.dp)
            .padding(10.dp)
            .background(Color.White, RoundedCornerShape(36.dp))
            .padding(10.dp)
            .height(40.dp),
        decorationBox = { innerTextField ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp)
            ) {
                if (endTime.isEmpty()) {
                    Text(currentTime, color = Color.Gray)
                } else {
                    innerTextField()
                }
                IconButton(onClick = { timePickerDialog.show() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_timer_24),
                        contentDescription = "Select End Time"
                    )
                }
            }
        }
    )
}

@Composable
fun PurposeInput(purpose: String, onPurposeChange: (String) -> Unit, customPurpose: String, onCustomPurposeChange: (String) -> Unit, isDarkTheme: Boolean) {
    val purposeList = listOf("Meeting", "Conference", "Training", "Discussion", "Interview", "Project Planning", "Other")
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
    ) {
        // Main Purpose Dropdown (read-only)
        Row(
            //make it align center
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            TextField(
                value = customPurpose,
                onValueChange = {},
                readOnly = true,
                placeholder = { Text("Select purpose") },
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.White, // Set the background color
                    textColor = Color.Black // Set text color
                ),
                modifier = Modifier
                    .width(380.dp)
                    .padding(10.dp)
                    .clip(RoundedCornerShape(50.dp)) ,
                trailingIcon = {
                    Icon(
                        Icons.Default.ArrowDropDown,
                        contentDescription = "Dropdown icon",
                        modifier = Modifier.clickable { expanded = !expanded }
                    )
                }
            )
        }

        // Dropdown List
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .width(380.dp)
                .padding(start = 24.dp)
        ) {
            purposeList.forEach { item ->
                DropdownMenuItem(
                    text = { Text(text = item) },
                    onClick = {
                        onCustomPurposeChange(item)
                        if (item != "Other") {
                            onPurposeChange(item)
                        } else {
                            onPurposeChange("") // Clear old value when switching to "Other"
                        }
                        expanded = false
                    }
                )
            }
        }

        // Show additional field if "Other" is selected
        if (customPurpose == "Other") {
            val cyanInTitle = if (isDarkTheme) Color(0xFFAFEEEE) else Color(0xFF00cccc)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Enter Your Purpose: ",
                color = cyanInTitle,
                modifier = Modifier.padding(start = 24.dp, top = 10.dp))
            Row(
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                TextField(
                    value = purpose,
                    onValueChange = onPurposeChange,
                    placeholder = { Text("Enter your reservation purpose") },
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.White, // Set the background color
                        textColor = Color.Black // Set text color
                    ),
                    modifier = Modifier
                        .width(380.dp)
                        .padding(10.dp)
                        .clip(RoundedCornerShape(50.dp))
                )
            }
        }
        else if (purpose.isNotEmpty()) {
            onPurposeChange(customPurpose)
        }
    }
}