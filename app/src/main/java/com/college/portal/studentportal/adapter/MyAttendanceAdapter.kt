package com.college.portal.studentportal.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.college.portal.studentportal.R
import com.college.portal.studentportal.data.model.Subject
import com.college.portal.studentportal.roomDatabase.attendance.Attendance
import com.college.portal.studentportal.ui.notifications.myAttendance.SelectSubjectFragmentDirections
import java.lang.Exception

class MyAttendanceAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val myPresent = mutableListOf<Attendance>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.myattendance_recylerview_item,parent,false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val cAttendance = try {
            myPresent[position]
        }catch (e:Exception){
            null
        }
        val viewHolder = holder as ViewHolder
        val date = position+1
        viewHolder.present.text = date.toString()
        if(cAttendance != null){
            when(cAttendance.isPresent){
                true -> viewHolder.present.setBackgroundColor(Color.GREEN)
                else -> viewHolder.present.setBackgroundColor(Color.RED)
            }
        }else{
            viewHolder.present.setBackgroundColor(Color.BLUE)
        }
    }

    fun updateList(attList : List<Attendance>){
        myPresent.clear()
        myPresent.addAll(attList)
        notifyDataSetChanged()
    }
    override fun getItemCount(): Int {
        return 31
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val present:TextView = itemView.findViewById(R.id.attendance)
    }
}

class SubjectAdapter(private val uid : String): RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private val mySubjectList = mutableListOf<Subject>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return AttendanceAdapter.ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.groups_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder = holder as AttendanceAdapter.ViewHolder
        val subject = mySubjectList[position]
        viewHolder.subName.text = subject.subCode

        viewHolder.itemView.setOnClickListener {
            val action = SelectSubjectFragmentDirections.actionSelectSubjectFragmentToMyAttendanceFragment(subject,uid)
            it.findNavController().navigate(action)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(newList: List<Subject>){
        mySubjectList.clear()
        mySubjectList.addAll(newList)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return mySubjectList.size
    }
}