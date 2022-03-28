package com.college.portal.studentportal.ui.group_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.IllegalStateException

class GroupDetailsViewModelFactory: ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GroupDetailsViewModel::class.java)){
            return GroupDetailsViewModel() as T
        } else{
            throw IllegalStateException("Unknown class")
        }
    }
}