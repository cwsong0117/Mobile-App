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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hermen.ass1.MeetingRoom.MeetingRoomFormViewModel
import com.hermen.ass1.MeetingRoom.MeetingRoomViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import com.hermen.ass1.User.SessionManager
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun RoomDetail(navController: NavController, roomName: String, isDarkTheme: Boolean) {
    val viewModel: MeetingRoomFormViewModel = viewModel()

    //later need to read the user id that user used to login
    val user = SessionManager.currentUser!!
    val userId = user.id

    val context = LocalContext.current
    val backgroundColor = if (isDarkTheme) Color(0xFF121212) else Color(0xFFE5FFFF)
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
                name = viewModel.name, onNameChange = { viewModel.name = it },
                date = viewModel.date, onDateChange = { viewModel.date = it },
                startTime = viewModel.startTime, onStartTimeChange = { viewModel.startTime = it },
                endTime = viewModel.endTime, onEndTimeChange = { viewModel.endTime = it },
                purpose = viewModel.purpose, onPurposeChange = { viewModel.purpose = it },
                customPurpose = viewModel.customPurpose, onCustomPurposeChange = { viewModel.customPurpose = it },
                status = "Pending", roomName = roomName,userId = userId,
                isDarkTheme = isDarkTheme, onSuccess = {
                    // Clear all input fields after successful submission
                    viewModel.name = ""
                    viewModel.date = ""
                    viewModel.startTime = ""
                    viewModel.endTime = ""
                    viewModel.purpose = ""
                    viewModel.customPurpose = ""

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
        NameInput(name, onNameChange, isDarkTheme)

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

        DateInput(date, onDateChange, isDarkTheme)

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

            StartTimeInput(startTime, onStartTimeChange, isDarkTheme)

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

            EndTimeInput(endTime, onEndTimeChange, isDarkTheme)
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
            PurposeInput(purpose, onPurposeChange,  customPurpose = customPurpose,
                onCustomPurposeChange = onCustomPurposeChange, isDarkTheme = isDarkTheme)
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
                        try {
                            val timeFormatter = DateTimeFormatter.ofPattern("HH : mm")
                            val dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy") // Adjust to your input format

                            val selectedDate = LocalDate.parse(date, dateFormatter) // e.g., "10/05/2025"
                            val startTimeParsed = LocalTime.parse(startTime.uppercase(), timeFormatter)
                            val endTimeParsed = LocalTime.parse(endTime.uppercase(), timeFormatter)

                            val startDateTime = LocalDateTime.of(selectedDate, startTimeParsed)
                            val endDateTime = LocalDateTime.of(selectedDate, endTimeParsed)
                            val now = LocalDateTime.now()

                            when {
                                startDateTime >= endDateTime -> {
                                    Toast.makeText(context, "Start time must be before end time", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                startDateTime <= now -> {
                                    Toast.makeText(context, "Start time must be in the future", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                else -> {
                                    isDialogOpen = true
                                }
                            }
                        } catch (e: Exception) {
                            Toast.makeText(context, "Invalid date or time format", Toast.LENGTH_SHORT).show()
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
fun NameInput(name: String, onNameChange: (String) -> Unit, isDarkTheme: Boolean) {
    val backgroundColor = if (isDarkTheme) Color(0xFF1E1E1E) else Color.White
    val textColor = if (isDarkTheme) Color.White else Color.Black
    val placeholderColor = if (isDarkTheme) Color.LightGray else Color.Gray

    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            singleLine = true,
            placeholder = {
                Text("ex: John", color = placeholderColor)
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                backgroundColor = backgroundColor,
                textColor = textColor,
                focusedBorderColor = if (isDarkTheme) Color.White else Color.Black,
                unfocusedBorderColor = if (isDarkTheme) Color.Gray else Color.LightGray,
                cursorColor = textColor
            ),
            modifier = Modifier
                .width(380.dp)
                .padding(10.dp)
                .clip(RoundedCornerShape(50.dp))
        )
    }
}

@Composable
fun DateInput(date: String, onDateChange: (String) -> Unit, isDarkTheme: Boolean) {
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

    val bgColor = if (isDarkTheme) Color(0xFF1E1E1E) else Color.White
    val textColor = if (isDarkTheme) Color.White else Color.Black
    val placeholderColor = if (isDarkTheme) Color.LightGray else Color.Gray

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        BasicTextField(
            value = date,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .width(380.dp)
                .padding(10.dp)
                .background(bgColor, RoundedCornerShape(36.dp))
                .padding(horizontal = 16.dp, vertical = 10.dp)
                .height(50.dp),
            decorationBox = { innerTextField ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (date.isEmpty()) {
                        Text(text = currentDate, color = placeholderColor)
                    } else {
                        Text(date, color = textColor)
                    }
                    IconButton(onClick = { datePickerDialog.show() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_calendar_month_24),
                            contentDescription = "Select Date",
                            tint = textColor
                        )
                    }
                }
            }
        )
    }
}

@Composable
fun TimeInput(
    time: String,
    onTimeChange: (String) -> Unit,
    iconId: Int,
    isDarkTheme: Boolean
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val currentTime = SimpleDateFormat("HH : mm", Locale.getDefault()).format(calendar.time)

    val timePickerDialog = TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            val formattedTime = String.format("%02d : %02d", hourOfDay, minute)
            onTimeChange(formattedTime)
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        true
    )

    val bgColor = if (isDarkTheme) Color(0xFF1E1E1E) else Color.White
    val textColor = if (isDarkTheme) Color.White else Color.Black
    val placeholderColor = if (isDarkTheme) Color.LightGray else Color.Gray

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        BasicTextField(
            value = time,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .width(380.dp)
                .padding(10.dp)
                .background(bgColor, RoundedCornerShape(36.dp))
                .padding(horizontal = 16.dp, vertical = 10.dp)
                .height(50.dp),
            decorationBox = { innerTextField ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (time.isEmpty()) {
                        Text(text = currentTime, color = placeholderColor)
                    } else {
                        Text(time, color = textColor)
                    }
                    IconButton(onClick = { timePickerDialog.show() }) {
                        Icon(
                            painter = painterResource(id = iconId),
                            contentDescription = "Select Time",
                            tint = textColor
                        )
                    }
                }
            }
        )
    }
}

@Composable
fun StartTimeInput(startTime: String, onStartTimeChange: (String) -> Unit, isDarkTheme: Boolean) {
    TimeInput(startTime, onStartTimeChange, R.drawable.baseline_timer_24, isDarkTheme)
}

@Composable
fun EndTimeInput(endTime: String, onEndTimeChange: (String) -> Unit, isDarkTheme: Boolean) {
    TimeInput(endTime, onEndTimeChange, R.drawable.baseline_timer_24, isDarkTheme)
}

@Composable
fun PurposeInput(purpose: String, onPurposeChange: (String) -> Unit, customPurpose: String, onCustomPurposeChange: (String) -> Unit, isDarkTheme: Boolean) {
    val purposeList = listOf("Meeting", "Conference", "Training", "Discussion", "Interview", "Project Planning", "Other")
    var expanded by remember { mutableStateOf(false) }
    val backgroundColor = if (isDarkTheme) Color(0xFF1E1E1E) else Color.White
    val textColor = if (isDarkTheme) Color.White else Color.Black
    val placeholderColor = if (isDarkTheme) Color.LightGray else Color.Gray

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
                placeholder = { Text("Select purpose", color = Color.Gray) },
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = backgroundColor,
                    textColor = textColor,
                    placeholderColor = placeholderColor,
                    cursorColor = textColor
                )
                ,
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
                    placeholder = { Text("Enter your reservation purpose",color = Color.Gray) },
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = backgroundColor,
                        textColor = textColor,
                        placeholderColor = placeholderColor,
                        cursorColor = textColor
                    )
                    ,
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