package com.college.portal.studentportal.ui.dashboard

import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.college.portal.studentportal.roomDatabase.groups.BasicGroupData
import com.college.portal.studentportal.roomDatabase.groups.GroupDatabase
import com.college.portal.studentportal.roomDatabase.user.CurrentUserDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DashboardViewModel(

    preferences: SharedPreferences?,
    private val currentUserDatabase: CurrentUserDatabase,
    private val groupDatabase: GroupDatabase

) : ViewModel() {

    private val groupRepository = GroupRepository(groupDatabase)
    val groupList: MutableLiveData<MutableList<BasicGroupData>> = MutableLiveData()
    val v = viewModelScope.launch {
        groupDatabase.getGroupDao().getAllGroup().collect {
            withContext(Dispatchers.Main) {
                groupList.value = it
            }
        }
    }

    suspend fun searchGroupData(queryText: String): List<BasicGroupData> {
        return groupRepository.searchGroup(queryText)
    }

    companion object {
        private const val TAG = "DashboardViewModel"
    }

    init {
        retrieveGroup()
    }

    private fun retrieveGroup() {
        viewModelScope.launch(Dispatchers.IO) {
            val currentUserEntity = currentUserDatabase.getCurrentUserDao().getCurrentUser()
            groupRepository.retrieveGroupsRDatabase(currentUserEntity)
        }
    }

    override fun onCleared() {
        super.onCleared()
        groupRepository.deRegister()
        System.gc()
    }

}