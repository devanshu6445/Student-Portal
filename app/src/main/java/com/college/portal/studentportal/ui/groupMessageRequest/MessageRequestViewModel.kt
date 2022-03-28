package com.college.portal.studentportal.ui.groupMessageRequest

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.college.portal.studentportal.roomDatabase.groupEverything.GroupMessageDatabase
import com.college.portal.studentportal.roomDatabase.groupEverything.MessageRequest
import com.college.portal.studentportal.roomDatabase.groups.BasicGroupData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MessageRequestViewModel(private val database: GroupMessageDatabase,
                              private val basicGroupData: BasicGroupData): ViewModel() {
    private val messageRequestRepository = MessageRequestRepository(database,basicGroupData)
    private val _messageRequestList = MutableLiveData<List<MessageRequest>>()
    val messageRequestList: LiveData<List<MessageRequest>> = _messageRequestList

    init {
        viewModelScope.launch(Dispatchers.IO) {
            messageRequestRepository.getAllMessageRequests().collect {
                withContext(Dispatchers.Main){
                    _messageRequestList.value = it
                }
            }
        }
        messageRequestRepository.registerMessageRequestListener()
    }

    override fun onCleared() {
        super.onCleared()
        messageRequestRepository.deregisterMessageRequestListener()
    }
}