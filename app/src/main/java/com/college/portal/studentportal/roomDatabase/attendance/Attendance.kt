package com.college.portal.studentportal.roomDatabase.attendance

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "my_attendance", primaryKeys = ["day","month","year"])
class Attendance(){
    var isPresent:Boolean = true
    var day: Int = 0
    var month: String = "month"
    var year:Int = 2022
}
