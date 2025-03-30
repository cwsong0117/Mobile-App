package com.hermen.ass1.MeetingRoomModel

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class MeetingRoom(
    @StringRes val meetingRoomStringResourceId: Int,
    @DrawableRes val meetingRoomImageResourceId: Int
)
