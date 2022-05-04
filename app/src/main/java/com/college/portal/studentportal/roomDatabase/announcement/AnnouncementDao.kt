package com.college.portal.studentportal.roomDatabase.announcement

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AnnouncementDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAnnouncement(announcementList : List<AnnouncementEntity>)

    @Query("SELECT * FROM announcements WHERE announcementType == 'Universal'")
    fun getUniversalAnnouncements():Flow<List<AnnouncementEntity>>

    @Query("SELECT * FROM announcements WHERE announcementCourse == :course")
    fun getCourseAnnouncements(course : String): Flow<List<AnnouncementEntity>>

    @Query("SELECT * FROM announcements WHERE announcementCourse == :course AND announcementClass == :section")
    fun getClassAnnouncements(course: String,section : String): Flow<List<AnnouncementEntity>>

    @Query("SELECT * FROM announcements WHERE (announcementTimestamp+86000) > :currentTimestamp ORDER BY CASE announcementType WHEN 'Universal' THEN 0 WHEN 'Course' THEN 1 WHEN 'Class' THEN 2 END LIMIT 1" )
    fun getLatestAnnouncement(currentTimestamp:Long) : Flow<AnnouncementEntity>

    @Query("SELECT * FROM announcements ORDER BY CASE announcementType WHEN 'Universal' THEN 0 WHEN 'Course' THEN 1 WHEN 'Class' THEN 2 END" )
    fun getAllAnnouncements():Flow<List<AnnouncementEntity>>

}