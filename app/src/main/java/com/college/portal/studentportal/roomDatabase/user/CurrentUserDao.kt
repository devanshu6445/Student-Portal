package com.college.portal.studentportal.roomDatabase.user

import androidx.room.*
import com.college.portal.studentportal.data.model.LoggedInUser
import kotlinx.coroutines.flow.Flow

@Dao
interface
CurrentUserDao {

    @Insert
    fun insertCurrentUser(currentUserEntity: LoggedInUser)

    @Query("SELECT * FROM currentUser WHERE id = (SELECT MAX(id) FROM currentUser)")
    fun getCurrentUser(): LoggedInUser

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertStudentIntoStudentDatabase(studentList: List<LoggedInUser>)

    @Query("SELECT * FROM currentUser WHERE userSemester == :semester AND userCourse == :course")
    fun getStudentListForAttendance(semester:String,course:String) :Flow<List<LoggedInUser>>
}