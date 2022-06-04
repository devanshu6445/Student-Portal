package com.college.portal.studentportal.roomDatabase.assignment

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "assignment_details")
class Assignment {

    var assignmentSubjectCode:String = ""
    var assignmentID = ""
    var assignmentDeadline = ""
    var assignmentTitle = ""
    var assignmentCourse = ""
    var assignmentSection = ""
    var assignmentInstruction = ""
    var assignmentGivenBy = ""
    var assignmentURL:String? = null
    @PrimaryKey(autoGenerate = false) var assignmentTimestamp = 0L
}