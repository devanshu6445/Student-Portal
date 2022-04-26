package com.college.portal.studentportal.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.college.portal.studentportal.R
import com.college.portal.studentportal.roomDatabase.groups.BasicGroupData
import com.college.portal.studentportal.ui.dashboard.GroupDiffUtilCallback
import com.college.portal.studentportal.ui.group_message.GroupMessageActivity
import kotlinx.coroutines.*

class GroupAdapter(var context:Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var groupList = mutableListOf<BasicGroupData>()
    private val ioScope = CoroutineScope(Dispatchers.IO + Job())
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val groupView = LayoutInflater.from(parent.context).inflate(R.layout.groups_item,parent,false)
        return GroupViewHolder(groupView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val groupData = groupList[position]
        val groupViewHolder:GroupViewHolder = holder as GroupViewHolder
        groupViewHolder.groupName.text = groupData.groupName
        Glide.with(context)
            .load(groupData.groupImage)
            .error(R.drawable.googleg_standard_color_18)
            .into(groupViewHolder.groupImage)
        groupPurpose(groupViewHolder.groupPurpose, groupData)
        lastGroupMessage(groupViewHolder.groupLastPost,groupViewHolder.groupLastPostTime,groupData)
        holder.itemView.setOnClickListener {
            context.startActivity(Intent(context, GroupMessageActivity::class.java).apply {
                putExtra("group-data",groupData)
                putExtra("start-flag",true)
            })
        }
    }

    override fun getItemCount(): Int {
        return groupList.size
    }

    private fun lastGroupMessage(lastPost:TextView,
                                 lastPostTime: TextView,
                                 groupDataToLoad: BasicGroupData
    ){
        //TODO: Not yet implemented
    }
    private fun groupPurpose(groupPurpose:TextView,basicGroupData: BasicGroupData){
        if (basicGroupData.groupPurpose == "college")
            groupPurpose.text = groupPurpose.context.getString(R.string.purpose_college)
        else
            groupPurpose.text = groupPurpose.context.getString(R.string.purpose_student)

    }

    fun updateList(groupUpdateList: List<BasicGroupData>){
        val obj = this
        ioScope.launch {
            val groupDiffUtilCallback = GroupDiffUtilCallback(groupList,groupUpdateList)
            val result = DiffUtil.calculateDiff(groupDiffUtilCallback)
            groupList.clear()
            groupList.addAll(groupUpdateList)
            withContext(Dispatchers.Main){
                result.dispatchUpdatesTo(obj)
            }
        }
    }

    class GroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val groupImage : ImageView = itemView.findViewById(R.id.group_image)
        val groupName: TextView = itemView.findViewById(R.id.group_name)
        val groupLastPost : TextView = itemView.findViewById(R.id.lastPost)
        val groupPurpose : TextView = itemView.findViewById(R.id.groupPurpose)
        val groupLastPostTime : TextView = itemView.findViewById(R.id.lastPostTime)

    }

}
