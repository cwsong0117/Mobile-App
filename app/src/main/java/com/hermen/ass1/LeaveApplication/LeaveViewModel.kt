package com.hermen.ass1.LeaveApplication

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.hermen.ass1.ApplicationStatusModel.ApplicationStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LeaveViewModel: ViewModel() {

    private val db = Firebase.firestore

    fun LeaveStatus(
        leaveId: String,
        employeeId: String,
        leaveType: String,
        dateFrom: String,
        dateTo: String,
        reason: String,
        status: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        generateNextLeaveDocId {
            generateNextApplyId { newId ->
                val leave = hashMapOf(
                    "leaveId" to newId,
                    "employeeId" to employeeId,
                    "leaveType" to leaveType,
                    "dateFrom" to dateFrom,
                    "dateTo" to dateTo,
                    "reason" to reason,
                    "status" to status
                )
                db.collection("Leave")
                    .document(leaveId)
                    .set(leave)
                    .addOnSuccessListener { onSuccess() }
            }
        }
    }

    private fun generateNextLeaveDocId(onIdGenerated: (String) -> Unit) {
        db.collection("Leave")
            .get()
            .addOnSuccessListener { documents ->
                val lastId = documents.mapNotNull {
                    val id = it.id
                    if (id.startsWith("L")) {
                        id.substring(1).toIntOrNull()
                    } else null
                }.maxOrNull() ?: 0

                val nextId = "L" + (lastId + 1).toString().padStart(3, '0')
                onIdGenerated(nextId)
            }
            .addOnFailureListener {
                onIdGenerated("L001")
            }
    }

    private fun generateNextApplyId(onIdGenerated: (String) -> Unit) {
        db.collection("Leave")
            .orderBy("applyId", Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .addOnSuccessListener { document ->
                val lastId = document.firstOrNull()?.getString("leaveId")
                val nextId = if (lastId != null && lastId.startsWith("LA001")) {
                    val num = lastId.substring(2).toInt() + 1
                    "LA" + num.toString().padStart(3, '0')
                } else {
                    "LA001"
                }
                onIdGenerated(nextId)
            }
            .addOnFailureListener {
                onIdGenerated("LA001")
            }
    }

    private val _leaveList = MutableStateFlow<List<LeaveStatus>>(emptyList())
    val leaveList: StateFlow<List<LeaveStatus>> = _leaveList

    init {
        fetchLeaveList()
    }

    fun fetchLeaveList() {
        db.collection("Leave")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("Firebase", "Error fetching data", error)
                    return@addSnapshotListener
                }

                val requests = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        LeaveStatus(
                            leaveId = doc.getString("leaveId") ?: "",
                            name = doc.getString("name") ?: "",
                            dateFrom = doc.getString("dateFrom") ?: "",
                            dateTo = doc.getString("dateTo") ?: "",
                            reason = doc.getString("reason") ?: "",
                            leaveType = doc.getString("leaveType") ?: "",
                            status = doc.getString("status") ?: "Pending",
                            employeeId = doc.getString("id") ?: ""
                        )
                    } catch (e: Exception) {
                        Log.e("Firebase", "Document parsing error", e)
                        null
                    }
                } ?: emptyList()
                _leaveList.value = requests
                Log.d("Firebase", "Loaded ${requests.size} requests")
            }
    }

    fun updateLeaveList(leaveId: String, newStatus: String) {
        db.collection("Leave")
            .whereEqualTo("leaveId", leaveId)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val docId = documents.first().id
                    db.collection("Leave")
                        .document(docId)
                        .update("status", newStatus)
                        .addOnSuccessListener {
                            Log.d("Firebase", "Status updated to $newStatus")
                        }
                        .addOnFailureListener { e ->
                            Log.e("Firebase", "Error updating status", e)
                        }
                } else {
                    Log.e("Firebase", "No matching document found for applyId: $leaveId")
                        }
                }
            .addOnFailureListener { e ->
                Log.e("Firebase", "Failed to fetch document for status update", e)
            }
    }
}