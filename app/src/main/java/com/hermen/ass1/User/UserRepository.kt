package com.hermen.ass1.User

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.storage.FirebaseStorage

object UserRepository {

    private val userRef
        get() = Firebase.firestore.collection("User")

    suspend fun getUsers(): List<User> {
        return try {
            val snapshot = userRef.get().await()
            snapshot.documents.mapNotNull { it.toUser() }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getUserById(userId: String): User? {
        return try {
            val doc = userRef.document(userId).get().await()
            if (doc.exists()) doc.toUser() else null
        } catch (e: Exception) {
            null
        }
    }

}

