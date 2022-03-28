package com.college.portal.studentportal.ui.login;

import android.content.SharedPreferences;
import android.util.Patterns;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.college.portal.studentportal.R;
import com.college.portal.studentportal.callback.FirebaseAuthCompleteListener;
import com.college.portal.studentportal.data.model.LoggedInUser;
import com.college.portal.studentportal.roomDatabase.user.CurrentUserDatabase;

public class LoginViewModel extends ViewModel implements FirebaseAuthCompleteListener {

    private final MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private final MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    private final LoginRepository loginRepository;
    private final SharedPreferences preferences;
    private final CurrentUserDatabase userDatabase;

    LoginViewModel(LoginRepository loginRepository, SharedPreferences preferences, CurrentUserDatabase userDatabase) {
        this.loginRepository = loginRepository;
        this.preferences = preferences;
        this.userDatabase = userDatabase;
    }

    MutableLiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    MutableLiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    public void login(String username, String password) {
        // can be launched in a separate asynchronous job
        loginRepository.login(username,password,this,userDatabase);
    }

    public void loginDataChanged(String username, String password) {
        if (!isUserNameValid(username)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_username, null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_password));
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    // A placeholder username validation check
    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        if (username.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        } else {
            return !username.trim().isEmpty();
        }
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }

    @Override
    public void onAuthComplete(Result<LoggedInUser> result) {
        LoggedInUser data = ((Result.Success<LoggedInUser>) result).getData();

        loginResult.setValue(new LoginResult(new LoggedInUserView(data.getUserName())));
        saveUserData(data);

    }

    private void saveUserData(LoggedInUser loggedInUser){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("uid",loggedInUser.getUserUid());
        editor.putString("displayName",loggedInUser.getUserName());
        editor.putString("role",loggedInUser.getRole());
        editor.putLong("lastLoginTime",loggedInUser.getLastLogin());
        editor.putString("semester",loggedInUser.getUserSemester());
        editor.apply();

    }

    @Override
    public void onAuthFailure(Result.Error error) {
        loginResult.setValue(new LoginResult(R.string.login_failed));
    }
}