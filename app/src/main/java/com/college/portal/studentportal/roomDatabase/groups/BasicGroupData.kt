package com.college.portal.studentportal.roomDatabase.groups

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

/**
 * This class is made just to use in the local database(Room)
 * any data fetched from network will be calculated then inserted into local database
 * to improve the performance of app
 */
@Entity(tableName = "groupData")
class BasicGroupData(
    val groupName:String,
    val groupImage:String,
    val groupPurpose:String,
    val groupSemester:String,
    val groupCourse:String,
    @PrimaryKey(autoGenerate = false) val groupID: String,
    val groupLastAnnouncement:String) : Serializable
