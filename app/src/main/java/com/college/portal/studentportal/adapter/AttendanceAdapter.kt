package com.college.portal.studentportal.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.college.portal.studentportal.R
import com.college.portal.studentportal.data.model.LoggedInUser
import com.college.portal.studentportal.data.model.Subject
import com.college.portal.studentportal.ui.notifications.attendanceFragment.AttendanceFragmentDirections
import com.college.portal.studentportal.ui.notifications.attendanceFragment.AttendanceRepository

class AttendanceAdapter(private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val subList = mutableListOf<Subject>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.groups_item,parent,false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val subject = subList[position]
        val viewHolder = holder as ViewHolder
        viewHolder.apply {
            subName.text = subject.subCode
        }
        viewHolder.itemView.setOnClickListener {
            val action = AttendanceFragmentDirections.actionAttendanceFragmentToStudentForAttendanceFragment(subject)
            it.findNavController().navigate(action)
        }
    }

    override fun getItemCount(): Int {
        return subList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(subjectList: List<Subject>){
        subList.clear()
        subList.addAll(subjectList)
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        val subName: TextView = itemView.findViewById(R.id.group_name)
    }
}

class StudentAttendanceAdapter(private val context: Context,private val subjectCode:String): RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private val studentList = mutableListOf<LoggedInUser>()
    val absentStudentList = mutableListOf<LoggedInUser>()
    private val attendanceRepository = AttendanceRepository()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.groups_item,parent,false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val student = studentList[position]
        val viewHolder = holder as ViewHolder
        viewHolder.apply {
            studentName.text = student.userName
        }
        viewHolder.itemView.setOnClickListener {
            if (absentStudentList.contains(student)){
                absentStudentList.remove(student)
                viewHolder.root.setBackgroundColor(Color.WHITE)
                Toast.makeText(context, "${student.userName} will be marked as present", Toast.LENGTH_SHORT).show()
            }else{
                absentStudentList.add(student)
                viewHolder.root.setBackgroundColor(Color.RED)
                Toast.makeText(context, "${student.userName} will be marked as absent", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getItemCount(): Int {
        return studentList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(stuList: List<LoggedInUser>){
        studentList.clear()
        studentList.addAll(stuList)
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        val studentName: TextView = itemView.findViewById(R.id.group_name)
        val root:View = itemView.findViewById(R.id.root)
    }
}