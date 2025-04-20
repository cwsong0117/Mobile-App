package com.hermen.ass1.Announcement

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AnnouncementViewModel : ViewModel() {
    private val _announcement = MutableLiveData<Announcement?>(null)
    val announcement: LiveData<Announcement?> get() = _announcement

    // State for form inputs
    val title = MutableLiveData("")
    val content = MutableLiveData("")
    val imageUri = MutableLiveData<Uri?>(null)

    private val _announcements = MutableStateFlow<List<Announcement>>(emptyList())
    val announcements: StateFlow<List<Announcement>> = _announcements

    init {
        loadAnnouncements()
    }

    private fun loadAnnouncements() {
        viewModelScope.launch {
            val list = AnnouncementRepository.getAnnouncements()
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

    // Function to save a new or updated announcement
    fun saveAnnouncement(isEditing: Boolean, id: String? = null) {
        val newAnnouncement = Announcement(
            title = title.value ?: "",
            content = content.value ?: "",
            employeeID = "example_employee_id",  // Replace with actual user ID
            created_at = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(Date())
        )

        viewModelScope.launch {
            if (isEditing && id != null) {
                // Update announcement
                AnnouncementRepository.updateAnnouncement(id, newAnnouncement)
            } else {
                // Create new announcement
                AnnouncementRepository.createAnnouncement(newAnnouncement)
            }
        }
    }
}
