package com.college.portal.studentportal.ui.notifications.leaveRequest

import androidx.lifecycle.*
import com.college.portal.studentportal.data.model.LeaveRequest
import com.college.portal.studentportal.data.model.LoggedInUser
import com.college.portal.studentportal.roomDatabase.leaveRequest.LeaveDatabase
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class LeaveRequestApprovingViewModel(database: LeaveDatabase,loggedInUser: LoggedInUser) : ViewModel(),LeaveRequestCallback {

    private val leaveRequestRepository = LeaveRequestRepository(database,this)
    private val _leaveRequestList = MutableLiveData<List<LeaveRequest>>()
    val leaveRequestList:LiveData<List<LeaveRequest>> = _leaveRequestList

    init {
        leaveRequestRepository.updateLeaveDatabaseForHOD(loggedInUser.userCourse)
        viewModelScope.launch {
            leaveRequestRepository.getAllLeaveRequests()?.collect {
                _leaveRequestList.value = it
            }
        }
    }

    fun leaveRequestUpdate(leaveRequest: LeaveRequest){
        viewModelScope.launch {
            leaveRequestRepository.deleteLeaveRequest(leaveRequest)
        }
        leaveRequestRepository.updateLeaveRequestOnServer(leaveRequest)
    }


    override fun onSuccess() {

    }

    override fun onFailure() {

    }

}
class LeaveRequestApprovingViewModelFactory(private val database: LeaveDatabase,private val loggedInUser: LoggedInUser): ViewModelProvider.Factory{

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(LeaveRequestApprovingViewModel::class.java))
            return LeaveRequestApprovingViewModel(database,loggedInUser) as T
        else
            throw IllegalStateException("Unknown class")
    }

}