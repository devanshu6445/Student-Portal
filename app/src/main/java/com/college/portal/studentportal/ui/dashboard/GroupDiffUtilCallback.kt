package com.college.portal.studentportal.ui.dashboard

import androidx.recyclerview.widget.DiffUtil
import com.college.portal.studentportal.roomDatabase.groups.BasicGroupData

class GroupDiffUtilCallback(private val oldList: List<BasicGroupData>,private val newList: List<BasicGroupData>): DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].groupID == newList[newItemPosition].groupID
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].groupName == newList[newItemPosition].groupName
    }

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        return newList[newItemPosition]
    }
}