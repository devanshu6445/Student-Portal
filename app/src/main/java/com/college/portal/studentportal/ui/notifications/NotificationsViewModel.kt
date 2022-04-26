package com.college.portal.studentportal.ui.notifications

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.college.portal.studentportal.data.model.LoggedInUser
import com.college.portal.studentportal.roomDatabase.user.CurrentUserDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NotificationsViewModel(database: CurrentUserDatabase) : ViewModel() {

    private val profileRepo = ProfileRepository(database)
    private val _loggedInUser = MutableLiveData<LoggedInUser>()
    val loggedInUser: LiveData<LoggedInUser> = _loggedInUser
    private val _logoutProgress = MutableLiveData<Int>()
    val logoutProgress:LiveData<Int> = _logoutProgress
    init {
        viewModelScope.launch {
            val user = profileRepo.getProfile()
            withContext(Dispatchers.Main){
                _loggedInUser.value = user
            }
        }
    }

    fun logout() {
        _logoutProgress.value = 1
        viewModelScope.launch {
            val result = profileRepo.logout()
            withContext(Dispatchers.Main){
                if(result)
                    _logoutProgress.value = 2
                else
                    _logoutProgress.value = 3
            }
        }
    }

}