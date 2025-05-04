package com.hermen.ass1.Attendance

import android.app.Application
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
import kotlinx.coroutines.tasks.await
import android.content.res.Configuration
import android.location.Geocoder
import android.location.Location
import android.os.Looper
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.IOException
import java.util.Date
import android.Manifest
import android.content.pm.PackageManager
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch


@Composable
fun isLandscape(): Boolean {
    val configuration = LocalConfiguration.current
    return configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
}

@Composable
fun isTablet(): Boolean {
    val configuration = LocalConfiguration.current
    return configuration.screenWidthDp >= 600
}

data class Attendance(
    val attendanceID: String = "",
    val clockOutTime: Timestamp? = null,
    val clockInTime: Timestamp? = null,
    val employeeID: String = "",
    val status: String = ""
)

data class EditableAttendanceState(
    val clockIn: Date,
    val clockOut: Date,
    val status: String
)

class AttendanceViewModel(application: Application) : AndroidViewModel(application) {
    private val db = Firebase.firestore
    val attendance = mutableStateListOf<Attendance>()

    //for orientation changes
    var showEarlyLeaveDialog by mutableStateOf(false)
    var showSuccessDialog by mutableStateOf(false)
    var showEditDialog by mutableStateOf(false)
    var showRemoveDialog by mutableStateOf(false)
    var selectedAttendance: Attendance? by mutableStateOf(null)
    var editableState: EditableAttendanceState? by mutableStateOf(null)

    //expand for history list
    var expandedHistoryId by mutableStateOf<String?>(null)
        private set

    fun toggleHistoryExpansion(attendanceId: String) {
        expandedHistoryId = if (expandedHistoryId == attendanceId) null else attendanceId
    }

    fun isHistoryExpanded(attendanceId: String): Boolean {
        return expandedHistoryId == attendanceId
    }

    //expand for admin
    val expandedCards = mutableStateMapOf<String, Boolean>()

    fun toggleExpanded(employeeID: String) {
        val current = expandedCards[employeeID] ?: false
        expandedCards[employeeID] = !current
    }

    fun isExpanded(employeeID: String): Boolean {
        return expandedCards[employeeID] ?: false
    }

    //especially for editing for surviving orientationc change

    fun setEditableState(attendance: Attendance) {
        editableState = EditableAttendanceState(
            clockIn = attendance.clockInTime?.toDate() ?: Date(),
            clockOut = attendance.clockOutTime?.toDate() ?: Date(),
            status = attendance.status ?: ""
        )
    }

    fun updateClockIn(date: Date) {
        editableState = editableState?.copy(clockIn = date)
    }

    fun updateClockOut(date: Date) {
        editableState = editableState?.copy(clockOut = date)
    }

    fun updateStatus(newStatus: String) {
        editableState = editableState?.copy(status = newStatus)
    }

    fun clearEditableState() {
        editableState = null
    }
    //especially for editing for surviving orientation change

    //for tablettttt
    var selectedHistoryAttendance by mutableStateOf<Attendance?>(null)

    fun selectAttendance(attendance: Attendance) {
        selectedHistoryAttendance = attendance
    }

    var selectedEmployeeId by mutableStateOf<String?>(null)

    fun selectEmployee(employeeID: String) {
        selectedEmployeeId = employeeID
    }
    //tablettttttttttttt


    //for fetching location
    private val context = application.applicationContext
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    private val _userLocation = MutableStateFlow<Location?>(null)
    val userLocation: StateFlow<Location?> = _userLocation.asStateFlow()

    private val _userAddress = MutableStateFlow("Fetching address...")
    val userAddress: StateFlow<String> = _userAddress.asStateFlow()

    fun fetchUserLocation() {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) return

        val request = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            1000L
        ).setWaitForAccurateLocation(true)
            .setMaxUpdates(1)
            .build()

        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val location = result.lastLocation ?: return
                _userLocation.value = location

                try {
                    val geocoder = Geocoder(context, Locale.getDefault())
                    val addresses = geocoder.getFromLocation(
                        location.latitude,
                        location.longitude,
                        1
                    )
                    _userAddress.value = addresses?.firstOrNull()?.getAddressLine(0)
                        ?: "Address not found"
                } catch (e: IOException) {
                    _userAddress.value = "Failed to fetch address"
                }

                fusedLocationClient.removeLocationUpdates(this)
            }
        }

        try {
            fusedLocationClient.requestLocationUpdates(
                request,
                callback,
                Looper.getMainLooper()
            )
        } catch (e: SecurityException) {
            // Permission might have been revoked suddenly
            e.printStackTrace()
        }
    }
    //for fetching location

    //for mainscreen attendance ui
    private val _lastClockIn = mutableStateOf("--:--")
    val lastClockIn: State<String> get() = _lastClockIn

    private val _latestClockOut = mutableStateOf("--:--")
    val latestClockOut: State<String> get() = _latestClockOut

    fun loadTodayAttendance(employeeID: String) {
        viewModelScope.launch {
            val attendance = getLatestAttendanceToday(employeeID)
            val formatter = SimpleDateFormat("HH:mm", Locale.getDefault()).apply {
                timeZone = TimeZone.getTimeZone("Asia/Kuala_Lumpur")
            }

            _lastClockIn.value = attendance?.clockInTime?.toDate()?.let { formatter.format(it) } ?: "--:--"
            _latestClockOut.value = attendance?.clockOutTime?.toDate()?.let { formatter.format(it) } ?: "--:--"
        }
    }

    suspend fun getLatestAttendanceToday(employeeID: String): Attendance? {
        return try {
            val dbSnapshot = db.collection("Attendance")
                .whereEqualTo("employeeID", employeeID)
                .get()
                .await()

            val malaysiaTZ = TimeZone.getTimeZone("Asia/Kuala_Lumpur")
            val today = Calendar.getInstance(malaysiaTZ)

            dbSnapshot.documents
                .mapNotNull { it.toObject(Attendance::class.java) }
                .filter { attendance ->
                    attendance.clockInTime?.toDate()?.let { clockInDate ->
                        val clockInCalendar = Calendar.getInstance(malaysiaTZ).apply { time = clockInDate }
                        clockInCalendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                                clockInCalendar.get(Calendar.MONTH) == today.get(Calendar.MONTH) &&
                                clockInCalendar.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH)
                    } ?: false
                }
                .maxByOrNull { it.clockInTime?.toDate()?.time ?: 0L }  // get the latest one today
        } catch (e: Exception) {
            null
        }
    }

    //for mainscreen attendance ui

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

    suspend fun getLatestAttendanceForToday(employeeID: String): Attendance? {
        return try {
            val dbSnapshot = db.collection("Attendance")
                .whereEqualTo("employeeID", employeeID)
                .whereEqualTo("status", "Clocked In")
                .whereEqualTo("clockOutTime", null)
                .get()
                .await()

            val today = Calendar.getInstance(TimeZone.getTimeZone("Asia/Kuala_Lumpur"))

            dbSnapshot.documents
                .mapNotNull { it.toObject(Attendance::class.java) }
                .firstOrNull { attendance ->
                    attendance.clockInTime?.toDate()?.let { clockInDate ->
                        val clockInCalendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Kuala_Lumpur")).apply {
                            time = clockInDate
                        }
                        clockInCalendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                                clockInCalendar.get(Calendar.MONTH) == today.get(Calendar.MONTH) &&
                                clockInCalendar.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH)
                    } ?: false
                }
        } catch (e: Exception) {
            null
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

    fun deleteAttendance(attendanceID: String, onComplete: () -> Unit) {
        db.collection("Attendance")
            .whereEqualTo("attendanceID", attendanceID)
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (doc in querySnapshot) {
                    db.collection("Attendance").document(doc.id).delete()
                }
                onComplete()
            }
            .addOnFailureListener {
                Log.e("DeleteAttendance", "Failed to delete", it)
            }
    }

    fun editAttendance(attendanceID: String, updatedData: Map<String, Any>, onComplete: () -> Unit) {
        db.collection("Attendance")
            .whereEqualTo("attendanceID", attendanceID)
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (doc in querySnapshot) {
                    db.collection("Attendance").document(doc.id)
                        .update(updatedData)
                        .addOnSuccessListener { onComplete() }
                }
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
