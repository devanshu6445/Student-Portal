package com.college.portal.studentportal.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.college.portal.studentportal.R
import com.college.portal.studentportal.WrapContentLinearLayoutManager
import com.college.portal.studentportal.roomDatabase.announcement.AnnouncementEntity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.lang.StringBuilder

class AnnouncementAdapter(private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private val announcementList = mutableListOf<AnnouncementEntity>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.announcement_item,parent,false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder = holder as ViewHolder
        val announcement = announcementList[position]
        viewHolder.announcementText.text = announcement.announcementText
        if(announcement.announcementUrl!= null){
            viewHolder.announcementAttachment.visibility  = View.VISIBLE
            viewHolder.announcementAttachment.setOnClickListener {
                showMediaDownloadDialog(announcement.announcementUrl)
            }
        }else{
            viewHolder.announcementAttachment.visibility = View.GONE
        }
        when(announcement.announcementType){
            "Universal" -> {
                val annType = StringBuilder("Announcement for everyone").toString()
                viewHolder.announcementTitle.text = annType
                viewHolder.announcementType.text = announcement.announcementType
            }
            "Course" ->{
                val annText = StringBuilder("Announcement for").append(announcement.announcementCourse)
                viewHolder.announcementTitle.text = annText
                viewHolder.announcementType.text = announcement.announcementType
            }
            "Class" ->{
                val annText = StringBuilder("Announcement for section ").append(announcement.announcementClass).append("of").append(announcement.announcementCourse).toString()
                viewHolder.announcementTitle.text = annText
                viewHolder.announcementType.text = announcement.announcementType
            }
        }

    }

    override fun getItemCount(): Int {
        return announcementList.size
    }

    private fun showMediaDownloadDialog(url:String) {

        val dialog = MaterialAlertDialogBuilder(context,R.style.ThemeOverlay_App_MaterialAlertDialog).setView(R.layout.assignment_media_layout).show()

        val mediaRV = dialog.findViewById<RecyclerView>(R.id.assignment_mediaRV)
        mediaRV?.layoutManager = WrapContentLinearLayoutManager(context)
        mediaRV?.adapter = AssignmentMediaDownloadAdapter(url,context)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(announcementList: List<AnnouncementEntity>){
        this.announcementList.clear()
        this.announcementList.addAll(announcementList)
        notifyDataSetChanged()
    }
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val announcementText:TextView = itemView.findViewById(R.id.announcementTText)
        val announcementTitle:TextView  = itemView.findViewById(R.id.announcementText)
        val announcementType:TextView = itemView.findViewById(R.id.announcementType)
        val announcementAttachment:ImageButton = itemView.findViewById(R.id.announcementAttachment)
    }
}