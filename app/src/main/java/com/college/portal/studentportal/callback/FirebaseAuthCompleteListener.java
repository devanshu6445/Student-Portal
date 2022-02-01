package com.college.portal.studentportal.callback;

import com.college.portal.studentportal.data.Result;
import com.college.portal.studentportal.data.model.LoggedInUser;

public interface FirebaseAuthCompleteListener {
    void onAuthComplete(Result<LoggedInUser> result);
    void onAuthFailure(Result.Error error);
}
