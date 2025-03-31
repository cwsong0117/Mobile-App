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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import com.hermen.ass1.ui.theme.Ass1Theme

@Composable
fun MeetingRoomApply() {
    MeetingRoomList(
        meetingRoomsList = MeetingRoomResource().loadMeetingRooms()
    )
}

@Composable
fun meetingRoomCard(
    meetingRoom: MeetingRoom,
    modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
           Image(
               painter = painterResource(meetingRoom.meetingRoomImageResourceId),
               contentDescription = stringResource(meetingRoom.meetingRoomStringResourceId),
               modifier = Modifier
                   .fillMaxWidth()
                   .height(200.dp),
               contentScale = ContentScale.Crop
           )
            // Gradient Overlay for Center-left Transparency
            Box(
                modifier = Modifier
                    .width(190.dp)
                    .height(200.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.5f), // Darker shade at the left
                                Color.Transparent, // Fully transparent in middle
                            ),
                            startX = 0f, //start fading at the left
                            endX = 300f
                        )
                    )
            ) {
                Text(
                    text = LocalContext.current.getString(meetingRoom.meetingRoomStringResourceId),
                    color = Color.Black,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier
                        .fillMaxHeight()
                        .background(
                            color = Color.White.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(100.dp)
                        )
                        .padding(16.dp)
                )
            }

       }
    }
}

@Composable
fun MeetingRoomList(meetingRoomsList: List<MeetingRoom>) {
    LazyColumn(

    ) {
        items(meetingRoomsList) { meetingRoom ->
            meetingRoomCard(
                meetingRoom = meetingRoom,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}