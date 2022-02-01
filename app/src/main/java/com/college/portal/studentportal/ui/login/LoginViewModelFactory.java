package com.college.portal.studentportal.ui.login;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.annotation.NonNull;

import com.college.portal.studentportal.data.LoginDataSource;
import com.college.portal.studentportal.data.LoginRepository;
import com.college.portal.studentportal.roomDatabase.UsersDatabase;

/**
 * ViewModel provider factory to instantiate LoginViewModel.
 * Required given LoginViewModel has a non-empty constructor
 */
public class LoginViewModelFactory implements ViewModelProvider.Factory {


    SharedPreferences preferences;
    UsersDatabase usersDatabase;

    public LoginViewModelFactory(SharedPreferences preferences, UsersDatabase usersDatabase) {
        this.preferences = preferences;
        this.usersDatabase = usersDatabase;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(LoginViewModel.class)) {

            return (T) new LoginViewModel(LoginRepository.getInstance(new LoginDataSource()),preferences,usersDatabase);
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}