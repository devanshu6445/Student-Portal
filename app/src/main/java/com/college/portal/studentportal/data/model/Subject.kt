package com.college.portal.studentportal.data.model

import java.io.Serializable

class Subject() : Serializable{

    var subCode = ""
    var subName = ""
    var subSem = ""
    var subTaughtBy = ""
    var subCourse = ""

    constructor(
        subCode:String,
        subName:String,
        subSem:String,
        subTaughtBy:String
    ): this(){
        this.subCode = subCode
        this.subName = subName
        this.subSem = subSem
        this.subTaughtBy = subTaughtBy
    }

}