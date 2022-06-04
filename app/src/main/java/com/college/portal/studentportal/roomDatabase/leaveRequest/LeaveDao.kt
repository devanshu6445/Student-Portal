package com.college.portal.studentportal.roomDatabase.leaveRequest

import androidx.room.*
import com.college.portal.studentportal.data.model.LeaveRequest
import kotlinx.coroutines.flow.Flow

@Dao
interface LeaveDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLeaveRequest(leaveRequestList: List<LeaveRequest>)

    @Query("SELECT * FROM leaves ORDER BY timestamp DESC")
    fun getAllLeaves(): Flow<List<LeaveRequest>>

    @Delete
    fun deleteLeaveRequest(leaveRequest: LeaveRequest)
}