package com.college.portal.studentportal.ui.login

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.college.portal.studentportal.roomDatabase.user.CurrentUserDatabase

/**
 * ViewModel provider factory to instantiate LoginViewModel.
 * Required given LoginViewModel has a non-empty constructor
 */

class LoginViewModelFactory(
    private var preferences: SharedPreferences,
    private var userDatabase: CurrentUserDatabase
    ) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            LoginViewModel(LoginRepository(), preferences, userDatabase) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

}