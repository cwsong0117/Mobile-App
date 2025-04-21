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
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hermen.ass1.MeetingRoom.MeetingRoomViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit
import com.hermen.ass1.User.SessionManager
import com.hermen.ass1.User.UserProfileViewModel

@Composable
fun RoomDetail(navController: NavController, roomName: String, isDarkTheme: Boolean, userProfileViewModel: UserProfileViewModel = viewModel() ) {
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
                }
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
                 userId: String, isDarkTheme: Boolean) {

    val cyanInTitle = if (isDarkTheme) Color(0xFFAFEEEE) else Color(0xFF00cccc)
    val cyanInButton = if (isDarkTheme) Color(0xFF00ced1) else Color(0xFF0099cc)
    val scrollState = rememberScrollState()
    val viewModel: MeetingRoomViewModel = viewModel()
    val context = LocalContext.current
    val backgroundColor = if (isDarkTheme) Color.Transparent else Color(0xFFE5FFFF)

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
            horizontalArrangement = Arrangement.Center

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
                        if (!validateSelectedDate(context, date)) {
                            return@Button
                        }
                        else if (name.isBlank() || date.isBlank() || startTime.isBlank() || endTime.isBlank() || purpose.isBlank()) {
                            Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        viewModel.submitApplication(
                            name = name,
                            date = date,
                            startTime = startTime,
                            endTime = endTime,
                            purpose = purpose,
                            type = roomName,
                            status = status,
                            userId = userId,
                            onSuccess = onSuccess,
                            onError = { e -> Log.e("Submit", "Error submitting application", e)}
                        )
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
        modifier = Modifier
            .width(380.dp)
            .padding(10.dp)
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
            onDateChange(dateFormat.format(selectedCalendar.time))
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    // Allow selecting today or future, not past
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
                    IconButton(onClick = { datePickerDialog.show() }) {
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

fun validateSelectedDate(context: Context, selectedDateStr: String): Boolean {
    val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    val selectedDate = dateFormat.parse(selectedDateStr)
    val currentTime = Calendar.getInstance().time

    val diffInMillis = selectedDate.time - currentTime.time
    val diffInHours = TimeUnit.MILLISECONDS.toHours(diffInMillis)

    return if (diffInHours < 24) {
        Toast.makeText(context, "You must reserve at least 1 day in advance.", Toast.LENGTH_SHORT).show()
        false
    } else {
        true
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
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Main Purpose Dropdown (read-only)
        TextField(
            value = customPurpose,
            onValueChange = {},
            readOnly = true,
            placeholder = { Text("Select purpose") },
            modifier = Modifier
                .width(380.dp)
                .padding(10.dp)
                .clip(RoundedCornerShape(50.dp)),
            trailingIcon = {
                Icon(
                    Icons.Default.ArrowDropDown,
                    contentDescription = "Dropdown icon",
                    modifier = Modifier.clickable { expanded = !expanded }
                )
            }
        )

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
                color = cyanInTitle)
            TextField(
                value = purpose,
                onValueChange = onPurposeChange,
                placeholder = { Text("Enter your reservation purpose") },
                modifier = Modifier
                    .width(380.dp)
                    .padding(10.dp)
                    .clip(RoundedCornerShape(50.dp))
            )
        }
        else if (purpose.isNotEmpty()) {
            onPurposeChange(customPurpose)
        }
    }
}
