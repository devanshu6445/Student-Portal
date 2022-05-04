package com.college.portal.studentportal.ui.notifications.changePassword

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.college.portal.studentportal.callback.FirebaseGeneralCallback
import com.college.portal.studentportal.data.model.ChangePassword
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

class ChangePasswordViewModel : ViewModel(),FirebaseGeneralCallback {
    // TODO: Implement the ViewModel

    private val changePasswordRepository = ChangePasswordRepository(this)
    private val _passwordChangeStatus = MutableLiveData(0)
    val passwordChangeStatus: LiveData<Int> = _passwordChangeStatus
    fun changePassword(changePassword: ChangePassword){
        _passwordChangeStatus.value = 1
        viewModelScope.launch(Dispatchers.IO) {
            changePasswordRepository.reAuthenticateUser(changePassword)
        }
    }

    override fun onSuccessful() {
        _passwordChangeStatus.value = 2
    }

    override fun onFailure(error: Exception) {
        _passwordChangeStatus.value = 3
    }
}