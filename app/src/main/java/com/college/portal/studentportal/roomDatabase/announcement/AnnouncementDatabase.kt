package com.college.portal.studentportal.roomDatabase.announcement

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [AnnouncementEntity::class], version = 1, exportSchema = false)
abstract class AnnouncementDatabase : RoomDatabase() {

    abstract fun getAnnouncementDao(): AnnouncementDao

    companion object {

        fun getDatabase(context: Context): AnnouncementDatabase {
            return synchronized(this) {
                val database =
                    Room.databaseBuilder(
                        context,
                        AnnouncementDatabase::class.java,
                        "announcement"
                    )
                        .build()
                database
            }
        }
    }
}