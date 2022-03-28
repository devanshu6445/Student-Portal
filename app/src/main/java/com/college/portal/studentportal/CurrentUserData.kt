package com.college.portal.studentportal

import android.content.Context
import com.college.portal.studentportal.data.model.LoggedInUser

class CurrentUserData(val userName:String?,
                      val userImageUrl: String?,
                      val userUid:String?,
                      val userSemester:String?){


    companion object{
        private var currentUserData: CurrentUserData? = null

        fun getCurrentUser(context: Context){
            if (currentUserData!=null){
                val preferences = context.getSharedPreferences("userData",Context.MODE_PRIVATE)
                currentUserData = preferences?.let {
                    CurrentUserData(
                        it.getString("displayName","uid")!!,
                        it.getString("lastLoginTime","uid")!!,
                        it.getString("uid","student")!!,
                        it.getString("semester","uid")!!
                    )
                }!!
            }
        }
    }
}