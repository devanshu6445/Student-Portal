package com.college.portal.studentportal.roomDatabase.assignment

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Assignment::class], version = 1, exportSchema = false)
abstract class AssignmentDatabase : RoomDatabase() {

    abstract fun getAssignmentDao(): AssignmentDao

    companion object {

        fun getDatabase(context: Context): AssignmentDatabase {
            return synchronized(this) {
                val database =
                    Room.databaseBuilder(context, AssignmentDatabase::class.java, "assignments")
                        .build()
                database
            }
        }
    }
}