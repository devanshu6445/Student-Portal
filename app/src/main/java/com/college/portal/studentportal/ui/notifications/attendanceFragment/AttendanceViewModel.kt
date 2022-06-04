package com.college.portal.studentportal.ui.notifications.attendanceFragment

import androidx.lifecycle.*
import com.college.portal.studentportal.data.model.Subject
import com.college.portal.studentportal.roomDatabase.subjectDatabase.SubjectDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception

class AttendanceViewModel(database: SubjectDatabase) : ViewModel(),FirebaseAttendanceCallback<Subject> {
    // TODO: Implement the ViewModel

    private val _subjectList = MutableLiveData<List<Subject>>()
    private val attendanceRepository = AttendanceRepository(database)
    val subjectList:LiveData<List<Subject>> = _subjectList
    init {
        viewModelScope.launch(Dispatchers.IO) {
            val list = attendanceRepository.getSubjectList()
            withContext(Dispatchers.Main){
                _subjectList.value = list?: mutableListOf()
            }
        }
        attendanceRepository.updateSubjectDatabase()
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

class AttendanceViewModelFactory(private val database: SubjectDatabase):ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(AttendanceViewModel::class.java)){
            return AttendanceViewModel(database) as T
        }else{
            throw IllegalStateException("unknown class")
        }
    }

}