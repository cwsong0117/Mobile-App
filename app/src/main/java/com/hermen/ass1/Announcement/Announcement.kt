package com.hermen.ass1.Announcement

import kotlinx.serialization.Serializable
import com.google.firebase.firestore.DocumentSnapshot
import java.text.SimpleDateFormat
import java.util.*

@Serializable
data class Announcement(
    val title: String = "",
    val content: String = "",
    val employeeID: String = "",
    val created_at: String = ""
)

fun DocumentSnapshot.toAnnouncement(): Announcement {
    val title = getString("title") ?: ""
    val content = getString("content") ?: ""
    val employeeID = getString("employeeID") ?: ""

    // Firebase Timestamp to Date
    val timestamp = getTimestamp("created_at")?.toDate() ?: Date()

    // Format the timestamp to only include the date
    val formattedDate = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(timestamp)

    return Announcement(
        title = title,
        content = content,
        employeeID = employeeID,
        created_at = formattedDate
    )
}
