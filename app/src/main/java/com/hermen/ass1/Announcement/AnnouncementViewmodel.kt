package com.hermen.ass1.Announcement

import android.util.Log
import android.net.Uri
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.hermen.ass1.User.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class AnnouncementViewModel : ViewModel() {
    private val _announcement = MutableLiveData<Announcement?>(null)
    val announcement: LiveData<Announcement?> get() = _announcement

    val title = mutableStateOf("")
    val content = mutableStateOf("")
    val imageUrl = mutableStateOf<String?>(null)
    val saveSuccessful = mutableStateOf(false)

    private val _announcements = MutableStateFlow<List<Announcement>>(emptyList())
    val announcements: StateFlow<List<Announcement>> = _announcements
    val collectionRef = Firebase.firestore.collection("Announcement")

    init {
        loadAnnouncements()
    }

    fun loadAnnouncements() {
        viewModelScope.launch {
            val list = AnnouncementRepository.getAnnouncements()
            Log.d("DEBUG", "Fetched Announcements: $list")  // Log fetched data
            _announcements.value = list
        }
    }

    fun loadAnnouncement(id: String) {
        viewModelScope.launch {
            Log.d("AnnouncementViewModel", "Fetching announcement with ID: $id")
            val data = AnnouncementRepository.getAnnouncementById(id)
            if (data != null) {
                Log.d("AnnouncementViewModel", "Announcement loaded: Title = ${data.title}, Content = ${data.content}")
                title.value = data.title
                content.value = data.content
            } else {
                Log.d("AnnouncementViewModel", "No announcement found with ID: $id")
            }
        }
    }

    fun createAnnouncementWithCustomId() {
        viewModelScope.launch {
            try {
                generateNextDocId { newId ->
                    val db = FirebaseFirestore.getInstance()
                    val ref = db.collection("Announcement")

                    // Prepare the new announcement data
                    val newAnnouncement = hashMapOf(
                        "title" to title.value,
                        "content" to content.value,
                        "employeeID" to SessionManager.currentUser?.id,
                        "created_at" to SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(Date()),
                        "imageUrl" to imageUrl.value
                    )

                    // Save the new announcement with the generated ID
                    ref.document(newId).set(newAnnouncement)
                        .addOnSuccessListener {
                            Log.d("CreateAnnouncement", "Announcement created successfully: $newId")
                            saveSuccessful.value = true
                        }
                        .addOnFailureListener { e ->
                            Log.e("CreateAnnouncement", "Error creating announcement: ${e.message}", e)
                        }
                }
            } catch (e: Exception) {
                Log.e("CreateAnnouncement", "Error creating announcement: ${e.message}", e)
            }
        }
    }

    private fun generateNextDocId(onIdGenerated: (String) -> Unit) {
        FirebaseFirestore.getInstance().collection("Announcement")
            .get()
            .addOnSuccessListener { documents ->
                val lastId = documents.mapNotNull {
                    val id = it.id
                    if (id.startsWith("a")) {
                        id.substring(1).toIntOrNull()  // Fix the substring to start from index 1
                    } else null
                }.maxOrNull() ?: 0

                // Generate the next ID and pad it with 2 digits (e.g., a01, a02, ...)
                val nextId = "a" + (lastId + 1).toString().padStart(2, '0')
                onIdGenerated(nextId)
            }
            .addOnFailureListener {
                onIdGenerated("a01")  // Fallback in case of error
            }
    }

    fun updateAnnouncement(id: String, imageUri: Uri?) {
        viewModelScope.launch {
            try {
                var imageUrlToSave = imageUrl.value ?: ""

                if (imageUri != null) {
                    imageUrlToSave = uploadImageAndGetUrl(imageUri)
                    imageUrl.value = imageUrlToSave
                }

                val dataToSave = mapOf(
                    "title" to title.value,
                    "content" to content.value,
                    "employeeID" to SessionManager.currentUser?.id,
                    "created_at" to SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(Date()),
                    "imageUrl" to imageUrlToSave
                )

                collectionRef.document(id).set(dataToSave)
                    .addOnSuccessListener {
                        Log.d("SAVE_ANNOUNCEMENT", "Updated announcement with ID: $id")
                        saveSuccessful.value = true
                    }
                    .addOnFailureListener { e ->
                        Log.e("SAVE_ANNOUNCEMENT", "Error updating announcement", e)
                    }
            } catch (e: Exception) {
                Log.e("SAVE_ANNOUNCEMENT", "Error updating announcement: ${e.message}", e)
            }
        }
    }

    suspend fun uploadImageAndGetUrl(uri: Uri): String {
        val storageRef = FirebaseStorage.getInstance().reference
        val announcementId = UUID.randomUUID().toString()
        val imageRef = storageRef.child("announcement/$announcementId.jpg")

        imageRef.putFile(uri).await()
        return imageRef.downloadUrl.await().toString()
    }

}
