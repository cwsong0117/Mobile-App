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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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

@Composable
fun MeetingRoomApply(navController: NavController) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val topBarTitle = if (selectedTabIndex == 0) "Meeting Room" else "Application Status"

    Column(
        modifier = Modifier
            .background(Color(0xFFe5ffff))
            .fillMaxSize()
    ) {
        BackButton(navController = navController, title = topBarTitle)
        MeetingRoomTabs(selectedTabIndex) { selectedTabIndex = it }

        when (selectedTabIndex) {
            0 -> ApplyTabContent(navController)
            1 -> StatusTabContent(navController)
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
                       navController.navigate("roomDetail/$rawName")
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
fun BackButton(navController: NavController, title: String) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .height(46.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_arrow_back_ios_new_24),
                    contentDescription = "Back",
                    modifier = Modifier.size(24.dp)
                )
            }
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp),
                color = Color.Black
            )
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
fun StatusTabContent(navController: NavController) {
    Status(navController)
}

@Composable
fun Status(navController: NavController) {
    StatusScreen(navController)
}

//sharable function that easy for apply
fun getRequestList(): List<ApplicationStatus> {
    return listOf(
        ApplicationStatus(
            applyId = "AP001",
            name = "blablabla",
            date = "12-04-2025",
            startTime = "10:00",
            endTime = "12:00",
            purpose = "meeting",
            roomType = "Huddle Room",
            status = "approved"
        ),
        ApplicationStatus(
            applyId = "AP002",
            name = "hahahaha",
            date = "18-04-2025",
            startTime = "10:00",
            endTime = "12:00",
            purpose = "phone call",
            roomType = "Phone Booth Room",
            status = "Pending"
        )
    )
}

@Composable
fun StatusScreen(navController: NavController) {
    val requestList = getRequestList()

    Column {
        LazyColumn {
            items(requestList.size) { index ->
                ApplicationStatusCard(navController, requestList[index])
            }
        }
    }
}

@Composable
fun ApplicationStatusCard(navController: NavController, request: ApplicationStatus) {
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
                    fontFamily = FontFamily.Serif
                )
            }
            Row {
                Text(text = "From: ${request.name}",
                    fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "..."
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
fun StatusDetails(navController: NavController, applyId: String) {
    val requestList = getRequestList()
    val selectedRequest = requestList.find { it.applyId == applyId }
    var qrBitmap by remember { mutableStateOf<Bitmap?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFe5ffff))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            BackButton(navController = navController, title = "Request From: ${selectedRequest?.name ?: "Unknown"}")

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                selectedRequest?.let {
                    Column {
                        Text(text = "Request For: ${it.roomType}")
                        Text(text = "From: ${it.name}")
                        Text(text = "Date: ${it.date}")
                        Text(text = "Time: ${it.startTime} - ${it.endTime}")
                        Text(text = "Purpose: ${it.purpose}")
                        Row {
                            Text(text = "Status: ")
                            Text(
                                text = it.status,
                                color = when (it.status.lowercase()) {
                                    "pending" -> Color(0xFFD59B00)
                                    "approved" -> Color(0xFF00A900)
                                    "rejected" -> Color(0xFFFF0000)
                                    else -> Color.Black
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        if (it.status.lowercase() == "approved") {
                            Button(onClick = {
                                val qrText = """
                                Room: ${it.roomType}
                                Name: ${it.name}
                                Date: ${it.date}
                                Time: ${it.startTime} - ${it.endTime}
                                Purpose: ${it.purpose}
                                Status: ${it.status}
                            """.trimIndent()
                                qrBitmap = generateQRCode(qrText)
                            }) {
                                Text("Generate QR Code")
                            }

                            qrBitmap?.let { bitmap ->
                                Spacer(modifier = Modifier.height(16.dp))
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


@Preview(showBackground = true)
@Composable
fun StatusPreview() {
    StatusDetails(navController = rememberNavController(), applyId = "AP001")
}

//@Preview(showBackground = true)
//@Composable
//fun MeetingRoomApplyPreview() {
//    Ass1Theme {
//        MeetingRoomApply(navController = rememberNavController())
//    }
//}