
package com.hermen.ass1.Announcement

import kotlinx.serialization.Serializable
import com.google.firebase.firestore.DocumentSnapshot
import java.text.SimpleDateFormat
import java.util.*

@Serializable
data class Announcement(
    val id: String = "",
    val title: String = "",
    val content: String = "",
    val employeeID: String = "",
    val created_at: String = "",
    val imageUrl: String = ""
)

fun DocumentSnapshot.toAnnouncement(): Announcement {
    val id = id
    val title = getString("title") ?: ""
    val content = getString("content") ?: ""
    val employeeID = getString("employeeID") ?: ""
    val imageUrl = getString("imageUrl") ?: ""
    val createdAt = getString("created_at") ?: ""

    return Announcement(
        id = id,
        title = title,
        content = content,
        employeeID = employeeID,
        created_at = createdAt,
        imageUrl = imageUrl
    )
}
