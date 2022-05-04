package com.college.portal.studentportal.roomDatabase.groupEverything

import android.content.Context
import android.database.sqlite.SQLiteCantOpenDatabaseException
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [GroupMessageInfo::class,Participant::class,MessageRequest::class], version = 1, exportSchema = false)
abstract class GroupMessageDatabase: RoomDatabase() {

    abstract fun getGroupMessageDao() : GroupMessageDao


    companion object{
        @Volatile
        private var INSTANCE: GroupMessageDatabase? = null

        private val callback = object : RoomDatabase.Callback() {}

        fun createDatabase(context: Context, databaseName: String): GroupMessageDatabase {
            //create database instance everytime this function is called
            return synchronized(this) {
                val instance = Room.databaseBuilder(context.applicationContext,
                    GroupMessageDatabase::class.java,
                    databaseName)
                    .addCallback(callback)
                    .build()
                INSTANCE = instance
                instance
            }
        }

        /**
         * This function will be used to get the already created database instance
         */
        fun getDatabase(): GroupMessageDatabase = INSTANCE ?: throw SQLiteCantOpenDatabaseException("Database isn't here")
    }
}