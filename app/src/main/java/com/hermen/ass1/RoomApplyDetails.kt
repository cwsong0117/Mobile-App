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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

@Composable
fun RoomDetail(navController: NavController, roomName: String) {
    // Local state for each field
    val name = remember { mutableStateOf("") }
    val date = remember { mutableStateOf("") }
    val startTime = remember { mutableStateOf("") }
    val endTime = remember { mutableStateOf("") }
    val purpose = remember { mutableStateOf("") }

    // Show the room details of the meeting room based on the meetingRoomId
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {

        Column(
            modifier = Modifier
                .background(Color(0xFFe5ffff))
        ) {
            BackButton(navController, title = roomName)
            ApplyDetails(
                name = name.value, onNameChange = { name.value = it },
                date = date.value, onDateChange = { date.value = it },
                startTime = startTime.value, onStartTimeChange = { startTime.value = it },
                endTime = endTime.value, onEndTimeChange = { endTime.value = it },
                purpose = purpose.value, onPurposeChange = { purpose.value = it },
                roomName = roomName
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
                 roomName: String) {

    val cyanInTitle = Color(0xFF00cccc)
    val cyanInButton = Color(0xFF0099cc)
    val scrollState = rememberScrollState()
    val viewModel: MeetingRoomViewModel = viewModel()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
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

            PurposeInput(purpose, onPurposeChange)

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
                        viewModel.submitApplication(
                            name = name,
                            date = date,
                            startTime = startTime,
                            endTime = endTime,
                            purpose = purpose,
                            type = roomName,
                            onSuccess = { /*TODO*/ },
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

    ) {
        BasicTextField(
            value = name,
            onValueChange = onNameChange,
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
                    if (name.isEmpty()) {
                        Text("Enter Your Name", color = Color.Gray)
                    } else {
                        innerTextField()
                    }
                }
            }
        )
    }
}

@Composable
fun DateInput(date:String, onDateChange: (String) -> Unit) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    val currentDate = dateFormat.format(calendar.time)
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val selectedCalendar = Calendar.getInstance().apply {
                set(year, month, dayOfMonth)
            }
            onDateChange(dateFormat.format(selectedCalendar.time))
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )
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

@Composable
fun StartTimeInput(startTime: String, onStartTimeChange:(String) -> Unit) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(calendar.time)

    val timePickerDialog = TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            val formattedTime = String.format("%02d:%02d", hourOfDay, minute)
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
                    Text(currentTime, color = Color.Gray)/* TODO */
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
fun EndTimeInput(endTime:String, onEndTimeChange: (String) -> Unit) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(calendar.time)
    val timePickerDialog = TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            val formattedTime = String.format("%02d:%02d", hourOfDay, minute)
            onEndTimeChange(formattedTime)
            // Handle the selected time here
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
                    Text(currentTime, color = Color.Gray)/* TODO */
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
fun PurposeInput(purpose:String, onPurposeChange: (String) -> Unit) {
    BasicTextField( /* Create a drop window to let the user select in a range TODO*/
        value = purpose,
        onValueChange = onPurposeChange,
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
                if (purpose.isEmpty()) {
                    Text("why", color = Color.Gray)
                } else {
                    innerTextField()
                }
            }
        }
    )
}