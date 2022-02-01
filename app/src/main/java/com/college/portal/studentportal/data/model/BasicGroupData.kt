package com.college.portal.studentportal.data.model

class BasicGroupData(){

    var groupName:String = ""
    var groupImage:String =""
    var groupPurpose:String = ""
    var groupSemester:String = ""

    constructor(groupName:String,
                groupImage:String,
                groupPurpose:String,
                groupSemester:String) : this() {
        this.groupName = groupName
        this.groupImage = groupImage
        this.groupPurpose  = groupPurpose
        this.groupSemester  =groupSemester
    }


}