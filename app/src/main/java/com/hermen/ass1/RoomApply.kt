package com.hermen.ass1

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.lazy.items

@Composable
fun MeetingRoomApply() {
    MeetingRoomList(
        meetingRoomsList = MeetingRoomResource().loadMeetingRooms()
    )
}

@Composable
fun MeetingRoomList(meetingRoomsList: List<MeetingRoom>) {
    LazyColumn {
        items(meetingRoomsList) { meetingRoom ->
            meetingRoomCard(
                meetingRoom = meetingRoom,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@Composable
fun meetingRoomCard(meetingRoom: MeetingRoom, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
       Column {
           Image(
               painter = painterResource(meetingRoom.meetingRoomImageResourceId),
               contentDescription = stringResource(meetingRoom.meetingRoomStringResourceId),
               modifier = Modifier
                   .fillMaxWidth()
                   .height(194.dp),
               contentScale = ContentScale.Crop
           )
           Text(
               text = LocalContext.current.getString(meetingRoom.meetingRoomStringResourceId),
               color = Color.Black,
               fontSize = 25.sp,
               fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
               style = MaterialTheme.typography.headlineSmall,
               modifier = Modifier
                   .background(
                       color = Color.Black.copy(alpha = 0.2f),
                       shape = RoundedCornerShape(10.dp)
                   )
                   .padding(16.dp)
           )
       }
    }
}