package com.hermen.ass1.Announcement

import android.util.Log
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.hermen.ass1.Announcement.Announcement
import com.hermen.ass1.User.SessionManager
import kotlinx.coroutines.tasks.await

object AnnouncementRepository {

    private val announcementRef
        get() = Firebase.firestore.collection("Announcement")

    suspend fun getAnnouncements(): List<Announcement> {
        val snapshot = FirebaseFirestore.getInstance().collection("Announcement").get().await()

        Log.d("RepoDebug", "Fetched data: ${snapshot.documents}")
        return snapshot.documents.map { doc ->
            val announcement = doc.toAnnouncement()
            Log.d("RepoDebug", "Announcement: $announcement")
            announcement
        }
    }


    suspend fun getAnnouncementById(id: String): Announcement? {
        return try {
            val snapshot = announcementRef.document(id).get().await()
            snapshot.toAnnouncement() // Automatically gets the document ID
        } catch (e: Exception) {
            null
        }
    }


}
