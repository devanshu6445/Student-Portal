package com.college.portal.studentportal.ui.notifications.leaveRequest

import android.util.Log
import androidx.lifecycle.*
import com.college.portal.studentportal.data.model.LeaveRequest
import com.college.portal.studentportal.data.model.LoggedInUser
import com.college.portal.studentportal.roomDatabase.leaveRequest.LeaveDatabase
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MyLeaveRequestsViewModel(database: LeaveDatabase, loggedInUser: LoggedInUser) : ViewModel(),LeaveRequestCallback {
    private val leaveRequestRepository = LeaveRequestRepository(database,this)
    private val _leaveRequestList = MutableLiveData<List<LeaveRequest>>()
    val leaveRequestList:LiveData<List<LeaveRequest>> = _leaveRequestList
    init {
        viewModelScope.launch {
            leaveRequestRepository.getAllLeaveRequests()?.collect {
                _leaveRequestList.value = it
            }
        }
        leaveRequestRepository.registerLeaveRequestListener(loggedInUser)
    }

    override fun onSuccess() {
        Log.d("LeaveReq", "onSuccess: ")
    }

    override fun onFailure() {
        Log.d("LeaveReq", "onFailure: ")
    }
}
class MyLeaveRequestsViewModelFactory(private val database:LeaveDatabase,private val loggedInUser: LoggedInUser): ViewModelProvider.Factory{
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(MyLeaveRequestsViewModel::class.java)){
            return MyLeaveRequestsViewModel(database,loggedInUser) as T
        }else{
            throw IllegalStateException("Unknown class")
        }
    }

}