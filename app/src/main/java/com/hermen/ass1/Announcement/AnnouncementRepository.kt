package com.hermen.ass1.Announcement

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.hermen.ass1.Announcement.Announcement
import kotlinx.coroutines.tasks.await

object AnnouncementRepository {

    private val announcementRef
        get() = Firebase.firestore.collection("Announcement")

    suspend fun getAnnouncements(): List<Announcement> {
        return try {
            val snapshot = announcementRef.get().await()
            snapshot.documents.mapNotNull { it.toAnnouncement() }
        } catch (e: Exception) {
            emptyList()
        }
    }
}

