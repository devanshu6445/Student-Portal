package com.college.portal.studentportal.data.model

class SendMessageObject() {

    var senderName: String = ""
    var textMessage: String = ""
    var senderUid: String = ""
    var senderImageURL: String = ""
    var approvedBy: String = ""
    var docURL: String = ""
    var messageID: String = ""
    var mimeType: String = ""
    var messageTimeStamp: Long = 0

    constructor(
        senderName: String,
        textMessage: String,
        docURL: String,
        senderUid: String,
        senderImageURL: String,
        approvedBy: String,
        messageID: String,
        mimeType: String,
        messageTimeStamp: Long
    ) : this() {
        this.senderName = senderName
        this.textMessage = textMessage
        this.docURL = docURL
        this.senderUid = senderUid
        this.senderImageURL = senderImageURL
        this.approvedBy = approvedBy
        this.messageID = messageID
        this.mimeType = mimeType
        this.messageTimeStamp = messageTimeStamp
    }

}