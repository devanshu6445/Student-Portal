package com.college.portal.studentportal.callback

import com.college.portal.studentportal.data.model.BasicGroupData

interface FirebaseGDFCompleteListener {

    fun onFetchComplete(groupDataList:MutableList<BasicGroupData>)
}