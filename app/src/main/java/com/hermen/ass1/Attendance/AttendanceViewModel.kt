package com.hermen.ass1.Attendance

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.Timestamp

data class Attendance(
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

}
