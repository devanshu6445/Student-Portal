package com.college.portal.studentportal.roomDatabase.attendance

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Attendance::class], version = 1, exportSchema = false)
abstract class AttendanceDatabase: RoomDatabase() {

    abstract fun getAttendanceDao(): AttendanceDao

    companion object{
        private var database: AttendanceDatabase? = null

        fun getDatabase(context: Context, subjectCode:String): AttendanceDatabase{
            //create database( according to subject code) every time this function is called
            return synchronized(this){
                val database = Room.databaseBuilder(
                    context,
                    AttendanceDatabase::class.java,
                    subjectCode).build()
                this.database = database
                database
            }
        }
    }
}