package com.college.portal.studentportal.ui.group_message

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.college.portal.studentportal.roomDatabase.groupEverything.GroupMessageDatabase
import com.college.portal.studentportal.roomDatabase.groups.BasicGroupData
import com.college.portal.studentportal.roomDatabase.user.CurrentUserDatabase
import java.lang.IllegalStateException

class GroupMessageViewModelFactory(private val groupData: BasicGroupData,
                                   private val database: GroupMessageDatabase,
                                   private val currentUserDatabase: CurrentUserDatabase
                                   ) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(GroupMessageViewModel::class.java)){
            return GroupMessageViewModel(groupData = groupData,database ,currentUserDatabase) as T
        }
        else{
            throw IllegalStateException("Unknown ViewModel class")
        }
    }
}