package com.college.portal.studentportal.ui.notifications.changePassword

import com.college.portal.studentportal.callback.FirebaseGeneralCallback
import com.college.portal.studentportal.data.model.ChangePassword
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import java.lang.Exception

class ChangePasswordRepository(private val firebaseGeneralCallback: FirebaseGeneralCallback) {
    private val userAuth  = FirebaseAuth.getInstance()
    private val mUser = userAuth.currentUser

    private fun changePassword(changePassword: ChangePassword){
        mUser?.updatePassword(
            changePassword.newPassword
        )?.addOnCompleteListener {
            if(it.isSuccessful){
                firebaseGeneralCallback.onSuccessful()
            }else{
                firebaseGeneralCallback.onFailure(it.exception?: Exception("Unknown error"))
            }
        }
    }

    fun reAuthenticateUser(changePassword: ChangePassword){
        val authCredential = EmailAuthProvider.getCredential(mUser?.email.toString(),changePassword.currentPassword)
        mUser?.reauthenticate(authCredential)
            ?.addOnCompleteListener {
                if (it.isSuccessful){
                    changePassword(changePassword)
                }else{
                    firebaseGeneralCallback.onFailure(it.exception?:Exception("User authentication failed!"))
                }
            }
    }

    fun reAuthenticateUser(password: String){
        val authCredential = EmailAuthProvider.getCredential(mUser?.email.toString(),password)
        mUser?.reauthenticate(authCredential)
            ?.addOnCompleteListener {
                if (it.isSuccessful){
                    firebaseGeneralCallback.onSuccessful()
                }else{
                    firebaseGeneralCallback.onFailure(it.exception?:Exception("User authentication failed!"))
                }
            }
    }
}