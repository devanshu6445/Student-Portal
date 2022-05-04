package com.college.portal.studentportal.ui.notifications.attendanceFragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.college.portal.studentportal.data.model.Subject
import java.lang.Exception

class AttendanceViewModel : ViewModel(),FirebaseAttendanceCallback<Subject> {
    // TODO: Implement the ViewModel

    private val _subjectList = MutableLiveData<List<Subject>>()
    private val attendanceRepository = AttendanceRepository()
    val subjectList:LiveData<List<Subject>> = _subjectList
    init {
        attendanceRepository.getSubjectList(this)
    }

    override fun onSuccessful(list: List<Subject>) {
        _subjectList.value = list
    }

    override fun onFailure(error: Exception) {

    }
}

interface FirebaseAttendanceCallback<T> {

    fun onSuccessful(list: List<T>)
    fun onFailure(error:Exception)
}