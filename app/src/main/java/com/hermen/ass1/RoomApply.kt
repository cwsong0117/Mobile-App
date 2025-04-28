package com.hermen.ass1

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.hermen.ass1.MeetingRoom.MeetingRoom
import com.hermen.ass1.MeetingRoom.MeetingRoomResource
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.hermen.ass1.ApplicationStatusModel.ApplicationStatus
import android.graphics.Color.BLACK
import android.graphics.Color.WHITE
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hermen.ass1.MeetingRoom.RoomViewModel
import androidx.compose.ui.graphics.Color
import com.hermen.ass1.User.SessionManager

@Composable
fun MeetingRoomApply(navController: NavController, isDarkTheme: Boolean) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val topBarTitle = if (selectedTabIndex == 0) "Meeting Room" else "Application Status"
    val backgroundColor = if (isDarkTheme) Color.Black else Color(0xFFE5FFFF)

    Column(
        modifier = Modifier
            .background(backgroundColor)
            .fillMaxSize()
    ) {
        BackButton(navController = navController, title = topBarTitle, isDarkTheme = isDarkTheme)
        MeetingRoomTabs(selectedTabIndex, onTabSelected = { selectedTabIndex = it })

        when (selectedTabIndex) {
            0 -> ApplyTabContent(navController)
            1 -> StatusTabContent(navController, isDarkTheme)
        }
    }
}

@Composable
fun MeetingRoomList(meetingRoomsList: List<MeetingRoom>, navController: NavController) {

    LazyColumn(
        modifier = Modifier
            .padding(top = 10.dp),

    ) {
        items(meetingRoomsList) { meetingRoom ->
            MeetingRoomCard(
                meetingRoom = meetingRoom,
                navController = navController,
                modifier = Modifier .padding(8.dp)
            )
        }
    }
}

@Composable
fun MeetingRoomCard(
    meetingRoom: MeetingRoom,
    navController: NavController,
    modifier: Modifier = Modifier) {
    val context = LocalContext.current

    Card(
        modifier = modifier
            .padding(horizontal = 10.dp, vertical = 8.dp)
    ) {
        Box(
            modifier = Modifier
        ) {
           Image(
               painter = painterResource(meetingRoom.meetingRoomImageResourceId),
               contentDescription = stringResource(meetingRoom.meetingRoomStringResourceId),
               modifier = Modifier
                   .background(
                       color = Color.White.copy(alpha = 0.8f),
                   )
                   .fillMaxWidth()
                   .height(200.dp)
                   .clickable {
                       val rawName = context.getString(meetingRoom.meetingRoomStringResourceId)
                       navController.navigate("roomDetail/${rawName}")
                       Log.d("TEST", "Navigating to: $rawName")
                   },
               contentScale = ContentScale.Crop

           )

            Log.d("DEBUG", "Room ID : ${context.getString(meetingRoom.meetingRoomStringResourceId)}")

            // Gradient Overlay for Center-left Transparency
        Box(
            modifier = Modifier
                .width(180.dp)
                .height(70.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.7f), // Darker shade at the left
                            Color.Transparent, // Fully transparent in middle
                        ),
                        startX = 300f, //start fading at the left
                        endX = -150f
                    ),
                    shape = RoundedCornerShape(0.dp, 150.dp, 150.dp, 0.dp)
                ),
        ) {
            Column(verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()) {
                Text(
                    text = if(LocalContext.current.getString(meetingRoom.meetingRoomStringResourceId) == "Huddle Room") {
                        "Huddle\nRoom"
                    } else {
                        LocalContext.current.getString(meetingRoom.meetingRoomStringResourceId)
                    },
//                    .replace(" ", "\n"),
                    color = Color.Black,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .width(160.dp)
                        .height(200.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
        }
    }
}

@Composable
fun MeetingRoomTabs(selectedIndex: Int, onTabSelected: (Int) -> Unit) {
    val tabTitles = listOf("Apply", "Status")
    Divider(
        color = Color.LightGray,
        thickness = 1.dp
    )

    TabRow(selectedTabIndex = selectedIndex) {
        tabTitles.forEachIndexed { index, title ->
            Tab(
                selected = selectedIndex == index,
                onClick = { onTabSelected(index) },
                text = {
                    Text(
                        title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                }
            )
        }
    }
}

@Composable
fun ApplyTabContent(navController: NavController) {
    val meetingRoomsList = MeetingRoomResource().loadMeetingRooms()
    MeetingRoomList(meetingRoomsList = meetingRoomsList, navController = navController)
}

@Composable
fun StatusTabContent(navController: NavController, isDarkTheme: Boolean) {
    Status(navController, isDarkTheme)
}

@Composable
fun Status(navController: NavController, isDarkTheme: Boolean) {
    StatusScreen(navController, viewModel(), isDarkTheme = isDarkTheme)
}

@Composable
fun StatusScreen(navController: NavController, viewModel: RoomViewModel = viewModel(), isDarkTheme: Boolean) {
    val requestList by viewModel.requestList.collectAsState()
    var searchText by remember { mutableStateOf("") }
    // You can add a loading state if needed
    var isLoading by remember { mutableStateOf(true) }
    val user = SessionManager.currentUser!!
    val userId = user.id
    val isAdmin = userId.startsWith("A")
    // Step 1: First, filter by visibility
    val visibleList = if (isAdmin) {
        requestList
    } else {
        requestList.filter { it.userId == userId }
    }

// Step 2: Then, apply search filter if needed
    val filteredList = if (searchText.isNotBlank()) {
        visibleList.filter { request ->
            if (userId.startsWith("A")) {
                // Admin: filter by name or roomType
                request.name.contains(searchText, ignoreCase = true) ||
                        request.roomType.contains(searchText, ignoreCase = true) ||
                            request.status.contains(searchText, ignoreCase = true)
            } else {
                // Staff: filter only by roomType
                request.roomType.contains(searchText, ignoreCase = true) ||
                        request.status.contains(searchText, ignoreCase = true)
            }
        }
    } else {
        visibleList
    }

    LaunchedEffect(Unit) {
        viewModel.fetchRequestList()
        isLoading = false
    }
    //Search Bar
    SearchBar(
        searchText,
        onSearchChanged = {searchText = it},
        isAdmin = isAdmin
    )

    if (isLoading) {
        // Show loading indicator
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            // Add your loading indicator here
            Text("Loading...")
        }
    } else {
        if (filteredList.isEmpty()) {
            // Show empty state
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No requests found")
            }
        } else {
            LazyColumn {
                items(filteredList) { request ->
                    ApplicationStatusCard(navController, request)
                }
            }
        }
    }
}

@Composable
fun SearchBar(searchText: String, onSearchChanged: (String) -> Unit, isAdmin: Boolean) {
    val placeholderText = if (isAdmin) {
        "Search based on Name, Room Type, and Status"
    } else {
        "Search based on Room Type and Status"
    }

    TextField(
        value = searchText,
        onValueChange = onSearchChanged,
        placeholder = { Text(placeholderText, color = Color.Gray) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(20.dp),
        singleLine = true,
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color.White, // Set the background color
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        ),
        leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = "Search Icon")
        }
    )
}


@Composable
fun ApplicationStatusCard(navController: NavController, request: ApplicationStatus) {

    val textColor = Color.Black

    Spacer(modifier = Modifier.height(24.dp))
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .height(115.dp)
            .clickable {
                Log.d("DEBUG", "ApplicationStatusCard clicked")
                navController.navigate("status_details/${request.applyId}")
            },
        colors = CardDefaults.cardColors(containerColor = Color.LightGray) //change color later
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
        ) {
            Row(modifier = Modifier
                .fillMaxWidth()){
                Text(text = "Request: ${request.roomType}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    fontFamily = FontFamily.Serif,
                    color = textColor
                )
            }
            Row {
                Text(text = "From: ${request.name}",
                    fontSize = 18.sp,
                    color = textColor)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "...",
                    color = textColor
                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "Status: ${request.status}",
                    color = when (request.status.lowercase()) {
                        "pending" -> Color.Yellow
                        "approved" -> Color.Green
                        "rejected" -> Color.Red
                        else -> Color.Black
                    }
                )
            }
        }
    }
}

@Composable
fun StatusDetails(navController: NavController, applyId: String, viewModel: RoomViewModel = viewModel(), isDarkTheme: Boolean) {
    val requestList = viewModel.requestList.collectAsState()
    val selectedRequest = requestList.value.firstOrNull { it.applyId == applyId }
    var qrBitmap by remember { mutableStateOf<Bitmap?>(null) }
    val cyanInButton = Color(0xFF0099cc)
    val user = SessionManager.currentUser!!
    val userId = user.id
    val isAdmin = userId.startsWith("A") == true
    var isQrCodeVisible by remember { mutableStateOf(true) }
    val background = if (isDarkTheme) Color.Transparent else Color(0xFFE5FFFF)
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            BackButton(navController = navController, title = "Request From: ${selectedRequest?.name ?: "Unknown!"}", isDarkTheme = isDarkTheme)

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                selectedRequest?.let {
                    Column {
                        Row{
                            Text(text = "Request For: ",
                                fontSize = 24.sp)
                            Text(it.roomType,
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp,)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(text = "Applicants: ${it.name}",
                            fontSize = 24.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(text = "User ID: ${it.userId}",
                            fontSize = 24.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(text = "Date: ${it.date}",
                            fontSize = 24.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(text = "Time: ${it.startTime} - ${it.endTime}",
                            fontSize = 24.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(text = "Purpose: ${it.purpose}",
                            fontSize = 24.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Row {
                            Text(text = "Status: ",
                                fontSize = 24.sp)
                            Text(
                                text = it.status,
                                color = when (it.status.lowercase()) {
                                    "pending" -> Color(0xFFD59B00)
                                    "approved" -> Color(0xFF00A900)
                                    "rejected" -> Color(0xFFFF0000)
                                    else -> Color.Black
                                },
                                fontSize = 24.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            if (isAdmin) {
                                Log.d("NAVIGATION", "Passed room name: ${selectedRequest.roomType}")
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    Button(
                                        colors = ButtonDefaults.buttonColors(
                                            backgroundColor = cyanInButton,
                                            contentColor = Color.White
                                        ),
                                        onClick = {
                                            viewModel.updateRoomStatus(selectedRequest.applyId, "Approved")
                                        }
                                    ) {
                                        Text("Approve",color = Color.White)
                                    }

                                    DeleteButtonWithConfirmation(
                                        applyId = selectedRequest.applyId,
                                        viewModel = viewModel
                                    )

                                    Button(
                                        colors = ButtonDefaults.buttonColors(
                                            backgroundColor = Color.Red,
                                            contentColor = Color.White
                                        ),
                                        onClick = {
                                            viewModel.updateRoomStatus(selectedRequest.applyId, "Rejected")
                                            // hide the qr code
                                            isQrCodeVisible = false
                                            //remove the qr bitmap
                                            qrBitmap = null
                                        }
                                    ) {
                                        Text("Reject", color = Color.White)
                                    }
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                            if (it.status.lowercase() == "approved") {
                                Button(colors = ButtonDefaults.buttonColors(
                                    backgroundColor = cyanInButton,
                                    contentColor = Color.White
                                ),
                                    onClick = {
                                    val qrText = """
                                Apply ID: ${it.applyId}
                                Room: ${it.roomType}
                                User ID: ${it.userId}
                                Name: ${it.name}
                                Date: ${it.date}
                                Time: ${it.startTime} - ${it.endTime}
                                Purpose: ${it.purpose}
                                Status: ${it.status}
                                
                                You can use this QR Code to unlock the room.
                            """.trimIndent()
                                    qrBitmap = generateQRCode(qrText)
                                },
                                ) {
                                    Text("Generate QR Code", color = Color.White)
                                }
                                qrBitmap?.let { bitmap ->
                                    Spacer(modifier = Modifier.height(30.dp))
                                    Image(
                                        bitmap = bitmap.asImageBitmap(),
                                        contentDescription = "QR Code",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(300.dp)
                                            .padding(8.dp)
                                    )
                                }
                            } else {
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Your application has not been approved yet. QR Code is unavailable.",
                                    color = Color.Red,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

fun generateQRCode(text: String, size: Int = 512): Bitmap? {
    return try {
        val bitMatrix: BitMatrix = MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, size, size)
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        for (x in 0 until width) {
            for (y in 0 until height) {
                bmp.setPixel(x, y, if (bitMatrix.get(x, y)) BLACK else WHITE)
            }
        }
        bmp
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

@Composable
fun DeleteButtonWithConfirmation(
    applyId: String,
    viewModel: RoomViewModel
) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Confirm Deletion") },
            text = { Text("Are you sure you want to delete this application?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteApplication(
                        applyId,
                        onSuccess = {
                            Toast.makeText(context, "Application deleted", Toast.LENGTH_SHORT).show()
                            showDialog = false
                        },
                        onFailure = {
                            Toast.makeText(context, "Failed to delete", Toast.LENGTH_SHORT).show()
                            showDialog = false
                        }
                    )
                }) {
                    Text("Delete", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Button(
        onClick = { showDialog = true },
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color.White,
            contentColor = Color.Red
        ),
        border = BorderStroke(2.dp, Color.Red)
    ) {
        Text("Delete", color = Color.Red)
    }
}

@Preview(showBackground = true)
@Composable
fun StatusPreview() {
    StatusDetails(navController = rememberNavController(), applyId = "AP001", isDarkTheme = false)
}