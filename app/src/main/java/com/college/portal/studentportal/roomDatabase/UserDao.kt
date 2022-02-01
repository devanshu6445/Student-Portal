package com.college.portal.studentportal.roomDatabase

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertJava(user: UsersInfo)


}