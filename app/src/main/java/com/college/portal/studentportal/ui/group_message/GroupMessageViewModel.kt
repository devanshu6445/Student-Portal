package com.college.portal.studentportal.ui.group_message

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.college.portal.studentportal.callback.FirebaseGMDFCompleteListener
import com.college.portal.studentportal.data.model.GroupMessageData

class GroupMessageViewModel: ViewModel(), FirebaseGMDFCompleteListener{

    companion object{
        private val TAG = "GroupMessageViewModel: "
    }

    private val _groupMessageList =  MutableLiveData<MutableList<GroupMessageData>>()
    private val groupMessageRepository = GroupMessageRepository.getInstance()

    val groupMessageList : LiveData<MutableList<GroupMessageData>> = _groupMessageList

    init {
        populateMessageList()
    }

    private fun populateMessageList(){
        groupMessageRepository.retrieveMessages(this)
    }

    override fun onComplete(groupMessageList: MutableList<GroupMessageData>) {
        this._groupMessageList.value = groupMessageList
        groupMessageList.forEach {
            Log.d("messageList",it.senderName)
        }

    }

    override fun onFailure() {
        Log.d(TAG, "onFailure: for Error check error log")
    }

    fun sendMessage(message:String){
        groupMessageRepository.sendMessage(message)
    }

}