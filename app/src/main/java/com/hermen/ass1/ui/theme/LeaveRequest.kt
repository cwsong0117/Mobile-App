package com.hermen.ass1.ui.theme

data class LeaveRequest(
    val id: String = "",
    val name: String = "",
    val evidenceUrl: String = "",
    val leaveDates: List<String> = emptyList(),
    val leaveType: String = "",
    val reason: String = "",
    val status: String = ""
)
