package com.college.portal.studentportal.ui.group_message

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.college.portal.studentportal.CurrentUserException
import com.college.portal.studentportal.data.model.GroupMessageData
import com.college.portal.studentportal.data.model.LoggedInUser
import com.college.portal.studentportal.roomDatabase.groupEverything.GroupMessageDatabase
import com.college.portal.studentportal.roomDatabase.groupEverything.Participant
import com.college.portal.studentportal.roomDatabase.groups.BasicGroupData
import com.college.portal.studentportal.roomDatabase.user.CurrentUserDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GroupMessageViewModel(private val groupData: BasicGroupData,
                            private  val database: GroupMessageDatabase,
                            private val currentUserDatabase: CurrentUserDatabase
                            ): ViewModel(){

    companion object{
        private const val TAG = "GroupMessageViewModel: "
    }

    private val _groupMessageList =  MutableLiveData<MutableList<GroupMessageData>>()
    private val groupMessageRepository = GroupMessageRepository(database,groupData)
    private var _currentParticipant:Participant? = Participant()
    var currentParticipant:Participant? = _currentParticipant
    val groupMessageList : MutableLiveData<MutableList<GroupMessageData>> = MutableLiveData()

    val messageSent = MutableLiveData<Boolean>()

    private val _groupMessageData = MutableLiveData<GroupMessageData>()

    private val d  = viewModelScope.launch(Dispatchers.IO) {
        database.getGroupMessageDao().getMessages().collect {
            withContext(Dispatchers.Main){
                groupMessageList.value = it
            }
        }
    }

    private val p = viewModelScope.launch {
        groupMessageRepository.getCurrentParticipant().collect {
            currentParticipant = it
        }
    }

    val groupMessageData: LiveData<GroupMessageData> = _groupMessageData
    private var currentUserData: LoggedInUser? = null
    init {
        viewModelScope.launch(Dispatchers.IO) {
            currentUserData = currentUserDatabase.getCurrentUserDao().getCurrentUser()
        }
        viewModelScope.launch {
            populateMessageList()
        }
        viewModelScope.launch {
            groupMessageRepository.registerParticipantListener()
        }
    }

    private fun populateMessageList(){
        groupMessageRepository.retrieveMessages()
    }

    @Throws(CurrentUserException::class)
    fun sendMessage(message:String){
        if(currentUserData!=null){
            viewModelScope.launch(Dispatchers.IO) {
                val t = groupMessageRepository.sendMessage(message, currentUserData!!)
                withContext(Dispatchers.Main){
                    messageSent.value = t
                }
            }
        }else{
            throw CurrentUserException("Current user didn't load")
        }
    }

    fun sendMessage(uri: Uri){
        Log.d("uri-message",uri.path.toString())
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("cleared-viewModel","cleared")
        groupMessageRepository.deRegister()
        System.gc()
    }

}