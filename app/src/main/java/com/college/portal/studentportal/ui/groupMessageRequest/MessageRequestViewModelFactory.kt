package com.college.portal.studentportal.ui.groupMessageRequest

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.college.portal.studentportal.roomDatabase.groupEverything.GroupMessageDatabase
import com.college.portal.studentportal.roomDatabase.groups.BasicGroupData
import java.lang.IllegalStateException

class MessageRequestViewModelFactory(private val database: GroupMessageDatabase,private val basicGroupData: BasicGroupData): ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(MessageRequestViewModel::class.java)){
            return MessageRequestViewModel(database,basicGroupData) as T
        }else
            throw IllegalStateException("Unknown class")
    }
}