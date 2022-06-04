package com.college.portal.studentportal.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Timestamp

@Entity(tableName = "leaves")
class LeaveRequest() {

    var leaveReason = ""
    var requesterName = ""
    var requesterCourse = ""
    var requesterSem = ""
    var requesterUid = ""
    var fromDate: String = ""
    var toDate: String = ""
    @PrimaryKey(autoGenerate = false) var timestamp:Long = 0
    var status:String = ""
    var duration = ""

    constructor(leaveReason: String,
                requesterName: String,
                requesterCourse: String,
                requesterSem: String,
                requesterUid: String,
                fromDate: String,
                toDate:String,timestamp: Long): this(){
        this.leaveReason = leaveReason
        this.requesterName = requesterName
        this.requesterCourse = requesterCourse
        this.requesterSem = requesterSem
        this.requesterUid = requesterUid
        this.fromDate = fromDate
        this.toDate = toDate
        this.timestamp = timestamp
    }
}