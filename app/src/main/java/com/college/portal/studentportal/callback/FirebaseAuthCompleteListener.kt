package com.college.portal.studentportal.callback

import com.college.portal.studentportal.data.model.LoggedInUser
import com.college.portal.studentportal.ui.login.Result

interface FirebaseAuthCompleteListener {
    fun onAuthComplete(result: Result<LoggedInUser?>?)
    fun onAuthFailure(error: Result.Error?)
}