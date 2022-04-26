package com.college.portal.studentportal.ui.group_message

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.college.portal.studentportal.CurrentUserException
import com.college.portal.studentportal.data.model.LoggedInUser
import com.college.portal.studentportal.roomDatabase.groupEverything.GroupMessageDatabase
import com.college.portal.studentportal.roomDatabase.groupEverything.GroupMessageInfo
import com.college.portal.studentportal.roomDatabase.groupEverything.Participant
import com.college.portal.studentportal.roomDatabase.groups.BasicGroupData
import com.college.portal.studentportal.roomDatabase.user.CurrentUserDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GroupMessageViewModel(
    groupData: BasicGroupData,
    private  val database: GroupMessageDatabase,
    private val currentUserDatabase: CurrentUserDatabase
                            ): ViewModel(){

    companion object{
        private const val TAG = "GroupMessageViewModel: "
    }

    private val groupMessageRepository = GroupMessageRepository(database,groupData)
    private var _currentParticipant:Participant? = Participant()
    var currentParticipant:Participant? = _currentParticipant
    val groupMessageList : MutableLiveData<MutableList<GroupMessageInfo>> = MutableLiveData()
    val messageSent = MutableLiveData<Boolean>()

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

        viewModelScope.launch {

        }
    }

    private fun populateMessageList(){
        groupMessageRepository.updateMessageDatabase()
    }

    @Throws(CurrentUserException::class)
    fun sendMessage(message:String?,uri: Uri?,mimeType:String){
        if(currentUserData!=null){
            viewModelScope.launch(Dispatchers.IO) {
                val sentStatus = groupMessageRepository.sendMessage(message, currentUserData!!,uri,mimeType)
                withContext(Dispatchers.Main){
                    messageSent.value = sentStatus
                }
            }
        }else{
            throw CurrentUserException("Current user didn't load")
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("cleared-viewModel","cleared")
        try {
            groupMessageRepository.deRegister()
        } catch (e: UninitializedPropertyAccessException) {
            Log.e(TAG, "onCleared: ${e.message}", e)
        }
        System.gc()
    }

}