package com.hermen.ass1.ApplicationStatusModel

data class ApplicationStatus(
    val applyId: String,
    val name: String,
    val date: String,
    val startTime: String,
    val endTime: String,
    val purpose: String,
    val roomType: String,
    val status: String,
    val userId: String
)
