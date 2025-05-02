package com.hermen.ass1

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.*
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material.icons.filled.Close
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import java.text.SimpleDateFormat
import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import java.util.UUID
import java.util.*
import androidx.compose.material3.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.Image
import androidx.activity.compose.rememberLauncherForActivityResult
import com.hermen.ass1.User.SessionManager
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaveApplication(navController: NavController, isDarkTheme: Boolean) {
    val backgroundColor = if (isDarkTheme) Color.Transparent else Color(0xFFE5FFFF)
    val user = SessionManager.currentUser
    var leaveReason by rememberSaveable { mutableStateOf("") }
    val selectedDates = rememberSaveable { mutableStateListOf<Date>() }
    val leaveDate by rememberSaveable { mutableStateOf("") }
    val leaveType by rememberSaveable { mutableStateOf("") }
    val reason by rememberSaveable { mutableStateOf("") }


    // 使用 Calendar 代替 LocalDate
    val calendar = Calendar.getInstance()

    // 当前日期，初始化为今天
    val currentMonth = rememberSaveable { mutableStateOf(calendar.clone() as Calendar) }

    // 计算当前月份的天数
    fun getDaysInMonth(calendar: Calendar): Int {
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    }

    // 获取本月的日期列表
    fun getDaysOfMonth(calendar: Calendar): List<Date> {
        val daysInMonth = getDaysInMonth(calendar)
        val firstDayOfMonth = calendar.clone() as Calendar
        firstDayOfMonth.set(Calendar.DAY_OF_MONTH, 1) // 设置为1号

        val dayList = mutableListOf<Date>()
        for (i in 0 until daysInMonth) {
            val currentDay = firstDayOfMonth.clone() as Calendar
            currentDay.add(Calendar.DAY_OF_MONTH, i) // 每次递增一天
            dayList.add(currentDay.time) // 添加到日期列表
        }
        return dayList
    }

    // 初始时获取当前月份的日期列表
    val dayList = rememberSaveable { mutableStateOf(getDaysOfMonth(currentMonth.value)) }

    // 🧾 Leave Type Dropdown
    val leaveTypes = listOf(
        "Annual Leave",
        "Medical Leave",
        "Emergency Leave",
        "Maternity Leave",
        "Paternity Leave",
        "Compassionate Leave / Bereavement Leave",
        "Replacement Leave",
        "Marriage Leave"
    )

    var expanded by rememberSaveable { mutableStateOf(false) }
    var selectedLeaveType by rememberSaveable { mutableStateOf("") }

    val context = LocalContext.current
    var selectedFileUri by rememberSaveable { mutableStateOf<Uri?>(null) }

    // Launcher for file selection
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedFileUri = uri
        uri?.let {
            Toast.makeText(context, "File selected: ${it.lastPathSegment}", Toast.LENGTH_SHORT).show()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        contentAlignment = Alignment.TopCenter
    ) {
        Column { BackButton(navController = navController, title = "Leave Application", isDarkTheme = false)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp)
                    .verticalScroll(rememberScrollState()),  // 允许滚动
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {

                if (user?.id?.startsWith("A") == true) {
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = "Approve leave of absence →",
                        color = Color.Blue,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        modifier = Modifier
                            .padding(top = 12.dp)
                            .clickable {
                                // Navigate to ApproveLeave screen
                                navController.navigate(AppScreen.ApproveLeave.name)
                            }
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Show leave of absence →",
                    color = Color.Blue,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .clickable {
                            // Navigate to ApproveLeave screen
                            navController.navigate(AppScreen.ShowLeave.name)
                        }
                )

                Spacer(modifier = Modifier.height(50.dp))

                // 📅 Leave Dates title
                Text(
                    text = "Select Leave Dates",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                )

                // 📅 Calendar controls (prev / next month)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    IconButton(
                        onClick = {
                            val updatedCalendar = currentMonth.value.clone() as Calendar
                            updatedCalendar.add(Calendar.MONTH, -1)
                            currentMonth.value = updatedCalendar // 替换为新实例
                            dayList.value = getDaysOfMonth(updatedCalendar)
                        }
                    ) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Previous month")
                    }

                    Text(
                        text = "${currentMonth.value.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())} ${currentMonth.value.get(Calendar.YEAR)}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )

                    IconButton(
                        onClick = {
                            val updatedCalendar = currentMonth.value.clone() as Calendar
                            updatedCalendar.add(Calendar.MONTH, 1)
                            currentMonth.value = updatedCalendar // 替换为新实例
                            dayList.value = getDaysOfMonth(updatedCalendar)
                        }
                    ) {
                        Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "Next month")
                    }
                }

                Spacer(modifier = Modifier.height(15.dp))

                // 📆 Multi-date picker calendar
                Column {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(7),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        contentPadding = PaddingValues(4.dp)
                    ) {
                        items(dayList.value) { date ->
                            val isSelected = selectedDates.contains(date)
                            Box(
                                modifier = Modifier
                                    .padding(4.dp)
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) Color(0xFF00BCD4) else Color(0xFFE0E0E0))
                                    .clickable {
                                        val today = Calendar.getInstance().apply {
                                            set(Calendar.HOUR_OF_DAY, 0)
                                            set(Calendar.MINUTE, 0)
                                            set(Calendar.SECOND, 0)
                                            set(Calendar.MILLISECOND, 0)
                                        }

                                        val clickedDate = Calendar.getInstance().apply {
                                            time = date
                                            set(Calendar.HOUR_OF_DAY, 0)
                                            set(Calendar.MINUTE, 0)
                                            set(Calendar.SECOND, 0)
                                            set(Calendar.MILLISECOND, 0)
                                        }

                                        if (clickedDate.after(today)) {
                                            if (isSelected) selectedDates.remove(date)
                                            else selectedDates.add(date)
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = SimpleDateFormat("d", Locale.getDefault()).format(date), // 只显示日期
                                    color = if (isSelected) Color.White else Color.Black,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // 📋 Show selected dates
                    if (selectedDates.isNotEmpty()) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            selectedDates.sortedBy { it.time }.forEach { date ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .background(Color(0xFFF1F1F1), shape = RoundedCornerShape(8.dp))
                                        .padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = SimpleDateFormat("d MMM yyyy", Locale.getDefault()).format(date), // 显示完整日期格式
                                        modifier = Modifier.weight(1f),
                                        fontSize = 16.sp
                                    )
                                    IconButton(onClick = { selectedDates.remove(date) }) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Remove date"
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        Text("No leave dates selected yet.", color = Color.Gray, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(30.dp))
                    }
                }

                Text(
                    text = "Leave Type",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp)
                )

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    TextField(
                        readOnly = true,
                        value = selectedLeaveType,
                        onValueChange = {},
                        placeholder = { Text("Select leave type") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                        colors = TextFieldDefaults.textFieldColors( // ⬅️ 用 Material 3 的 colors
                            containerColor = Color(0xFFEDE7F6), // 自定义颜色或用 Color.White
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .menuAnchor(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = false
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        leaveTypes.forEach { selectionOption ->
                            DropdownMenuItem(
                                text = { Text(selectionOption) },
                                onClick = {
                                    selectedLeaveType = selectionOption
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                // 📝 Leave reason
                Text(
                    text = "Reason",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp)
                )
                TextField(
                    value = leaveReason,
                    onValueChange = { leaveReason = it },
                    placeholder = { Text("Please enter the reason for your leave") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp)),
                    singleLine = false,
                    maxLines = 5
                )

                Spacer(modifier = Modifier.height(30.dp))

                Text(
                    text = "Evidence",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Image
                    Image(
                        painter = painterResource(id = R.drawable.img_2),
                        contentDescription = "Selected File",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(30.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // Upload Button
                    Button(onClick = {
                        // Launch file picker for images and PDFs
                        filePickerLauncher.launch("*/*")
                    }) {
                        Text("Upload")
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 16.dp),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Button(
                        onClick = {
                            if (selectedFileUri == null) {
                                Toast.makeText(context, "No file selected", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            if (selectedDates.isEmpty() || selectedLeaveType.isBlank() || leaveReason.isBlank()) {
                                Toast.makeText(context, "Please complete all fields", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                            val formattedLeaveDates = selectedDates.sortedBy { it.time }.map { dateFormat.format(it) }

                            val storage = FirebaseStorage.getInstance()
                            val firestore = FirebaseFirestore.getInstance()
                            val fileName = UUID.randomUUID().toString()
                            val storageRef = storage.reference.child("evidence/$fileName")

                            // Upload file to Firebase Storage
                            storageRef.putFile(selectedFileUri!!)
                                .continueWithTask { task ->
                                    if (!task.isSuccessful) {
                                        throw task.exception ?: Exception("Upload failed")
                                    }
                                    storageRef.downloadUrl
                                }
                                .addOnSuccessListener { downloadUrl ->
                                    // Fetch existing doc IDs
                                    firestore.collection("Leave")
                                        .get()
                                        .addOnSuccessListener { snapshot ->
                                            val existingIds = snapshot.documents.mapNotNull { doc ->
                                                val id = doc.id
                                                if (id.matches(Regex("L\\d{3}"))) id else null
                                            }

                                            // Generate next custom Leave ID like L001, L002, ...
                                            val nextId = (1..999).map {
                                                "L" + it.toString().padStart(3, '0')
                                            }.firstOrNull { id -> id !in existingIds }

                                            if (nextId == null) {
                                                Toast.makeText(context, "No more Leave IDs available", Toast.LENGTH_LONG).show()
                                                return@addOnSuccessListener
                                            }


                                            if (user == null) {
                                                Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show()
                                                return@addOnSuccessListener
                                            }

                                            // Construct leave data map
                                            val leaveData = mapOf(
                                                "id" to user.id,
                                                "name" to user.name,
                                                "leaveDates" to formattedLeaveDates,
                                                "leaveType" to selectedLeaveType,
                                                "reason" to leaveReason,
                                                "evidenceUrl" to downloadUrl.toString(),
                                                "status" to "pending"
                                            )

                                            // Save to Firestore with custom ID
                                            firestore.collection("Leave")
                                                .document(nextId)
                                                .set(leaveData)
                                                .addOnSuccessListener {
                                                    Toast.makeText(context, "Leave submitted!", Toast.LENGTH_SHORT).show()
                                                }
                                                .addOnFailureListener { e ->
                                                    Toast.makeText(context, "Failed to save: ${e.message}", Toast.LENGTH_SHORT).show()
                                                }
                                        }
                                        .addOnFailureListener { e ->
                                            Toast.makeText(context, "Failed to read IDs: ${e.message}", Toast.LENGTH_SHORT).show()
                                        }
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(context, "Upload failed: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                        },
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 16.dp)
                    ) {
                        Text("Confirm")
                    }
                }



                if (user != null) {
                    Text("Name: ${user.name}")
                    Text("Email: ${user.email}")
                    Text("Role: ${user.id}")
                } else {
                    Text("No user is logged in.")
                }

            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun LeaveApplicationPreview() {
    val navController = androidx.navigation.compose.rememberNavController()
    LeaveApplication(navController, isDarkTheme = false)
}
