package com.college.portal.studentportal.ui.login;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import android.content.SharedPreferences;
import android.util.Patterns;

import com.college.portal.studentportal.callback.FirebaseAuthCompleteListener;
import com.college.portal.studentportal.data.LoginRepository;
import com.college.portal.studentportal.data.Result;
import com.college.portal.studentportal.data.model.LoggedInUser;
import com.college.portal.studentportal.R;
import com.college.portal.studentportal.roomDatabase.UserDao;
import com.college.portal.studentportal.roomDatabase.UsersDatabase;
import com.college.portal.studentportal.roomDatabase.UsersInfo;
import com.google.firebase.firestore.auth.User;

public class LoginViewModel extends ViewModel implements FirebaseAuthCompleteListener {

    private MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    private LoginRepository loginRepository;
    private SharedPreferences preferences;
    private UsersDatabase usersDatabase;


    LoginViewModel(LoginRepository loginRepository,SharedPreferences preferences,UsersDatabase usersDatabase) {
        this.loginRepository = loginRepository;
        this.preferences = preferences;
        this.usersDatabase = usersDatabase;
    }


    MutableLiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    MutableLiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    public void login(String username, String password) {
        // can be launched in a separate asynchronous job
        loginRepository.login(username,password,this);
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
        loginResult.setValue(new LoginResult(new LoggedInUserView(data.getDisplayName())));
        saveUserData(data);

    }

    private void saveUserData(LoggedInUser loggedInUser){
        /*UserDao userDao = usersDatabase.getUserDao();
        Thread thread = new Thread(() ->
                userDao.insertJava(new UsersInfo(
                        loggedInUser.getUserId(),
                        loggedInUser.getDisplayName(),
                        loggedInUser.getRole(),
                        loggedInUser.getLastLogin())));
        thread.start();*/
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("uid",loggedInUser.getUserId());
        editor.putString("displayName",loggedInUser.getDisplayName());
        editor.putString("role",loggedInUser.getRole());
        editor.putString("lastLoginTime",loggedInUser.getLastLogin());
        editor.putString("semester",loggedInUser.getSemester());
        editor.apply();

    }

    @Override
    public void onAuthFailure(Result.Error error) {
        loginResult.setValue(new LoginResult(R.string.login_failed));
    }
}