package com.college.portal.studentportal.roomDatabase.announcement

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "announcements")
data class AnnouncementEntity(

    val announcementText:String,
    val announcementID:String,
    val announcementType:String,
    val announcementCreator:String,
    val announcementCourse:String?,
    val announcementClass:String?,
    @PrimaryKey(autoGenerate = false) val announcementTimestamp: Long
)
