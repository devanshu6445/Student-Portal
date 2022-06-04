package com.college.portal.studentportal.ui.notifications.attendanceFragment

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.college.portal.studentportal.callback.FirebaseGeneralCallback
import com.college.portal.studentportal.data.model.LoggedInUser
import com.college.portal.studentportal.roomDatabase.user.CurrentUserDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception

class StudentForAttendanceViewModel(
    subjectSemester: String,
    course: String,
    section: String,
    studentDatabase: CurrentUserDatabase
) : ViewModel(),FirebaseGeneralCallback {

    private val attendanceRepository = AttendanceRepository(studentDatabase)
    private val _studentList = MutableLiveData<List<LoggedInUser>>()
    private val _uploadStatus = MutableLiveData(0)
    val uploadStatus:LiveData<Int> = _uploadStatus
    val studentList: LiveData<List<LoggedInUser>> = _studentList

    companion object {
        private const val TAG = "StudentForAttendanceVie"
    }

    init {
        viewModelScope.launch {
            attendanceRepository.getStudentForTheSubject(subjectSemester, course, section).collect {
                withContext(Dispatchers.Main) {
                    _studentList.value = it
                }
            }
        }
        attendanceRepository.getStudentForTheSubjectNetwork()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun submit(presentStudentList: List<LoggedInUser>, subjectCode: String) {
        _uploadStatus.value = 1
        attendanceRepository.submitAttendance(presentStudentList, subjectCode,this)
    }


    override fun onSuccessful() {
        _uploadStatus.value = 2
    }

    override fun onFailure(error: Exception) {
        _uploadStatus.value = 3
    }
}