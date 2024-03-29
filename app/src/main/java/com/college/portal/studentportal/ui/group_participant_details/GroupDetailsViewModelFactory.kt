package com.college.portal.studentportal.ui.group_participant_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.college.portal.studentportal.roomDatabase.groupEverything.GroupMessageDatabase
import com.college.portal.studentportal.roomDatabase.groups.BasicGroupData
import java.lang.IllegalStateException

class GroupDetailsViewModelFactory(private val database: GroupMessageDatabase,private val groupData: BasicGroupData): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GroupDetailsViewModel::class.java)){
            return GroupDetailsViewModel(database,groupData) as T
        } else{
            throw IllegalStateException("Unknown class")
        }
    }
}