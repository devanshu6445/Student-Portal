package com.college.portal.studentportal.ui.notifications.myAttendance

import androidx.lifecycle.*
import com.college.portal.studentportal.data.model.Subject
import com.college.portal.studentportal.roomDatabase.attendance.Attendance
import com.college.portal.studentportal.roomDatabase.attendance.AttendanceDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class MyAttendanceViewModel(private val database: AttendanceDatabase,private val subject: Subject,private val studentUid: String) : ViewModel() {

    private val myAttendanceRepository = MyAttendanceRepository(database)
    private val _attendanceList = MutableLiveData<List<Attendance>>()
    private val _monthPercentage = MutableLiveData<String>()
    val monthPercentage:LiveData<String> = _monthPercentage
    private val _overallPercentage = MutableLiveData<String>()
    val overallPercentage:LiveData<String> = _overallPercentage
    val attendanceList:LiveData<List<Attendance>> = _attendanceList

    init {
        myAttendanceRepository.registerListener(subject.subCode,studentUid)
        getAll()
        getMonthAttendancePercentage()
        getOverAllPercentage()
    }
    private fun getOverAllPercentage(){
        viewModelScope.launch {
            myAttendanceRepository.getOverAllPercentage().collect {
                withContext(Dispatchers.Main){
                    _overallPercentage.value = it
                }
            }

        }
    }
    fun getMonthAttendancePercentage(month: String = SimpleDateFormat("MMMM", Locale.UK).format(Date())){
        viewModelScope.launch {
            myAttendanceRepository.getMonthPercentage(month).collect {
                withContext(Dispatchers.Main){
                    _monthPercentage.value = it
                }
            }

        }
    }
    fun getAll(month: String = SimpleDateFormat("MMMM", Locale.UK).format(Date())){
        viewModelScope.launch {
            myAttendanceRepository.getPresents(month).collect {
                withContext(Dispatchers.Main){
                    _attendanceList.value = it
                }
            }
        }
    }
}

class MyAttendanceViewModelFactory(private val database: AttendanceDatabase,private val subject: Subject,private val studentUid :String): ViewModelProvider.Factory{
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(MyAttendanceViewModel::class.java))
            return MyAttendanceViewModel(database,subject,studentUid) as T
        else
            throw IllegalStateException("Unknown class")
    }

}