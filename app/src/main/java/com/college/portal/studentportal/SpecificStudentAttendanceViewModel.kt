package com.college.portal.studentportal

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.college.portal.studentportal.data.model.LoggedInUser
import com.college.portal.studentportal.roomDatabase.user.CurrentUserDatabase
import com.college.portal.studentportal.ui.notifications.attendanceFragment.AttendanceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SpecificStudentAttendanceViewModel(
    subjectSemester: String,
    course: String,
    studentDatabase: CurrentUserDatabase
) : ViewModel() {

    private val attendanceRepository = AttendanceRepository(studentDatabase)
    private val _studentList = MutableLiveData<List<LoggedInUser>>()

    val studentList: LiveData<List<LoggedInUser>> = _studentList

    init {
        viewModelScope.launch {
            attendanceRepository.getStudentForShowingAttendance(subjectSemester,course)?.collect {
                withContext(Dispatchers.Main){
                    _studentList.value = it
                }
            }
        }
    }

}