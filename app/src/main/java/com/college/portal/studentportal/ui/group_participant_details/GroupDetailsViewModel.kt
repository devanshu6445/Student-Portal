package com.college.portal.studentportal.ui.group_participant_details

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.college.portal.studentportal.callback.FirebasePFCompleteListener
import com.college.portal.studentportal.roomDatabase.groups.BasicGroupData
import com.college.portal.studentportal.roomDatabase.groupEverything.Participant

class GroupDetailsViewModel(private val groupData: BasicGroupData): ViewModel(), FirebasePFCompleteListener {

    private val groupDetailsRepository = GroupDetailsRepository.getRepository()

    private val _participantList = MutableLiveData<MutableList<Participant>>()

    val participantList: LiveData<MutableList<Participant>> = _participantList

    init {
        loadParticipantList()
    }

    private fun loadParticipantList(){
        groupDetailsRepository.loadList(this, groupData)
    }

    override fun onComplete(participantList: MutableList<Participant>) {
        _participantList.value = participantList
    }

    override fun onFailure() {
        Log.e("PF error occurred","Error")
    }


}