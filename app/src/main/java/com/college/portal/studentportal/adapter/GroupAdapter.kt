package com.college.portal.studentportal.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.college.portal.studentportal.ui.group_message.GroupMessageActivity
import com.college.portal.studentportal.R
import com.college.portal.studentportal.data.model.BasicGroupData

class GroupAdapter(var context:Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var groupList = mutableListOf<BasicGroupData>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val groupView = LayoutInflater.from(parent.context).inflate(R.layout.groups_item,parent,false)
        return GroupViewHolder(groupView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val groupData = groupList[position]
        val groupViewHolder:GroupViewHolder = holder as GroupViewHolder
        groupViewHolder.groupName.text = groupData.groupName
        /*Glide.with(context)
                .load(groupData.groupImage)
                .into(groupViewHolder.groupImage)*/
        groupPurpose(groupViewHolder.groupPurpose, groupData)
        lastGroupMessage(groupViewHolder.groupLastPost,groupViewHolder.groupLastPostTime,groupData)
        holder.itemView.setOnClickListener {
            context.startActivity(Intent(context, GroupMessageActivity::class.java))
        }
    }

    override fun getItemCount(): Int {
        return groupList.size
    }

    private fun lastGroupMessage(lastPost:TextView,
                                 lastPostTime: TextView,
                                 groupDataToLoad:BasicGroupData
    ){
        //TODO: Not yet implemented
    }
    private fun groupPurpose(groupPurpose:TextView,basicGroupData: BasicGroupData){
        if (basicGroupData.groupPurpose.equals("college"))
            groupPurpose.text = groupPurpose.context.getString(R.string.purpose_college)
        else
            groupPurpose.text = groupPurpose.context.getString(R.string.purpose_student)

    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(groupUpdateList: MutableList<BasicGroupData>){
        groupList.clear()
        groupList = groupUpdateList
        notifyDataSetChanged()
    }

    class GroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val groupImage : ImageView = itemView.findViewById<ImageView>(R.id.group_image)
        val groupName: TextView = itemView.findViewById<TextView>(R.id.group_name)
        val groupLastPost : TextView = itemView.findViewById<TextView>(R.id.lastPost)
        val groupPurpose : TextView = itemView.findViewById<TextView>(R.id.groupPurpose)
        val groupLastPostTime : TextView = itemView.findViewById<TextView>(R.id.lastPostTime)

    }

}
