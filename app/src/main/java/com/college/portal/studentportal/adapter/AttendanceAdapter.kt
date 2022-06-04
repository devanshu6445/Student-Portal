package com.college.portal.studentportal.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.college.portal.studentportal.R
import com.college.portal.studentportal.SpecificStudentAttendanceFragmentDirections
import com.college.portal.studentportal.data.model.LoggedInUser
import com.college.portal.studentportal.data.model.Subject
import com.college.portal.studentportal.ui.notifications.attendanceFragment.AttendanceFragmentDirections
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class AttendanceAdapter(private val navCode:Int,private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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
            when(navCode){
                0 ->  {
                    val items = arrayOf("A","B","C","D")
                    MaterialAlertDialogBuilder(context,R.style.ThemeOverlay_App_MaterialAlertDialog)
                        .setItems(items){_,which ->
                            val action = AttendanceFragmentDirections.actionAttendanceFragmentToStudentForAttendanceFragment(subject,items[which])
                            it.findNavController().navigate(action)
                        }.show()
                }
                1 -> {
                    val action = AttendanceFragmentDirections.actionAttendanceFragmentToSpecificStudentAttendanceFragment2(subject)
                    it.findNavController().navigate(action)
                }
            }
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
class StudentAdapter(private val subject: Subject): RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private val studentList = mutableListOf<LoggedInUser>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return StudentAttendanceAdapter.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.groups_item,parent,false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder = holder as StudentAttendanceAdapter.ViewHolder
        val student = studentList[position]
        viewHolder.apply {
            studentName.text = student.userName
        }
        viewHolder.itemView.setOnClickListener {
            val action = SpecificStudentAttendanceFragmentDirections.actionSpecificStudentAttendanceFragmentToMyAttendanceFragment(subject, student.userUid)
            it.findNavController().navigate(action)
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

}


class StudentAttendanceAdapter(private val context: Context,private val subjectCode:String): RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private val studentList = mutableListOf<LoggedInUser>()
    val presentStudentList = mutableListOf<LoggedInUser>()
    //private val attendanceRepository = AttendanceRepository()

    init {
        presentStudentList.addAll(studentList)
    }

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

            if(presentStudentList.contains(student)){
                presentStudentList.remove(student)
                viewHolder.root.setBackgroundColor(context.resources.getColor(R.color.red_btn_bg_color))
            }else{
                presentStudentList.add(student)
                viewHolder.root.setBackgroundColor(Color.WHITE)
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
        presentStudentList.addAll(studentList)
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        val studentName: TextView = itemView.findViewById(R.id.group_name)
        val root:View = itemView.findViewById(R.id.root)
    }
}