package com.college.portal.studentportal.ui.dashboard

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.college.portal.studentportal.roomDatabase.groups.GroupDatabase
import com.college.portal.studentportal.roomDatabase.user.CurrentUserDatabase
import java.lang.IllegalStateException

class DashboardViewModelFactory(

    private val currentUserDatabase: CurrentUserDatabase,
    private val groupDatabase: GroupDatabase

    ): ViewModelProvider.Factory {


    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(DashboardViewModel::class.java)){
            return DashboardViewModel(currentUserDatabase,groupDatabase) as T
        }
        else{
            throw IllegalStateException("Unknown ViewModel class")
        }

    }
}