package com.college.portal.studentportal.ui.notifications.leaveRequest

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.college.portal.studentportal.data.model.LeaveRequest
import com.college.portal.studentportal.data.model.LoggedInUser

class SendLeaveRequestViewModel(private val loggedInUser: LoggedInUser) : ViewModel(),
    LeaveRequestCallback {

    private val leaveRequestRepository = LeaveRequestRepository(this)
    private val _sendStatus = MutableLiveData(0)
    val sendStatus: LiveData<Int> = _sendStatus

    fun sendRequest(reason: String, fromDate: String, toDate: String,days:Long) {
        _sendStatus.value = 1
        val leaveRequest = LeaveRequest(
            reason,
            loggedInUser.userName,
            loggedInUser.userCourse,
            loggedInUser.userSemester,
            loggedInUser.userUid,
            fromDate,
            toDate,
            System.currentTimeMillis()/1000
        )
        leaveRequest.duration = "${days+1} days"
        leaveRequestRepository.sendRequest(leaveRequest)
    }

    override fun onSuccess() {
        _sendStatus.value = 2
    }

    override fun onFailure() {
        _sendStatus.value = 3
    }
}

interface LeaveRequestCallback {
    fun onSuccess()
    fun onFailure()
}

class SendLeaveRequestViewModelFactory(private val loggedInUser: LoggedInUser) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SendLeaveRequestViewModel::class.java)) {
            return SendLeaveRequestViewModel(loggedInUser) as T
        } else
            throw IllegalStateException("Unknown class")
    }

}