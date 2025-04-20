package com.hermen.ass1.ui.theme

data class LeaveRequest(
    val evidenceUrl: String = "",
    val leaveDates: List<String> = emptyList(),
    val leaveType: String = "",
    val reason: String = "",
    val status: String = ""
)
