package com.college.portal.studentportal.roomDatabase.groupEverything

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "groupParticipants")
class Participant(){

    var name: String = ""
    var imageURL: String = ""
    var role:String = ""
    @PrimaryKey(autoGenerate = false) var uid: String = ""

    constructor(name: String,
                imageURL: String,
                role:String,
                uid: String
    ) : this(){
        this.name = name
        this.imageURL = imageURL
        this.role = role
        this.uid = uid
    }
}


