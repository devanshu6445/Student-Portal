package com.college.portal.studentportal.ui.group_message

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.college.portal.studentportal.ui.dashboard.DashboardViewModel
import java.lang.IllegalStateException

class GroupMessageViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(GroupMessageViewModel::class.java)){
            return GroupMessageViewModel() as T
        }
        else{
            throw IllegalStateException("Unknown ViewModel class")
        }
    }
}