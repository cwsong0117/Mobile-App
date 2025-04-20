package com.hermen.ass1.User

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class UserProfileViewModel : ViewModel() {
    var name by mutableStateOf("")
    var age by mutableStateOf("")
    var birthday by mutableStateOf("")
    var position by mutableStateOf("")
    var department by mutableStateOf("")
    var contactNo by mutableStateOf("")
    var email by mutableStateOf("")
    var imageUrl by mutableStateOf("")

    var nameErrorMessage by mutableStateOf<String?>(null)
    var ageErrorMessage by mutableStateOf<String?>(null)
    var positionErrorMessage by mutableStateOf<String?>(null)
    var departmentErrorMessage by mutableStateOf<String?>(null)
    var contactNoErrorMessage by mutableStateOf<String?>(null)
    var emailErrorMessage by mutableStateOf<String?>(null)

    var hasChanges by mutableStateOf(false)
        private set

    private lateinit var originalUser: User

    fun initializeUserData(user: User) {
        // Initialize only if the data is not already set
        if (this::originalUser.isInitialized.not()) {
            originalUser = user.copy() // Save a snapshot for comparison

            name = user.name
            age = user.age.toString()
            position = user.position
            department = user.department
            contactNo = user.contactNo
            birthday = user.birthday
            email = user.email
            imageUrl = user.imageUrl ?: ""

            hasChanges = false
        }
    }

    fun checkForChanges() {
        hasChanges = name != originalUser.name ||
                age != originalUser.age.toString() ||
                position != originalUser.position ||
                department != originalUser.department ||
                contactNo != originalUser.contactNo ||
                email != originalUser.email ||
                imageUrl != (originalUser.imageUrl ?: "")
    }

    private fun formatContactNumber(input: String): String {
        return when {
            input.length == 10 -> {
                "${input.substring(0, 3)}-${input.substring(3, 6)} ${input.substring(6)}"
            }
            input.length == 11 -> {
                "${input.substring(0, 3)}-${input.substring(3, 7)} ${input.substring(7)}"
            }
            else -> input // Return as is if the length is unexpected
        }
    }

    fun validateFields(): Boolean {
        var isValid = true

        if (name.isBlank()) {
            nameErrorMessage = "Name cannot be empty."
            isValid = false
        } else nameErrorMessage = null

        if (age.isBlank()) {
            ageErrorMessage = "Age cannot be empty."
            isValid = false
        } else if (age.any { it.isLetter() }) {
            ageErrorMessage = "Age should be numeric."
            isValid = false
        } else ageErrorMessage = null

        if (position.isBlank()) {
            positionErrorMessage = "Position cannot be empty."
            isValid = false
        } else positionErrorMessage = null

        if (department.isBlank()) {
            departmentErrorMessage = "Department cannot be empty."
            isValid = false
        } else departmentErrorMessage = null

        val rawContact = contactNo.replace(Regex("[^0-9]"), "")
        if (rawContact.length !in 10..11) {
            contactNoErrorMessage = "Contact number must be 10–11 digits."
            isValid = false
        } else contactNoErrorMessage = null

        if (email.isBlank()) {
            emailErrorMessage = "Email cannot be empty."
            isValid = false
        } else emailErrorMessage = null

        return isValid
    }

    fun onAgeChanged(newValue: String) {
        age = newValue
        ageErrorMessage = if (newValue.any { it.isLetter() }) "Age should be numeric." else null
        checkForChanges()
    }

    fun onContactNoChanged(newValue: String) {
        val cleanedValue = newValue.replace(Regex("[^0-9]"), "")
        contactNo = formatContactNumber(cleanedValue)
        contactNoErrorMessage = if (cleanedValue.length !in 10..11)
            "Contact number must be 10 or 11 digits." else null
        checkForChanges()
    }

    fun saveUserProfile() {
        if (!validateFields()) return

        val updatedUser = originalUser.copy(
            name = name,
            age = age.toInt(),
            position = position,
            department = department,
            contactNo = contactNo,
            email = email,
            imageUrl = imageUrl
        )

        val userRef = Firebase.firestore.collection("User").document(updatedUser.id)
        userRef.set(updatedUser)
            .addOnSuccessListener {
                SessionManager.currentUser = updatedUser
                originalUser = updatedUser
                hasChanges = false
                Log.d("UserProfileViewModel", "User profile updated successfully.")
            }
            .addOnFailureListener { e ->
                Log.e("UserProfileViewModel", "Failed to update user profile: ${e.message}")
            }
    }

    suspend fun uploadImageAndGetUrl(uri: Uri): String {
        val storageRef = FirebaseStorage.getInstance().reference
        val currentUid = FirebaseAuth.getInstance().currentUser?.uid
        Log.d("UID_CHECK", "Current UID = $currentUid")

        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: throw Exception("No UID")
        val imageRef = storageRef.child("images/$uid.jpg")

        // Upload the image
        imageRef.putFile(uri).await()

        // Get the download URL
        return imageRef.downloadUrl.await().toString()
    }

    fun markChangesMade() {
        hasChanges = true
    }
}
