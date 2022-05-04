package com.college.portal.studentportal.roomDatabase.attendance

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AttendanceDao {

    @Query("SELECT * FROM my_attendance WHERE month = :month ORDER BY day ASC")
    fun getAll(month:String):  List<Attendance>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAttendance(attendanceList: List<Attendance>)

    @Query("SELECT (COUNT(*)*100/(SELECT COUNT(*) FROM my_attendance WHERE month == :month)) FROM my_attendance WHERE isPresent == 1 AND month == :month")
    fun getMonthAttendancePercentage(month:String): String

    @Query("SELECT (COUNT(*)*100/(SELECT COUNT(*) FROM my_attendance)) FROM my_attendance WHERE isPresent == 1 ")
    fun getOverAllAttendancePercentage(): String
}