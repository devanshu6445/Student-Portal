package com.college.portal.studentportal.roomDatabase.assignment

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AssignmentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAssignment(assignmentList: List<Assignment>)

    @Query("SELECT * FROM assignment_details ORDER BY assignmentTimestamp DESC")
    fun getAllAssignments():Flow<List<Assignment>>

    @Query("SELECT * FROM assignment_details WHERE assignmentID == :assignmentID")
    fun getSingleAssignment(assignmentID:String):Assignment
}