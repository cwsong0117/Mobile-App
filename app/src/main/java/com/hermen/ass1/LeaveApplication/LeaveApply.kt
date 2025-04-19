package com.hermen.ass1.LeaveApplication

import android.app.DatePickerDialog
import android.content.Context
import android.net.Uri
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.IconButton
import androidx.compose.material3.*
import androidx.compose.material3.Icon
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.Firebase
import java.util.UUID
import com.google.firebase.storage.storage
import com.hermen.ass1.BackButton
import com.hermen.ass1.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun LeaveApply(navController: NavController, isDarkTheme: Boolean) {

    var name by remember { mutableStateOf("") }
    var dateFrom by remember { mutableStateOf("") }
    var dateTo by remember { mutableStateOf("") }
    var reason by remember { mutableStateOf("") }
    var leaveType by remember { mutableStateOf("") }
    var dayOfLeave by remember { mutableStateOf("") }
    val userId = "A001"
    val context = LocalContext.current
    val backgroundColor = if (isDarkTheme) Color.Transparent else Color(0xFFE5FFFF)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .verticalScroll(rememberScrollState())
    ) {
        BackButton(navController = navController, title = "Leave Application", isDarkTheme = false)
        LeaveApplicationScreen(navController, isDarkTheme,
            name = name, onNameChange = { name = it },
            dateFrom = dateFrom, onDateFromChange = { dateFrom = it },
            dateTo = dateTo, onDateToChange = { dateTo = it },
            reason = reason, onReasonChange = { reason = it },
            leaveType = leaveType, onLeaveTypeChange = { leaveType = it },
            dayOfLeave = dayOfLeave, onDayOfLeaveChange = { dayOfLeave = it },
            userId = userId, onSuccess = {
                name = ""
                dateFrom = ""
                dateTo = ""
                reason = ""
                leaveType = ""

                Toast.makeText(context, "Leave application submitted successfully!", Toast.LENGTH_SHORT).show()
            },
            imageUri = null)
    }
}

@Composable
fun LeaveApplicationScreen(navController: NavController,isDarkTheme: Boolean,
                           name: String, onNameChange: (String) -> Unit,
                           dateFrom: String, onDateFromChange: (String) -> Unit,
                           dateTo: String, onDateToChange: (String) -> Unit,
                           reason: String, onReasonChange: (String) -> Unit,
                           leaveType: String, onLeaveTypeChange: (String) -> Unit,
                           dayOfLeave: String, onDayOfLeaveChange: (String) -> Unit,
                           userId: String,onSuccess: () -> Unit,
                           imageUri: Uri? = null) {
    val context = LocalContext.current
    val imageUri = remember { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
            uri: Uri? -> uri?.let {
        imageUri.value = it
        uploadImageToFirebase(uri, context)
        }
    }
    val cyanInTitle = if (isDarkTheme) Color.Transparent else Color(0xFF00cccc)
    val backgroundColor = if (isDarkTheme) Color.Transparent else Color(0xFFE5FFFF)

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .background(backgroundColor)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = "Name",
                    modifier = Modifier
                        .padding(start = 40.dp, top = 20.dp),
                    color = cyanInTitle,
                    fontSize = 16.sp
                )
            }
            LeaveNameInput(name, onNameChange)
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Day Of Leave",
                    modifier = Modifier.padding(start = 40.dp, top = 20.dp),
                    color = cyanInTitle,
                    fontSize = 16.sp)
            }
            DayOfLeaveInput(dayOfLeave = dayOfLeave, onDayOfLeaveChange = onDayOfLeaveChange, isIntOnly = true, isOneDayOnly = true)
            if (dayOfLeave.trim() == "1"){
                Row { Text(text = "If you only want to apply for one day leave, then select the date at the Leave From Field",
                    color = Color.Red,
                    modifier = Modifier.padding(start = 40.dp, end = 20.dp)) }
            }
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Leave From",
                    modifier = Modifier
                        .padding(start = 40.dp, top = 20.dp),
                    color = cyanInTitle,
                    fontSize = 16.sp)
            }

            LeaveDateFromInput(dateFrom = dateFrom, onDateFromChange = onDateFromChange)
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Leave Until",
                    modifier = Modifier.padding(start = 40.dp, top = 20.dp),
                    color = cyanInTitle,
                    fontSize = 16.sp)
            }
            LeaveDateToInput(dateTo = dateTo, onDateToChange = onDateToChange, dayOfLeave = dayOfLeave)
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Leave Type",
                    modifier = Modifier.padding(start = 40.dp, top = 20.dp),
                    color = cyanInTitle,
                    fontSize = 16.sp)
            }
            LeaveType(leaveType = leaveType, onLeaveTypeChange = onLeaveTypeChange)
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Reason",
                    modifier = Modifier.padding(start = 40.dp, top = 20.dp),
                    color = cyanInTitle,
                    fontSize = 16.sp)
            }
            LeaveReason(reason = reason, onReasonChange = onReasonChange)
            Row(
                modifier = Modifier.padding(start = 36.dp, top = 12.dp),
            ) {
                Text(text = "Evidence: ",
                    fontSize = 16.sp,
                    color = cyanInTitle)
            }
            Row(
                modifier = Modifier.padding(start = 36.dp, top = 12.dp)
            ) {
                Button(onClick = { launcher.launch("image/*") },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF00cccc),
                        contentColor = Color.White
                    )){
                    Text("Upload Image")
                }
            }
            Box(

            ){
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomEnd),
                    horizontalArrangement = Arrangement.End,
                ){
                    Button(
                        modifier = Modifier.padding(top = 50.dp, bottom = 20.dp, end = 40.dp),
                        onClick = {
                            if (name.isBlank() || dateFrom.isBlank() || dateTo.isBlank() || reason.isBlank() || leaveType.isBlank()) {
                                Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF00cccc),
                            contentColor = Color.White
                        )
                    ) {
                        Text("Submit", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                }
            }

        }
    }
}

@Composable
fun LeaveNameInput(name: String, onNameChange: (String) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        TextField(
            value = name,
            onValueChange = onNameChange,
            placeholder = { Text("ex: John") },
            modifier = Modifier
                .width(380.dp)
                .padding(10.dp)
                .clip(RoundedCornerShape(50.dp)),
            singleLine = true,
        )
    }
}

@Composable
fun DayOfLeaveInput(dayOfLeave: String, onDayOfLeaveChange: (String) -> Unit, isIntOnly: Boolean = false, isOneDayOnly: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        TextField(
            value = dayOfLeave,
            onValueChange = {
                val filterInput = if (isIntOnly) it.filter { char -> char.isDigit() } else it
                onDayOfLeaveChange(filterInput)
            },
            placeholder = { Text("ex: 1") },
            modifier = Modifier
                .width(380.dp)
                .padding(10.dp)
                .clip(RoundedCornerShape(50.dp)),
            singleLine = true,
            keyboardOptions = if (isIntOnly) KeyboardOptions(keyboardType = KeyboardType.Number) else KeyboardOptions.Default
        )
    }
}

@Composable
fun LeaveDateFromInput(dateFrom: String, onDateFromChange: (String) -> Unit) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    val currentDate = dateFormat.format(calendar.time)

    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val selectedCalendar = Calendar.getInstance().apply {
                    set(year, month, dayOfMonth)
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                onDateFromChange(dateFormat.format(selectedCalendar.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            datePicker.minDate = calendar.timeInMillis
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        TextField(
            value = if (dateFrom.isEmpty()) currentDate else dateFrom,
            onValueChange = {},
            readOnly = true,
            placeholder = { Text("Select Date") },
            modifier = Modifier
                .width(380.dp)
                .padding(10.dp)
                .clip(RoundedCornerShape(50.dp))
                .clickable { datePickerDialog.show() }, // Field is now clickable
            singleLine = true,
            trailingIcon = {
                IconButton(onClick = { datePickerDialog.show() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_calendar_month_24),
                        contentDescription = "Select Date"
                    )
                }
            }
        )
    }
}

@Composable
fun LeaveDateToInput(dateTo: String, onDateToChange: (String) -> Unit, dayOfLeave: String) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    val currentDate = dateFormat.format(calendar.time)

    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val selectedCalendar = Calendar.getInstance().apply {
                    set(year, month, dayOfMonth)
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                onDateToChange(dateFormat.format(selectedCalendar.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            datePicker.minDate = calendar.timeInMillis
        }
    }
    val isEnabled = dayOfLeave != "1"

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        TextField(
            value = if (dateTo.isEmpty()) currentDate else dateTo,
            onValueChange = {},
            readOnly = true,
            enabled = isEnabled,
            placeholder = { Text("Select Date") },
            modifier = Modifier
                .width(380.dp)
                .padding(10.dp)
                .clip(RoundedCornerShape(50.dp))
                .clickable { if (isEnabled) datePickerDialog.show() }, // Field is now clickable
            singleLine = true,
            trailingIcon = {
                IconButton(onClick = { if (isEnabled) datePickerDialog.show() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_calendar_month_24),
                        contentDescription = "Select Date"
                    )
                }
            }
        )
        if (!isEnabled) {

        }
    }
}

@Composable
fun LeaveType(leaveType: String, onLeaveTypeChange: (String) -> Unit) {
    //use radio button to user select the type of leave, whether mc or annual leave (currently)
    var leaveTypeList =listOf("Medical Leave", "Annual Leave", "Emergency Leave", "Unpaid Leave", "Marriage Leave", "Quarantine Leave", "Hospitalization Leave")
    var Expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        TextField(
            value = leaveType,
            onValueChange = onLeaveTypeChange,
            readOnly = true,
            placeholder = { Text(text = "Leave Type") },
            modifier = Modifier
                .width(380.dp)
                .padding(10.dp)
                .clip(RoundedCornerShape(50.dp)),
            trailingIcon = {
                Icon(
                    Icons.Default.ArrowDropDown,
                    contentDescription = "leave type",
                    modifier = Modifier.clickable { Expanded = !Expanded }
                )
            }
        )
        DropdownMenu(
            expanded = Expanded,
            onDismissRequest = { Expanded = false },
            modifier = Modifier.width(360.dp).padding(start = 16.dp)
        ) {
            leaveTypeList.forEach { leave ->
                DropdownMenuItem(
                    text = { Text(text = leave) },
                    onClick = {
                        onLeaveTypeChange(leave)
                        Expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun LeaveReason(reason: String, onReasonChange: (String) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        TextField(
            value = reason,
            onValueChange = onReasonChange,
            placeholder = { Text("ex: Fever") },
            modifier = Modifier
                .width(380.dp)
                .padding(10.dp)
                .clip(RoundedCornerShape(50.dp)),
            singleLine = true
        )
    }
}

//used for upload image to firebase
fun uploadImageToFirebase(imageUri: Uri, context: Context) {
    val storageRef = Firebase.storage.reference
    val fileName = "images/${UUID.randomUUID()}.jpg"
    val imageRef = storageRef.child(fileName)
    val uploadTask = imageRef.putFile(imageUri)

    uploadTask
        .addOnSuccessListener {
            imageRef.downloadUrl.addOnSuccessListener { uri ->
                Toast.makeText(context, "Uploaded: $uri", Toast.LENGTH_SHORT).show()
                // You can also save the `uri.toString()` to Firestore
            }
        }
        .addOnFailureListener {
            Toast.makeText(context, "Upload failed: ${it.message}", Toast.LENGTH_SHORT).show()
        }
}

@Preview(showBackground = true)
@Composable
fun LeaveApplyPreview() {
    LeaveApply(navController = rememberNavController(), isDarkTheme = false)
}