package com.college.portal.studentportal.callback

import com.college.portal.studentportal.data.model.LoggedInUser
import com.college.portal.studentportal.ui.login.Result
import java.lang.Exception

interface FirebaseAuthCompleteListener {
    fun onAuthComplete(result: Result<LoggedInUser?>?)
    fun onAuthFailure(error: Result.Error?)
}

interface FirebaseGeneralCallback {

    fun onSuccessful()
    fun onFailure(error: Exception)
}