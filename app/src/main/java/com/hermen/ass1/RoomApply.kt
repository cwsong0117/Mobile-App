package com.hermen.ass1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.hermen.ass1.ui.theme.Ass1Theme

@Composable
fun MeetingRoomApply() {
    Scaffold(
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
            .padding(top = 60.dp)
    ) {
        items(meetingRoomsList) { meetingRoom ->
            meetingRoomCard(
                meetingRoom = meetingRoom,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@Composable
fun meetingRoomCard(
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
                       color = Color.White.copy(alpha = 0.5f),
                   )
                   .fillMaxWidth()
                   .height(200.dp),

               contentScale = ContentScale.Crop
           )
            // Gradient Overlay for Center-left Transparency
        Box(
            modifier = Modifier
                .width(180.dp)
                .height(200.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.6f), // Darker shade at the left
                            Color.Transparent, // Fully transparent in middle
                        ),
                        startX = 300f, //start fading at the left
                        endX = -150f
                    ),
                    shape = RoundedCornerShape(0.dp, 150.dp, 150.dp, 0.dp)
                ),
        ) {
            Text(
                text = LocalContext.current.getString(meetingRoom.meetingRoomStringResourceId)
                    .replace(" ", "\n"),
                color = Color.Black,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .wrapContentHeight()
                    .width(180.dp)
                    .align(Alignment.Center)
                    .height(200.dp),
                textAlign = TextAlign.Center
            )
        }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MeetingRoomApplyPreview() {
    Ass1Theme {
        MeetingRoomApply()
    }
}