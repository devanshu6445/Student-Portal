package com.college.portal.studentportal

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.college.portal.studentportal.adapter.StudentAdapter
import com.college.portal.studentportal.databinding.SpecificStudentAttendanceFragmentBinding
import com.college.portal.studentportal.roomDatabase.user.CurrentUserDatabase
import com.college.portal.studentportal.ui.notifications.attendanceFragment.SpecificStudentSharedVieModelFactory
import com.college.portal.studentportal.ui.notifications.attendanceFragment.StudentForAttendanceViewModel
import com.college.portal.studentportal.ui.notifications.attendanceFragment.StudentForAttendanceViewModelFactory

class SpecificStudentAttendanceFragment : Fragment() {

    private lateinit var viewModel: SpecificStudentAttendanceViewModel
    private val args:SpecificStudentAttendanceFragmentArgs by navArgs()
    private var recyclerView: RecyclerView? = null
    private var binding:SpecificStudentAttendanceFragmentBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        val database = CurrentUserDatabase.getDatabase(activity?.applicationContext!!,"studentDatabase")
        viewModel = ViewModelProvider(this,SpecificStudentSharedVieModelFactory(args.subject.subSem,args.subject.subCourse,database))[SpecificStudentAttendanceViewModel::class.java]
        binding = SpecificStudentAttendanceFragmentBinding.inflate(inflater,container,false)
        recyclerView = binding?.studentRV
        recyclerView?.layoutManager = WrapContentLinearLayoutManager(activity?.applicationContext!!)
        val adapter = StudentAdapter(args.subject)
        recyclerView?.adapter = adapter
        viewModel.studentList.observe(viewLifecycleOwner){
            adapter.updateList(it)
        }
        binding?.attendanceToolbar?.setNavigationOnClickListener {
            it.findNavController().popBackStack()
        }
        return binding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        recyclerView?.adapter = null
        recyclerView = null
        binding = null
    }
}