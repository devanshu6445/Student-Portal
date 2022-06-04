package com.college.portal.studentportal.roomDatabase.leaveRequest

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.college.portal.studentportal.data.model.LeaveRequest

@Database(entities = [LeaveRequest::class], version = 1, exportSchema = false)
abstract class LeaveDatabase: RoomDatabase() {

    abstract fun getLeaveDao(): LeaveDao

    companion object{

        fun getDatabase(context: Context): LeaveDatabase{
            return synchronized(this){
                val database = Room.databaseBuilder(context,LeaveDatabase::class.java,"leaveDatabase").build()
                database
            }
        }
    }
}