package com.college.portal.studentportal.ui.dashboard

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.IllegalStateException

class DashboardViewModelFactory(val sharedPreferences: SharedPreferences?): ViewModelProvider.Factory {


    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(DashboardViewModel::class.java)){
            return DashboardViewModel(sharedPreferences) as T
        }
        else{
            throw IllegalStateException("Unknown ViewModel class")
        }

    }
}