package com.college.portal.studentportal.ui.notifications.myAttendance

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.college.portal.studentportal.callback.FirebaseGeneralCallback
import com.college.portal.studentportal.data.model.LoggedInUser
import com.college.portal.studentportal.data.model.Subject
import com.college.portal.studentportal.ui.notifications.attendanceFragment.FirebaseAttendanceCallback
import java.lang.Exception
import kotlin.IllegalStateException
import kotlin.math.log

class SelectSubjectViewModel(private val loggedInUser: LoggedInUser) : ViewModel(),FirebaseAttendanceCallback<Subject>{

    private val selectSubjectRepository = SelectSubjectRepository(loggedInUser)
    private val _subjectList = MutableLiveData<List<Subject>>()
    val subjectList:LiveData<List<Subject>> = _subjectList

    companion object{
        private const val TAG = "SelectSubjectViewModel"
    }
    init {
        selectSubjectRepository.getSubjectListForStudent(this)
    }
    override fun onSuccessful(list: List<Subject>) {
        _subjectList.value = list
    }

    override fun onFailure(error: Exception) {
        Log.e(TAG, "onFailure: ", error)
    }

}

class SelectSubjectViewModelFactory(private val loggedInUser: LoggedInUser): ViewModelProvider.Factory{

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(SelectSubjectViewModel::class.java)){
            return SelectSubjectViewModel(loggedInUser) as T
        }else
            throw IllegalStateException("Unknown class")
    }

}