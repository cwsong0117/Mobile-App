package com.hermen.ass1.Attendance

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Query

data class Attendance(
    val attendanceID: String = "",
    val clockOutTime: Timestamp? = null,
    val clockInTime: Timestamp? = null,
    val employeeID: String = "",
    val status: String = ""
)

class AttendanceViewModel : ViewModel() {
    private val db = Firebase.firestore
    val attendance = mutableStateListOf<Attendance>()

    fun getAttendance(onComplete: () -> Unit) {
        db.collection("Attendance")
            .get()
            .addOnSuccessListener { result ->
                attendance.clear()
                for (document in result) {
                    val data = document.toObject(Attendance::class.java)
                    attendance.add(data)
                }
                onComplete()
            }
            .addOnFailureListener {
                onComplete()
            }
    }

    fun addAttendance(attendance: Attendance) {
        db.collection("Attendance")
            .add(attendance)
            .addOnSuccessListener { documentRef ->
                Log.d("Firestore", "DocumentSnapshot written with ID: ${documentRef.id}")
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error adding document", e)
            }
    }

    fun clockOut(
        employeeID: String,
        onSuccess: () -> Unit = {},
        onError: (Exception) -> Unit = {}
    ) {
        db.collection("Attendance")
            .whereEqualTo("employeeID", employeeID)
            .whereEqualTo("clockOutTime", null)
            .orderBy("clockInTime", Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .addOnSuccessListener { result ->
                if (!result.isEmpty) {
                    val document = result.documents.first()
                    db.collection("Attendance").document(document.id)
                        .update(
                            mapOf(
                                "clockOutTime" to Timestamp.now(),
                                "status" to "OUT"
                            )
                        )
                        .addOnSuccessListener { onSuccess() }
                        .addOnFailureListener { onError(it) }
                } else {
                    onError(Exception("No active clock-in record found."))
                }
            }
            .addOnFailureListener { onError(it) }
    }


}
