package com.college.portal.studentportal.callback

import com.college.portal.studentportal.roomDatabase.groups.BasicGroupData

interface FirebaseGDFCompleteListener {

    fun onFetchComplete(groupDataList:MutableList<BasicGroupData>)
}