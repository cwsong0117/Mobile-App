package com.hermen.ass1.Attendance

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import androidx.compose.runtime.State

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

    private val _latestClockIn = mutableStateOf<Timestamp?>(null)
    val latestClockIn: State<Timestamp?> get() = _latestClockIn

    fun fetchLatestClockIn(employeeID: String) {
        db.collection("Attendance")
            .whereEqualTo("employeeID", employeeID)
            .whereEqualTo("clockOutTime", null)
            .orderBy("clockInTime", Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .addOnSuccessListener { result ->
                val latest = result.documents.firstOrNull()?.getTimestamp("clockInTime")
                _latestClockIn.value = latest
            }
    }


    fun clockOut(
        employeeID: String,
        isEarlyLeave: Boolean = false,
        onSuccess: () -> Unit = {},
        onError: (Exception) -> Unit = {}
    ) {
        db.collection("Attendance")
            .whereEqualTo("employeeID", employeeID)
            .whereEqualTo("status", "Clocked In")
            .orderBy("clockInTime", Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .addOnSuccessListener { result ->
                if (!result.isEmpty) {
                    val document = result.documents.first()
                    val updates = mutableMapOf<String, Any>(
                        "clockOutTime" to Timestamp.now(),
                        "status" to if (isEarlyLeave) "Left Early" else "OUT"
                    )

                    db.collection("Attendance").document(document.id)
                        .update(updates)
                        .addOnSuccessListener { onSuccess() }
                        .addOnFailureListener { onError(it) }
                } else {
                    onError(Exception("No active clock-in record found."))
                }
            }
            .addOnFailureListener { onError(it) }
    }


    fun generateAttendanceID(onResult: (String) -> Unit) {
        val today = Calendar.getInstance(TimeZone.getTimeZone("Asia/Kuala_Lumpur"))
        val dateFormat = SimpleDateFormat("ddMMyy", Locale.getDefault())
        val dateStr = dateFormat.format(today.time)

        db.collection("Attendance")
            .whereGreaterThanOrEqualTo("clockInTime", getStartOfDayTimestamp(today))
            .whereLessThan("clockInTime", getEndOfDayTimestamp(today))
            .get()
            .addOnSuccessListener { result ->
                val count = result.size() + 1
                val attendanceID = "ATD$dateStr-${String.format("%03d", count)}"
                onResult(attendanceID)
            }
    }

}

private fun getStartOfDayTimestamp(calendar: Calendar): Timestamp {
    val start = calendar.clone() as Calendar
    start.set(Calendar.HOUR_OF_DAY, 0)
    start.set(Calendar.MINUTE, 0)
    start.set(Calendar.SECOND, 0)
    start.set(Calendar.MILLISECOND, 0)
    return Timestamp(start.time)
}

private fun getEndOfDayTimestamp(calendar: Calendar): Timestamp {
    val end = calendar.clone() as Calendar
    end.set(Calendar.HOUR_OF_DAY, 23)
    end.set(Calendar.MINUTE, 59)
    end.set(Calendar.SECOND, 59)
    end.set(Calendar.MILLISECOND, 999)
    return Timestamp(end.time)
}
