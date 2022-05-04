package com.college.portal.studentportal.ui.notifications.attendanceFragment

import android.os.Build
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.college.portal.studentportal.R
import com.college.portal.studentportal.adapter.StudentAttendanceAdapter
import com.college.portal.studentportal.databinding.StudentForAttendanceFragmentBinding
import com.college.portal.studentportal.roomDatabase.user.CurrentUserDatabase

class StudentForAttendanceFragment : Fragment() {

    companion object {
        fun newInstance() = StudentForAttendanceFragment()
    }

    private lateinit var viewModel: StudentForAttendanceViewModel
    private lateinit var binding:StudentForAttendanceFragmentBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var studentAttendanceAdapter: StudentAttendanceAdapter
    private val args: StudentForAttendanceFragmentArgs by navArgs()
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val database = CurrentUserDatabase.getDatabase(activity?.applicationContext!!,"studentDatabase")
        viewModel = ViewModelProvider(this,StudentForAttendanceViewModelFactory(args.subject.subSem,args.subject.subCourse,database))[StudentForAttendanceViewModel::class.java]
        val binding = StudentForAttendanceFragmentBinding.inflate(inflater, container, false)
        val view = binding.root
        recyclerView = binding.studentLRV
        layoutManager = LinearLayoutManager(activity?.applicationContext)
        studentAttendanceAdapter = context?.let { StudentAttendanceAdapter(it,args.subject.subCode) }!!
        recyclerView.layoutManager = this.layoutManager
        recyclerView.adapter = studentAttendanceAdapter

        viewModel.studentList.observe(viewLifecycleOwner){
            studentAttendanceAdapter.updateList(it)
        }
        binding.submitAttendance.setOnClickListener {
            viewModel.submit(studentAttendanceAdapter.absentStudentList,args.subject.subCode)
        }
        return view
    }


}