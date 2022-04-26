package com.college.portal.studentportal.data.model

import androidx.room.PrimaryKey

class MessageRequestNetwork() {

    var senderName:String = ""
    var textMessage:String =""
    var senderUid:String = ""
    var docURL: String = ""
    var senderImageURL: String = ""
    var messageID: String = ""
    var mimeType: String = ""
    @PrimaryKey(autoGenerate = false) var messageTimeStamp: Long = 0

    constructor(senderName:String,
                textMessage:String,
                docURl:String,
                senderUid:String,
                senderImageURL:String,
                messageID: String,
                mimeType:String,
                messageTimeStamp:Long) : this() {
        this.senderName = senderName
        this.textMessage = textMessage
        this.docURL = docURL
        this.senderUid = senderUid
        this.senderImageURL = senderImageURL
        this.messageID = messageID
        this.mimeType = mimeType
        this.messageTimeStamp = messageTimeStamp
    }

}