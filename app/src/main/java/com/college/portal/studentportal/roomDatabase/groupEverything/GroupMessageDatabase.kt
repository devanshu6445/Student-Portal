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

        fun createDatabase(context: Context, databaseName: String): GroupMessageDatabase {

            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(context.applicationContext,
                    GroupMessageDatabase::class.java,
                    databaseName)
                    .build()
                INSTANCE = instance
                instance
            }
        }

        fun getDatabase(): GroupMessageDatabase = INSTANCE ?: throw SQLiteCantOpenDatabaseException("Database isn't here")
    }
}