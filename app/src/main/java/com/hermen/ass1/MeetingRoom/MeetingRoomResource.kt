package com.hermen.ass1.MeetingRoom

import com.hermen.ass1.MeetingRoom.MeetingRoom
import com.hermen.ass1.R

class MeetingRoomResource {
    fun loadMeetingRooms() : List<MeetingRoom> {
        return listOf<MeetingRoom>(
            MeetingRoom(R.string.meetingRoom1, R.drawable.meeting_room1),
            MeetingRoom(R.string.meetingRoom2, R.drawable.meeting_room2),
            MeetingRoom(R.string.meetingRoom3, R.drawable.meeting_room3),
            MeetingRoom(R.string.meetingRoom4, R.drawable.meeting_room4)
        )
    }
}