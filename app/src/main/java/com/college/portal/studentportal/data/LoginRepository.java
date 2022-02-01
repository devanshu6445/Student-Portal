package com.college.portal.studentportal.data;

import com.college.portal.studentportal.callback.FirebaseAuthCompleteListener;
import com.college.portal.studentportal.data.model.LoggedInUser;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */

public class LoginRepository {

    private static volatile LoginRepository instance;
    FirebaseAuth userAuth = FirebaseAuth.getInstance();
    FirebaseUser userDetails;
    private LoginDataSource dataSource;
    private static final String TAG = "Login";


    // If user credentials will be cached in local storage, it is recommended it be encrypted
    // @see https://developer.android.com/training/articles/keystore
    private LoggedInUser user = null;

    // private constructor : singleton access
    private LoginRepository(LoginDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public static LoginRepository getInstance(LoginDataSource dataSource) {
        if (instance == null) {
            instance = new LoginRepository(dataSource);
        }
        return instance;
    }

    public boolean isLoggedIn() {
        return user != null;
    }

    public void logout() {
        user = null;
        dataSource.logout();
    }

    private void setLoggedInUser(LoggedInUser user) {
        this.user = user;
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
    }
    private Result<LoggedInUser> result;
    private LoggedInUser logged;
    public void login(String username, String password, FirebaseAuthCompleteListener listener) {
        // handle login
        userAuth.signInWithEmailAndPassword(username,password)
                .addOnSuccessListener(authResult -> {
                    userDetails = userAuth.getCurrentUser();
                    //TODO: REMOVE THE COMMENT AFTER UPLOADING USERS TO DATABASE FROM CONSOLE
                    //fetchLoggedInUserData(userDetails.getUid(),listener);
                    logged = new LoggedInUser(userDetails.getUid(),userDetails.getEmail(),"Admin","today","1");
                    result = new Result.Success<>(logged);
                    listener.onAuthComplete(result);
                })
                .addOnFailureListener(e -> {
                    Result.Error resultError = new Result.Error(e);
                    listener.onAuthFailure(resultError);
                });
    }

    private void fetchLoggedInUserData(String uid,FirebaseAuthCompleteListener listener){
        FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        logged = documentSnapshot.toObject(LoggedInUser.class);
                        result = new Result.Success<>(logged);
                        listener.onAuthComplete(result);
                    }
                });
    }
}