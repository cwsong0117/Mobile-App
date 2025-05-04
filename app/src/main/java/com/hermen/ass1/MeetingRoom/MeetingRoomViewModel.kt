package com.hermen.ass1.MeetingRoom

import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.hermen.ass1.ApplicationStatusModel.ApplicationStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MeetingRoomViewModel: ViewModel() {

    private val db = Firebase.firestore

    fun submitApplication(
        name: String,
        date: String,
        startTime: String,
        endTime: String,
        purpose: String,
        type: String,
        status: String,
        userId: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        generateNextDocId { docId ->
            generateNextApplyId { newId ->
                val room = hashMapOf(
                    "applyId" to newId,
                    "name" to name,
                    "date" to date,
                    "startTime" to startTime,
                    "endTime" to endTime,
                    "purpose" to purpose,
                    "type" to type,
                    "status" to status,
                    "userId" to userId
                )
                db.collection("Room")
                    .document(docId)
                    .set(room)
                    .addOnSuccessListener { onSuccess() }
                    .addOnFailureListener { e -> onError(e) }
            }
        }
    }

    private fun generateNextDocId(onIdGenerated: (String) -> Unit) {
        db.collection("Room")
            .get()
            .addOnSuccessListener { documents ->
                val lastId = documents.mapNotNull {
                    val id = it.id
                    if (id.startsWith("R")) {
                        id.substring(1).toIntOrNull()
                    } else null
                }.maxOrNull() ?: 0

                val nextId = "R" + (lastId + 1).toString().padStart(3, '0')
                onIdGenerated(nextId)
            }
            .addOnFailureListener {
                onIdGenerated("R001")
            }
    }

    private fun generateNextApplyId(onIdGenerated: (String) -> Unit) {
        db.collection("Room")
            .orderBy("applyId", Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .addOnSuccessListener { document ->
                val lastId = document.firstOrNull()?.getString("applyId")
                val nextId = if (lastId != null && lastId.startsWith("AP")) {
                    val num = lastId.substring(2).toInt() + 1
                    "AP" + num.toString().padStart(3, '0')
                } else {
                    "AP001"
                }
                onIdGenerated(nextId)
            }
            .addOnFailureListener {
                onIdGenerated("AP001")
            }
    }
}

class RoomViewModel : ViewModel() {
    private val _requestList = MutableStateFlow<List<ApplicationStatus>>(emptyList())
    val requestList: StateFlow<List<ApplicationStatus>> = _requestList
    private val db = Firebase.firestore

    init {
        fetchRequestList()
    }

    fun fetchRequestList() {
        db.collection("Room")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("Firebase", "Error fetching data", error)
                    return@addSnapshotListener
                }

                val requests = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        ApplicationStatus(
                            applyId = doc.getString("applyId") ?: "",
                            name = doc.getString("name") ?: "",
                            date = doc.getString("date") ?: "",
                            startTime = doc.getString("startTime") ?: "",
                            endTime = doc.getString("endTime") ?: "",
                            purpose = doc.getString("purpose") ?: "",
                            roomType = doc.getString("type") ?: "",
                            status = doc.getString("status") ?: "Pending",
                            userId = doc.getString("userId") ?: ""
                        )
                    } catch (e: Exception) {
                        Log.e("Firebase", "Document parsing error", e)
                        null
                    }
                } ?: emptyList()

                _requestList.value = requests
                Log.d("Firebase", "Loaded ${requests.size} requests")
            }
    }
    fun updateRoomStatus(applyId: String, newStatus: String) {
        db.collection("Room")
            .whereEqualTo("applyId", applyId)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val docId = documents.first().id
                    db.collection("Room")
                        .document(docId)
                        .update("status", newStatus)
                        .addOnSuccessListener {
                            Log.d("Firebase", "Status updated to $newStatus")
                        }
                        .addOnFailureListener { e ->
                            Log.e("Firebase", "Error updating status", e)
                        }
                } else {
                    Log.e("Firebase", "No matching document found for applyId: $applyId")
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firebase", "Failed to fetch document for status update", e)
            }
    }
    fun deleteApplication(applyId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("Room")
            .whereEqualTo("applyId", applyId)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val docId = documents.first().id
                    db.collection("Room")
                        .document(docId)
                        .delete()
                        .addOnSuccessListener {
                            Log.d("Firebase", "Application with applyId $applyId deleted successfully")
                            onSuccess()
                        }
                        .addOnFailureListener { e ->
                            Log.e("Firebase", "Error deleting document", e)
                            onFailure(e)
                        }
                } else {
                    Log.e("Firebase", "No application found with applyId: $applyId")
                    onFailure(Exception("Application not found"))
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firebase", "Error querying application to delete", e)
                onFailure(e)
            }
    }
}

class MeetingRoomFormViewModel : ViewModel() {
    var name by mutableStateOf("")
    var date by mutableStateOf("")
    var startTime by mutableStateOf("")
    var endTime by mutableStateOf("")
    var purpose by mutableStateOf("")
    var customPurpose by mutableStateOf("")
    var searchText by mutableStateOf("")
}