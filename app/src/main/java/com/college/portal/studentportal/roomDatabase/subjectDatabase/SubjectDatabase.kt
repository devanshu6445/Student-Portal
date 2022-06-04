package com.college.portal.studentportal.roomDatabase.subjectDatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.college.portal.studentportal.data.model.Subject

@Database(entities = [Subject::class], version = 1, exportSchema = false)
abstract class SubjectDatabase : RoomDatabase() {

    abstract fun getSubjectDao(): SubjectDao

    companion object{
        fun getDatabase(context: Context): SubjectDatabase{

            return synchronized(this){
                val database = Room.databaseBuilder(context,SubjectDatabase::class.java,"subjectDatabase").build()
                database
            }
        }
    }
}