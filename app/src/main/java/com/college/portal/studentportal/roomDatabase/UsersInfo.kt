package com.college.portal.studentportal.roomDatabase

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users_info")
class UsersInfo(@ColumnInfo(name = "uid") val uid: String,
                @ColumnInfo(name = "name") val name: String,
                @ColumnInfo(name = "Role") val role:String,
                @ColumnInfo(name = "lastLogin") val lastLogin: String){
    @PrimaryKey(autoGenerate = true) var id = 0
}
