package com.college.portal.studentportal.ui.dashboard

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
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

    private var _groupList: MutableLiveData<MutableList<BasicGroupData>> = MutableLiveData()
    private val groupRepository = GroupRepository()
    val groupList: MutableLiveData<MutableList<BasicGroupData>> = MutableLiveData()
    private val isLoaded: MutableLiveData<Boolean> = MutableLiveData<Boolean>(false)
    val v = viewModelScope.launch {
        groupDatabase.getGroupDao().getAllGroup().collect {
            withContext(Dispatchers.Main){
                groupList.value = it
            }
        }
    }

    companion object{
        private const val TAG = "DashboardViewModel"
    }

    init {
        retrieveGroup()
    }

    private fun loadGroup(){
        viewModelScope.launch(Dispatchers.IO) {
            val groupDao = groupDatabase.getGroupDao()

            withContext(Dispatchers.Main){
                isLoaded.value = true
                Log.d(TAG,"value set ${isLoaded.value}")
            }
        }
    }


    private fun retrieveGroup(){
        viewModelScope.launch(Dispatchers.IO) {
            val currentUserEntity = currentUserDatabase.getCurrentUserDao().getCurrentUser()
            groupRepository.retrieveGroupsRDatabase(database = groupDatabase,currentUserEntity)
        }
    }

    override fun onCleared() {
        super.onCleared()
        groupRepository.deRegister()
        System.gc()
    }

}