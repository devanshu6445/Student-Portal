package com.college.portal.studentportal.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.college.portal.studentportal.R
import com.college.portal.studentportal.roomDatabase.groupEverything.Participant

class GroupParticipantsAdapter(
    private val context: Context,
    private val participantList: MutableList<Participant>
    ): RecyclerView.Adapter<RecyclerView.ViewHolder>()
{
    private val menuItemClickListener = PopupMenu.OnMenuItemClickListener {

        when(it.itemId){
            R.id.banParticipant ->{ Log.d("banParticipant","Working")}
            R.id.show_profile -> { Log.d("show_profile","Working")}
            R.id.promoteUser -> { Log.d("promoteUser","Working")}
            R.id.demoteUser -> { Log.d("demote","Working")}
        }
        return@OnMenuItemClickListener true
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val participantView = LayoutInflater.from(parent.context).inflate(R.layout.groups_item,parent,false)
        return ParticipantViewHolder(participantView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val participant = participantList[position]
        val participantViewHolder = holder as ParticipantViewHolder

        participantViewHolder.apply {
            groupLastPostTime.visibility = View.GONE
            groupLastPost.visibility = View.GONE
            participantName.text = participant.name
        }

        when(participant.role){
            "admin" -> participantViewHolder.participantRole.text = context.getString(R.string.admin)
            "moderator" -> participantViewHolder.participantRole.text = context.getString(R.string.moderator)
            "teacher" -> participantViewHolder.participantRole.text = context.getString(R.string.teacher)
            else -> participantViewHolder.participantRole.visibility = View.GONE
        }

        Glide.with(participantViewHolder.profileImage.context)
            .load(participant.imageURL)
            .placeholder(R.drawable.ic_profile_placeholder)
            .error(R.drawable.ic_profile_placeholder)
            .into(participantViewHolder.profileImage)

        participantViewHolder.root.setOnClickListener{
            val popupMenu = PopupMenu(context,it)
            if (participant.role == "admin" || participant.role == "teacher"){
                popupMenu.menuInflater.inflate(R.menu.participants_menu_for_teacher,popupMenu.menu)
                popupMenu.setOnMenuItemClickListener(menuItemClickListener)
            } else if (participant.role == "moderator"){
                popupMenu.menuInflater.inflate(R.menu.participant_menu_for_moderator,popupMenu.menu)
                popupMenu.setOnMenuItemClickListener(menuItemClickListener)
            }
        }

    }

    override fun getItemCount(): Int = participantList.size

    fun updateParticipantList(participantList: MutableList<Participant>){
        this.participantList.clear()
        this.participantList.addAll(participantList)
        notifyDataSetChanged()
    }
    class ParticipantViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val profileImage : ImageView = itemView.findViewById<ImageView>(R.id.group_image)
        val participantName: TextView = itemView.findViewById<TextView>(R.id.group_name)
        val groupLastPost : TextView = itemView.findViewById<TextView>(R.id.lastPost)
        val participantRole : TextView = itemView.findViewById<TextView>(R.id.groupPurpose)
        val groupLastPostTime : TextView = itemView.findViewById<TextView>(R.id.lastPostTime)
        val root: View = itemView.rootView
    }
}