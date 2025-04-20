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

    suspend fun getAnnouncementById(id: String): Announcement? {
        return try {
            val snapshot = announcementRef.document(id).get().await()
            snapshot.toAnnouncement()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun createAnnouncement(announcement: Announcement) {
        try {
            announcementRef.add(announcement).await()
        } catch (e: Exception) {
            // Handle error
        }
    }

    suspend fun updateAnnouncement(id: String, announcement: Announcement) {
        try {
            announcementRef.document(id).set(announcement).await()
        } catch (e: Exception) {
            // Handle error
        }
    }
}
