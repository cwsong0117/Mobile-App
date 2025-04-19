package com.hermen.ass1.LeaveApplication

data class LeaveStatus(
    val name: String,
    val leaveId: String,
    val employeeId: String,
    val leaveType: String,
    val dateFrom: String,
    val dateTo: String,
    val reason: String,
    val status: String
)
