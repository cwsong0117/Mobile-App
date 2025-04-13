package com.hermen.ass1.Announcement

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AnnouncementViewModel : ViewModel() {
    private val _announcements = MutableStateFlow<List<Announcement>>(emptyList())
    val announcements: StateFlow<List<Announcement>> = _announcements

    init {
        fetchAnnouncements()
    }

    private fun fetchAnnouncements() {
        viewModelScope.launch {
            val result = AnnouncementRepository.getAnnouncements()
            _announcements.value = result
        }
    }
}
