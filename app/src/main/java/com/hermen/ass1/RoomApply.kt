package com.hermen.ass1

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import com.hermen.ass1.MeetingRoomModel.MeetingRoom
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.hermen.ass1.ui.theme.Ass1Theme

@Composable
fun MeetingRoomApply(navController: NavController) {
    Scaffold(
        topBar = {
            BackButton(navController = navController)
        },
        bottomBar = {
            BottomNavigationBar()
        }
    ) {
        innerPadding ->
        Box(
            modifier = Modifier.padding(innerPadding)
        ) {
          MeetingRoomList(
              meetingRoomsList = MeetingRoomResource().loadMeetingRooms()
          )
        }
    }
}

@Composable
fun MeetingRoomList(meetingRoomsList: List<MeetingRoom>) {
    LazyColumn(
        modifier = Modifier
    ) {
        items(meetingRoomsList) { meetingRoom ->
            MeetingRoomCard(
                meetingRoom = meetingRoom,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@Composable
fun MeetingRoomCard(
    meetingRoom: MeetingRoom,
    modifier: Modifier = Modifier) {

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
                   .height(200.dp),

               contentScale = ContentScale.Crop
           )
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
fun BackButton(navController: NavController) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(top = 20.dp)
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
//        Text(
//            text = "Meeting Room",
//            fontSize = 20.dp,
//            fontWeight = FontWeight.Bold,
//            modifier = Modifier.padding(start = 8.dp)
//        )
        }
    }

}

//@Preview(showBackground = true)
//@Composable
//fun MeetingRoomApplyPreview() {
//    Ass1Theme {
//        MeetingRoomApply()
//    }
//}