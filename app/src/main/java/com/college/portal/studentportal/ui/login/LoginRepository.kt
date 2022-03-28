package com.college.portal.studentportal.ui.login

import android.util.Log
import com.college.portal.studentportal.callback.FirebaseAuthCompleteListener
import com.college.portal.studentportal.data.model.LoggedInUser
import com.college.portal.studentportal.roomDatabase.user.CurrentUserDatabase
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */
class LoginRepository {
    private var userAuth = FirebaseAuth.getInstance()
    private var userDetails: FirebaseUser? = null

    // If user credentials will be cached in local storage, it is recommended it be encrypted
    // @see https://developer.android.com/training/articles/keystore
    private var user: LoggedInUser? = null
    val isLoggedIn: Boolean
        get() = user != null

    fun logout() {
        user = null
    }

    private fun setLoggedInUser(user: LoggedInUser) {
        this.user = user
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
    }

    private var result: Result<LoggedInUser?>? = null
    private var logged: LoggedInUser? = null
    private lateinit var userDatabase: CurrentUserDatabase
    fun login(
        username: String?, password: String?,
        listener: FirebaseAuthCompleteListener,
        userDatabase: CurrentUserDatabase?
    ) {
        // handle login
        userAuth.signInWithEmailAndPassword(username!!, password!!)
            .addOnSuccessListener { authResult: AuthResult? ->

                userDetails = userAuth.currentUser
                this.userDatabase = userDatabase!!
                fetchLoggedInUserData(userDetails?.uid!!,listener)
            }
            .addOnFailureListener { e: Exception? ->
                val resultError = Result.Error(e)
                listener.onAuthFailure(resultError)
            }
    }

    private fun fetchLoggedInUserData(uid: String, listener: FirebaseAuthCompleteListener) {
        FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { documentSnapshot: DocumentSnapshot ->
                logged = documentSnapshot.toObject(
                    LoggedInUser::class.java
                )
                result = Result.Success<LoggedInUser?>(logged)

                GlobalScope.launch(Dispatchers.IO) {
                    val currentUserDao = userDatabase.getCurrentUserDao()
                    if (logged!=null){
                        currentUserDao.insertCurrentUser(logged!!)
                        withContext(Dispatchers.Main){
                            listener.onAuthComplete(result)
                        }
                    }else{
                        throw ClassCastException(
                            "Could not cast document snapshot data to CurrentUserEntity"
                        )
                    }
                }
            }
            .addOnFailureListener { e: Exception ->
                Log.i("user-data-fetch-failed", e.message, e)
            }
            .addOnCanceledListener {
                Log.d(
                    "user-data-fetch-canceled",
                    "May be canceled by the user itself"
                )
            }
    }
}