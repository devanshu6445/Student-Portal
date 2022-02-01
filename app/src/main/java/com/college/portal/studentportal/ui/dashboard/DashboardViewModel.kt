package com.college.portal.studentportal.ui.dashboard

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.college.portal.studentportal.callback.FirebaseGDFCompleteListener
import com.college.portal.studentportal.data.GroupRepository
import com.college.portal.studentportal.data.model.BasicGroupData
import com.college.portal.studentportal.data.model.LoggedInUser

class DashboardViewModel(val preferences: SharedPreferences?) : ViewModel(),FirebaseGDFCompleteListener {

    private val _groupList = MutableLiveData<MutableList<BasicGroupData>>()
    private val groupRepository = GroupRepository()
    private lateinit var loggedInUser: LoggedInUser

    companion object{
        private val TAG = "DashboardViewModel"
    }

    init {
        retrieveGroup()
    }
    private val _text = MutableLiveData<String>().apply {
        value = "This is dashboard Fragment"
    }
    val text: LiveData<String> = _text
    val groupList:LiveData<MutableList<BasicGroupData>> = _groupList

    private fun retrieveGroup(){
        loggedInUser = preferences?.let {
                LoggedInUser(
                    it.getString("uid","student")!!,
                    it.getString("displayName","uid")!!,
                    it.getString("role","uid")!!,
                    it.getString("lastLoginTime","uid")!!,
                    it.getString("semester","uid")!!
                )
        }!!
        Log.d(TAG, "retrieveGroup: loggedInUserObject -> ${loggedInUser.semester + loggedInUser.role}")
        groupRepository.retrieveGroupsRDatabase(this,loggedInUser)
    }

    override fun onFetchComplete(groupDataList: MutableList<BasicGroupData>) {
        _groupList.apply {
            value=groupDataList
        }
    }


}