package com.college.portal.studentportal.ui.notifications.announcement

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.college.portal.studentportal.roomDatabase.announcement.AnnouncementDatabase

class PostAnnouncementViewModelFactory(private val database: AnnouncementDatabase): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(PostAnnouncementViewModel::class.java)){
            return PostAnnouncementViewModel(database) as T
        }else{
            throw IllegalStateException("Unknown class")
        }
    }
}