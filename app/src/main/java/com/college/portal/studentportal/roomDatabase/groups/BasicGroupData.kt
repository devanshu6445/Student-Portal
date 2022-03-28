package com.college.portal.studentportal.roomDatabase.groups

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "groupData")
class BasicGroupData(): Serializable{

    var groupName:String = ""
    var groupImage:String =""
    var groupPurpose:String = ""
    var groupSemester:String = ""
    @PrimaryKey(autoGenerate = false) var groupID: String = ""

    constructor(groupName:String,
                groupImage:String,
                groupPurpose:String,
                groupSemester:String, groupID:String) : this() {
        this.groupName = groupName
        this.groupImage = groupImage
        this.groupPurpose  = groupPurpose
        this.groupSemester  =groupSemester
        this.groupID = groupID
    }


}