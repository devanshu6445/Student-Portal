package com.college.portal.studentportal.roomDatabase.user

import androidx.room.*
import com.college.portal.studentportal.data.model.LoggedInUser
import kotlinx.coroutines.flow.Flow

@Dao
interface
CurrentUserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCurrentUser(currentUserEntity: LoggedInUser)

    @Query("SELECT * FROM currentUser WHERE userUid = (SELECT MAX(userUid) FROM currentUser)")
    fun getCurrentUser(): LoggedInUser

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertStudentIntoStudentDatabase(studentList: List<LoggedInUser>)

    @Query("SELECT * FROM currentUser WHERE userSemester == :semester AND userCourse == :course AND userSection == :section ORDER BY rollNo")
    fun getStudentListForAttendance(semester:String,course:String,section:String) :Flow<List<LoggedInUser>>

    @Query("SELECT * FROM currentUser WHERE userSemester == :semester AND userCourse == :course ORDER BY rollNo")
    fun getStudentListForShowingAttendance(semester:String,course:String) :Flow<List<LoggedInUser>>

    @Query("SELECT * FROM currentUser WHERE userUid == :uid")
    fun getStudent(uid:String): LoggedInUser
}