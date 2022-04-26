package com.college.portal.studentportal.ui.group_participant_details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.college.portal.studentportal.roomDatabase.groupEverything.GroupMessageDatabase
import com.college.portal.studentportal.roomDatabase.groupEverything.Participant
import com.college.portal.studentportal.roomDatabase.groups.BasicGroupData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GroupDetailsViewModel(private val database: GroupMessageDatabase,private val groupData: BasicGroupData) : ViewModel() {

    private val groupDetailsRepository = GroupDetailsRepository.createRepository(groupData)
    private val _participantList = MutableLiveData<List<Participant>>()
    val participantList: LiveData<List<Participant>> = _participantList
    init {
        viewModelScope.launch {
            groupDetailsRepository.getParticipants().collect {
                withContext(Dispatchers.Main){
                    _participantList.value = it
                }
            }
        }
        loadParticipantList()
    }

    private fun loadParticipantList() {
        groupDetailsRepository.loadList(groupData)
    }

    override fun onCleared() {
        try {
            groupDetailsRepository.removeListeners()
        } catch (e: UninitializedPropertyAccessException) {
            e.printStackTrace()
        }
    }
}