package com.college.portal.studentportal.callback

import com.college.portal.studentportal.data.model.GroupMessageData

interface FirebaseGMDFCompleteListener {

    fun onComplete(groupMessageList:MutableList<GroupMessageData>)
    fun onFailure()
}