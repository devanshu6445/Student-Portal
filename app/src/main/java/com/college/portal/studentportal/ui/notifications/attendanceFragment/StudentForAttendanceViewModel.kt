package com.college.portal.studentportal.ui.notifications.attendanceFragment

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.college.portal.studentportal.data.model.LoggedInUser
import com.college.portal.studentportal.roomDatabase.user.CurrentUserDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception

class StudentForAttendanceViewModel(subjectSemester:String,course:String,studentDatabase: CurrentUserDatabase) : ViewModel(),FirebaseAttendanceCallback<LoggedInUser> {

    private val attendanceRepository = AttendanceRepository(studentDatabase)
    private val  _studentList = MutableLiveData<List<LoggedInUser>>()
    val studentList:LiveData<List<LoggedInUser>> = _studentList

    companion object{
        private const val TAG = "StudentForAttendanceVie"
    }
    init {
        viewModelScope.launch {
            attendanceRepository.getStudentForTheSubject(subjectSemester,course).collect {
                withContext(Dispatchers.Main){
                    _studentList.value = it
                }
            }
        }
        attendanceRepository.getStudentForTheSubjectNetwork()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun submit(absentStudentList:List<LoggedInUser>,subjectCode:String){
        attendanceRepository.submitAttendance(absentStudentList,subjectCode)
    }

    override fun onSuccessful(list: List<LoggedInUser>) {
        _studentList.value = list
    }

    override fun onFailure(error: Exception) {
        Log.e(TAG, "onFailure: ${error.message}", error)
    }
}