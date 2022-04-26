package com.college.portal.studentportal.ui.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.college.portal.studentportal.roomDatabase.user.CurrentUserDatabase
import java.lang.IllegalStateException

class NotificationViewModelFactory(private val database: CurrentUserDatabase): ViewModelProvider.Factory {
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(NotificationsViewModel::class.java)){
            return NotificationsViewModel(database) as T
        }else
            throw IllegalStateException("Unknown class")
    }
}