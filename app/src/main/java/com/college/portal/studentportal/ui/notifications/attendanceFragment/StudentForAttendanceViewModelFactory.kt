package com.college.portal.studentportal.ui.notifications.attendanceFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.college.portal.studentportal.roomDatabase.user.CurrentUserDatabase
import java.lang.IllegalStateException

class StudentForAttendanceViewModelFactory(private val subjectSemester: String,
                                           private val course:String, private val studentDatabase: CurrentUserDatabase): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(StudentForAttendanceViewModel::class.java)){
            return StudentForAttendanceViewModel(subjectSemester,course,studentDatabase) as T
        }else{
            throw IllegalStateException("Unknown class")
        }
    }
}