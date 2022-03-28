package com.college.portal.studentportal.roomDatabase.user

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.college.portal.studentportal.data.model.LoggedInUser

@Database(entities = [LoggedInUser::class], version = 1, exportSchema = false)
abstract class CurrentUserDatabase: RoomDatabase() {

    abstract fun getCurrentUserDao(): CurrentUserDao

    companion object{

        @Volatile
        private var INSTANCE: CurrentUserDatabase? = null

        fun getDatabase(context: Context): CurrentUserDatabase {

            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(context.applicationContext,
                    CurrentUserDatabase::class.java,
                    "currentUserDatabase")
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}