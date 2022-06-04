package com.college.portal.studentportal.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "subjects")
class Subject() : Serializable{

    @PrimaryKey(autoGenerate = false) var subCode = ""
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