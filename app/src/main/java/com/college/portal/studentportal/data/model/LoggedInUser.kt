package com.college.portal.studentportal.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
@Entity(tableName = "currentUser")
class LoggedInUser() {

    var userUid: String = ""
    var userName: String = ""
    var userImageUrl: String = ""
    var role: String = ""
    var lastLogin: Long = 11111
    var userSemester: String = ""
    @PrimaryKey(autoGenerate = true) var id = 0

    constructor(
        userName:String,
        userImageUrl:String,
        userUid:String,
        userSemester:String,
        lastLogin: Long,
        role:String
    ): this(){
        this.userName = userName
        this.userImageUrl = userImageUrl
        this.userUid = userUid
        this.userSemester = userSemester
        this.lastLogin = lastLogin
        this.role = role
    }

}