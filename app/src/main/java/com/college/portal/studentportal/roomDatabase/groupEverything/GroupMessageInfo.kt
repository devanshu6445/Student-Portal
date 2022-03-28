package com.college.portal.studentportal.roomDatabase.groupEverything

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")
class GroupMessageInfo() {

    var senderName:String = ""
    var textMessage:String =""
    var textMessageTime:String = ""
    var senderUid:String = ""
    var imageURL: String = ""
    var imageMessageTime: String = ""
    var imageMessageCaption: String = ""
    var senderImageURL: String = ""
    var approvedBy:String  = ""
    var messageID: String = ""
    @PrimaryKey(autoGenerate = false) var messageTimeStamp: Long = 0

    constructor(senderName:String,
                textMessage:String,
                textMessageTime:String,
                senderUid:String,
                senderImageURL:String,
                approvedBy:String,
                messageID: String,
                messageTimeStamp:Long) : this() {
        this.senderName = senderName
        this.textMessage = textMessage
        this.textMessageTime = textMessageTime
        this.senderUid = senderUid
        this.senderImageURL = senderImageURL
        this.approvedBy = approvedBy
        this.messageID = messageID
        this.messageTimeStamp = messageTimeStamp
    }

    constructor(senderName:String,
                imageURL:String,
                imageMessageTime:String,
                senderUid:String,
                imageMessageCaption:String,
                senderImageURL: String,
                approvedBy: String,
                messageID: String,
                messageTimeStamp:Long) : this() {
        this.senderName = senderName
        this.imageURL = imageURL
        this.imageMessageTime = imageMessageTime
        this.senderUid = senderUid
        this.imageMessageCaption = imageMessageCaption
        this.senderImageURL = senderImageURL
        this.approvedBy = approvedBy
        this.messageID = messageID
        this.messageTimeStamp = messageTimeStamp
    }
}
