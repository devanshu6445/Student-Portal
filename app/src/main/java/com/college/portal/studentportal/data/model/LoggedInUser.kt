package com.college.portal.studentportal.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
@Entity(tableName = "currentUser")
class LoggedInUser() : Serializable{

    @PrimaryKey(autoGenerate = false) var userUid: String = ""
    var userName: String = ""
    var userImageUrl: String = ""
    var role: String = ""
    var lastLogin: Long = 11111
    var userSemester: String = ""
    var userCourse: String = ""
    var emailAddress:String = ""
    var presentLivingAddress = ""
    var dateOfBirth:String = ""
    var phoneNumber:String = ""
    var userDesignation:String = ""
    var userSection:String = ""
    var rollNo:Int = 0

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