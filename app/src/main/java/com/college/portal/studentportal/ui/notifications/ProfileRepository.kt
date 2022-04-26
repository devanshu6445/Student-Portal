package com.college.portal.studentportal.ui.notifications

import com.college.portal.studentportal.CurrentUserData
import com.college.portal.studentportal.data.model.LoggedInUser
import com.college.portal.studentportal.roomDatabase.user.CurrentUserDatabase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ProfileRepository(private val database: CurrentUserDatabase) {

    private val mFirebaseUser = FirebaseAuth.getInstance()
    private val mDatabaseReference = FirebaseDatabase.getInstance().reference

    suspend fun logout():Boolean = withContext(Dispatchers.IO){
        val result = try {
            mFirebaseUser.signOut()
            database.clearAllTables()
            true
        }catch (e: Exception){
            false
        }
        return@withContext result
    }
    suspend fun getProfile(): LoggedInUser = withContext(Dispatchers.IO){
        return@withContext database.getCurrentUserDao().getCurrentUser()
    }

    fun updateProfile(){

    }
}