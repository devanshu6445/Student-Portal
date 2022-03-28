package com.college.portal.studentportal.roomDatabase.groups

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [BasicGroupData::class], version = 1, exportSchema = false)
abstract class GroupDatabase: RoomDatabase() {

    abstract fun getGroupDao(): GroupDao

    companion object{
        private var INSTANCE:GroupDatabase? = null

        fun getInstance(context: Context): GroupDatabase{

            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context,
                    GroupDatabase::class.java,
                    "GroupDatabase")
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}