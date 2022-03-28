package com.college.portal.studentportal.roomDatabase.user

import androidx.room.*
import com.college.portal.studentportal.data.model.LoggedInUser

@Dao
interface
CurrentUserDao {

    @Insert
    fun insertCurrentUser(currentUserEntity: LoggedInUser)

    @Query("SELECT * FROM currentUser WHERE id = (SELECT MAX(id) FROM currentUser)")
    fun getCurrentUser(): LoggedInUser
}