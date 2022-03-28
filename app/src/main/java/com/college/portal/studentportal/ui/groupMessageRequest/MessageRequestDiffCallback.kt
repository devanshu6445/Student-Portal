package com.college.portal.studentportal.ui.groupMessageRequest

import androidx.recyclerview.widget.DiffUtil
import com.college.portal.studentportal.roomDatabase.groupEverything.MessageRequest

class MessageRequestDiffCallback(
    private val mOldMessageRequestList: List<MessageRequest>,
    private val mNewMessageRequestList: List<MessageRequest>
    ): DiffUtil.Callback() {


    override fun getOldListSize(): Int {
        return mOldMessageRequestList.size
    }

    override fun getNewListSize(): Int {
        return mNewMessageRequestList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return mOldMessageRequestList[oldItemPosition].messageID == mNewMessageRequestList[newItemPosition].messageID
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldMessageRequest = mOldMessageRequestList[oldItemPosition]
        val newMessageRequest = mNewMessageRequestList[newItemPosition]

        return oldMessageRequest.textMessage == newMessageRequest.textMessage
    }

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        return mNewMessageRequestList[newItemPosition]
    }
}