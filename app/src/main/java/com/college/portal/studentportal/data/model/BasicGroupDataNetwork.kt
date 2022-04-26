package com.college.portal.studentportal.data.model

import java.io.Serializable

class BasicGroupDataNetwork(): Serializable {

    var groupName:String = ""
    var groupImage:String =""
    var groupPurpose:String = ""
    var groupSemester:String = ""
    var groupID: String = ""

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