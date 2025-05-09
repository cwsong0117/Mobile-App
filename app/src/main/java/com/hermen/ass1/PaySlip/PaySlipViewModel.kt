package com.hermen.ass1.PaySlip

import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.privacysandbox.ads.adservices.adid.AdId
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PaySlipViewModel: ViewModel() {
    private val _requestList = MutableStateFlow<List<PaySlipResource>>(emptyList())
    val requestList: StateFlow<List<PaySlipResource>> = _requestList
    private val db = Firebase.firestore
    var year by mutableStateOf("")
    var month by mutableStateOf("")
    var allowance by mutableStateOf("")
    var bonus by mutableStateOf("")
    var overtimePay by mutableStateOf("")
    var incomeTax by mutableStateOf("")
    var unpaidLeave by mutableStateOf("")

    init {
        fetchRequestList()
    }

    fun fetchRequestList() {
        db.collection("PaySlip")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("Firebase", "Error fetching data", error)
                    return@addSnapshotListener
                }
                val requests = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        val userId = doc.getString("userId") ?: return@mapNotNull null
                        PaySlipResource(
                            userId = userId,
                            month = doc.getString("month") ?: "",
                            year = doc.getString("year") ?: "",
                            basicSalary = doc.getDouble("basicSalary") ?: 0.00,
                            allowance = doc.getDouble("allowance") ?: 0.0,
                            bonus = doc.getDouble("bonus") ?: 0.0,
                            overtimePay = doc.getDouble("overtimePay") ?: 0.0,
                            incomeTax = doc.getDouble("incomeTax") ?: 0.0,
                            unpaidLeave = doc.getDouble("unpaidLeave") ?: 0.0,
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

    var isSubmitting by mutableStateOf(false)

    fun submitPaySlip(
        month: String,
        year: String,
        basicSalary: Double,
        allowance: Double,
        bonus: Double,
        overtimePay: Double,
        incomeTax: Double,
        unpaidLeave: Double,
        employeeId: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        isSubmitting = true

        // First handle the BasicSalary record
        handleBasicSalary(employeeId = employeeId,
            onSuccess = {
                // Then handle the PaySlip record
                handlePaySlipRecord(
                    month = month,
                    year = year,
                    basicSalary = basicSalary,
                    allowance = allowance,
                    bonus = bonus,
                    overtimePay = overtimePay,
                    incomeTax = incomeTax,
                    unpaidLeave = unpaidLeave,
                    employeeId = employeeId,
                    onSuccess = onSuccess,
                    onError = onError
                )
            },
            onError = onError
        )
    }

    private fun handleBasicSalary(
        employeeId: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        val basicSalaryValue = basicSalary.toDoubleOrNull() ?: 0.0

        // Check if basic salary record exists
        db.collection("BasicSalary")
            .whereEqualTo("userId", employeeId)
            .limit(1)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    // Update existing basic salary
                    val document = querySnapshot.documents[0]
                    updateBasicSalary(
                        documentId = document.id,
                        basicSalary = basicSalaryValue,
                        onSuccess = onSuccess,
                        onError = onError
                    )
                } else {
                    // Create new basic salary
                    createBasicSalary(
                        employeeId = employeeId,
                        basicSalary = basicSalaryValue,
                        onSuccess = onSuccess,
                        onError = onError
                    )
                }
            }
            .addOnFailureListener { e ->
                isSubmitting = false
                onError(e)
            }
    }

    private fun updateBasicSalary(
        documentId: String,
        basicSalary: Double,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        db.collection("BasicSalary")
            .document(documentId)
            .update(mapOf(
                "basicSalary" to basicSalary
            ))
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e ->
                isSubmitting = false
                onError(e)
            }
    }

    private fun createBasicSalary(
        employeeId: String,
        basicSalary: Double,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        generateBasicSalaryId { salaryId ->
            val basicSalaryData = hashMapOf(
                "userId" to employeeId,
                "basicSalary" to basicSalary,
                "salaryId" to salaryId
            )

            db.collection("BasicSalary")
                .document(salaryId)
                .set(basicSalaryData)
                .addOnSuccessListener { onSuccess() }
                .addOnFailureListener { e ->
                    isSubmitting = false
                    onError(e)
                }
        }
    }

    private fun generateBasicSalaryId(onIdGenerated: (String) -> Unit) {
        db.collection("BasicSalary")
            .orderBy("salaryId", Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .addOnSuccessListener { documents ->
                val lastId = documents.firstOrNull()?.getString("salaryId")
                val nextId = if (lastId != null && lastId.startsWith("BS")) {
                    val num = lastId.substring(2).toInt() + 1
                    "BS" + num.toString().padStart(3, '0')
                } else {
                    "BS001"
                }
                onIdGenerated(nextId)
            }
            .addOnFailureListener {
                onIdGenerated("BS001")
            }
    }

    private fun handlePaySlipRecord(
        month: String,
        year: String,
        basicSalary: Double,
        allowance: Double,
        bonus: Double,
        overtimePay: Double,
        incomeTax: Double,
        unpaidLeave: Double,
        employeeId: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        // Check if PaySlip record exists
        db.collection("PaySlip")
            .whereEqualTo("userId", employeeId)
            .whereEqualTo("month", month)
            .whereEqualTo("year", year)
            .limit(1)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    // Update existing PaySlip
                    val document = querySnapshot.documents[0]
                    updateExistingPayslip(
                        documentId = document.id,
                        basicSalary = basicSalary,
                        allowance = allowance,
                        bonus = bonus,
                        overtimePay = overtimePay,
                        incomeTax = incomeTax,
                        unpaidLeave = unpaidLeave,
                        onSuccess = onSuccess,
                        onError = onError
                    )
                } else {
                    // Create new PaySlip
                    createNewPayslip(
                        month = month,
                        year = year,
                        basicSalary = basicSalary,
                        allowance = allowance,
                        bonus = bonus,
                        overtimePay = overtimePay,
                        incomeTax = incomeTax,
                        unpaidLeave = unpaidLeave,
                        employeeId = employeeId,
                        onSuccess = onSuccess,
                        onError = onError
                    )
                }
            }
            .addOnFailureListener { e ->
                isSubmitting = false
                onError(e)
            }
    }

    private fun updateExistingPayslip(
        documentId: String,
        basicSalary: Double,
        allowance: Double,
        bonus: Double,
        overtimePay: Double,
        incomeTax: Double,
        unpaidLeave: Double,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        val updates = hashMapOf<String, Any>(
            "basicSalary" to basicSalary,
            "allowance" to allowance,
            "bonus" to bonus,
            "overtimePay" to overtimePay,
            "incomeTax" to incomeTax,
            "unpaidLeave" to unpaidLeave,
        )

        db.collection("PaySlip")
            .document(documentId)
            .update(updates)
            .addOnSuccessListener {
                isSubmitting = false
                onSuccess()
            }
            .addOnFailureListener { e ->
                isSubmitting = false
                onError(e)
            }
    }

    private fun createNewPayslip(
        month: String,
        year: String,
        basicSalary: Double,
        allowance: Double,
        bonus: Double,
        overtimePay: Double,
        incomeTax: Double,
        unpaidLeave: Double,
        employeeId: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        generateNextDocId { docId ->
            generateNextApplyId { newId ->
                val payslip = hashMapOf(
                    "payslipId" to newId,
                    "month" to month,
                    "year" to year,
                    "basicSalary" to basicSalary,
                    "allowance" to allowance,
                    "bonus" to bonus,
                    "overtimePay" to overtimePay,
                    "incomeTax" to incomeTax,
                    "unpaidLeave" to unpaidLeave,
                    "userId" to employeeId,
                )

                db.collection("PaySlip")
                    .document(docId)
                    .set(payslip)
                    .addOnSuccessListener {
                        isSubmitting = false
                        onSuccess()
                    }
                    .addOnFailureListener { e ->
                        isSubmitting = false
                        onError(e)
                    }
            }
        }
    }

    private fun generateNextDocId(onIdGenerated: (String) -> Unit) {
        db.collection("PaySlip")
            .get()
            .addOnSuccessListener { documents ->
                val lastId = documents.mapNotNull {
                    val id = it.id
                    if (id.startsWith("P")) {
                        id.substring(1).toIntOrNull()
                    } else null
                }.maxOrNull() ?: 0

                val nextId = "P" + (lastId + 1).toString().padStart(3, '0')
                onIdGenerated(nextId)
            }
            .addOnFailureListener {
                onIdGenerated("P001")
            }
    }

    private fun generateNextApplyId(onIdGenerated: (String) -> Unit) {
        db.collection("PaySlip")
            .orderBy("payslipId", Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .addOnSuccessListener { document ->
                val lastId = document.firstOrNull()?.getString("payslipId")
                val nextId = if (lastId != null && lastId.startsWith("PS")) {
                    val num = lastId.substring(2).toInt() + 1
                    "PS" + num.toString().padStart(3, '0')
                } else {
                    "PS001"
                }
                onIdGenerated(nextId)
            }
            .addOnFailureListener {
                onIdGenerated("PS001")
            }
    }

    var basicSalary by mutableStateOf("")

    private var lastLoadedUserId: String? = null
    private var lastLoadedMonth: String? = null
    private var lastLoadedYear: String? = null
    var existingDocId by mutableStateOf<String?>(null)

    fun fetchRecordByMonth(userId: String) {
        if (
            lastLoadedUserId == userId &&
            lastLoadedMonth == month &&
            lastLoadedYear == year
        ) return

        db.collection("PaySlip")
            .whereEqualTo("year", year)
            .whereEqualTo("month", month)
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->

                if (error != null) {
                    Log.e("Firestore", "Fetch error", error)
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    snapshot.documents.forEach { doc ->
                        updateFieldsFromDocument(userId, doc)
                    }
                } else {
                    resetFieldsToDefault()
                }
                lastLoadedUserId = userId
                lastLoadedMonth = month
                lastLoadedYear = year
            }
    }


    private fun updateFieldsFromDocument(userId: String, document: DocumentSnapshot) {
        try {
            existingDocId = document.id
            basicSalary = (document.getDouble("basicSalary") ?: 0.0).toString()
            allowance = (document.getDouble("allowance") ?: 0.0).toString()
            bonus = (document.getDouble("bonus") ?: 0.0).toString()
            overtimePay = (document.getDouble("overtimePay") ?: 0.0).toString()
            incomeTax = (document.getDouble("incomeTax") ?: 0.0).toString()
            unpaidLeave = (document.getDouble("unpaidLeave") ?: 0.0).toString()

            // Log to confirm the fields are updated correctly
            Log.d("ViewModel", "Updated fields for $userId: allowance=$allowance, " +
                    "bonus=$bonus, overtimePay=$overtimePay, incomeTax=$incomeTax, " +
                    "unpaidLeave=$unpaidLeave, basicSalary=$basicSalary")
        } catch (e: Exception) {
            Log.e("ViewModel", "Error updating fields", e)
        }
    }

    private fun resetFieldsToDefault() {
        basicSalary = "0.0"
        allowance = "0.0"
        bonus = "0.0"
        overtimePay = "0.0"
        incomeTax = "0.0"
        unpaidLeave = "0.0"
        Log.d("ViewModel", "Reset all fields to default values")
    }

    // ViewModel
    private val _isRecordExists = MutableStateFlow(false)
    val isRecordExistsFlow: StateFlow<Boolean> = _isRecordExists

    // Firestore check function
    fun checkForExistingRecord(userId: String, year: String, month: String) {
        // Reset the state before querying
        _isRecordExists.value = false

        db.collection("PaySlip")
            .whereEqualTo("year", year)
            .whereEqualTo("month", month)
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { documents ->
                _isRecordExists.value = !documents.isEmpty()
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error checking for record", e)
                _isRecordExists.value = false
            }
    }

    fun deletePaySlip(
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        existingDocId?.let { docId ->
            db.collection("PaySlip")
                .document(docId)
                .delete()
                .addOnSuccessListener {
                    Log.d("Firebase", "PaySlip document $docId deleted successfully")
                    resetFieldsToDefault()
                    existingDocId = null
                    onSuccess()
                }
                .addOnFailureListener { e ->
                    Log.e("Firebase", "Error deleting document", e)
                    onFailure(e)
                }
        } ?: run {
            val e = Exception("No existing document ID found.")
            Log.w("Firebase", e.message ?: "Unknown error")
            onFailure(e)
        }
    }

}