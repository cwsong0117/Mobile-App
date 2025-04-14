package com.hermen.ass1.MeetingRoom

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore

class MeetingRoomViewModel: ViewModel() {

    private val db = Firebase.firestore

    fun submitApplication(
        name: String,
        date: String,
        startTime: String,
        endTime: String,
        purpose: String,
        type: String,
        onSuccess: () -> Unit,
        onError:(Exception) -> Unit
    ) {
        generateNextId { newId ->
            val room = hashMapOf(
                "applyId" to newId,
                "name" to name,
                "date" to date,
                "startTime" to startTime,
                "endTime" to endTime,
                "purpose" to purpose,
                "type" to type
            )
            db.collection("Room")
                .add(room)
                .addOnSuccessListener { onSuccess() }
                .addOnFailureListener { e -> onError(e) }
        }
    }
    private fun generateNextId(onIdGenerated: (String) -> Unit) {
        db.collection("Room")
            .orderBy("applyId", Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .addOnSuccessListener { document ->
                val lastId = document.firstOrNull()?.getString("applyId")
                val nextId = if (lastId != null && lastId.length >= 5) {
                    val num = lastId.substring(2).toInt() + 1 //only view the digit after AP
                    "AP" + num.toString().padStart(3, '0')
                } else {
                    "AP001"
                }
                onIdGenerated(nextId)
            }
            .addOnFailureListener {
                onIdGenerated("AP001") //fallback once fails to fetch last ID
            }
    }
}

