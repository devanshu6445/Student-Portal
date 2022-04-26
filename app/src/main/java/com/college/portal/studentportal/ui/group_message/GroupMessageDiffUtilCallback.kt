package com.college.portal.studentportal.ui.group_message

import androidx.recyclerview.widget.DiffUtil
import com.college.portal.studentportal.roomDatabase.groupEverything.GroupMessageInfo

class GroupMessageDiffUtilCallback(private val mOldLMessageList: List<GroupMessageInfo>,
                                   private val mNewMessageList:List<GroupMessageInfo>): DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return mOldLMessageList.size
    }

    override fun getNewListSize(): Int {
        return mNewMessageList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return mOldLMessageList[oldItemPosition].messageID == mNewMessageList[newItemPosition].messageID
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return mOldLMessageList[oldItemPosition].messageTimeStamp == mNewMessageList[newItemPosition].messageTimeStamp &&
                mOldLMessageList[oldItemPosition].textMessage == mNewMessageList[newItemPosition].textMessage &&
                mOldLMessageList[oldItemPosition].docURL == mNewMessageList[newItemPosition].docURL
    }

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        return mNewMessageList[newItemPosition]
    }
}