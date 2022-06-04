package com.college.portal.studentportal.ui.notifications.attendanceFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.college.portal.studentportal.R
import com.college.portal.studentportal.adapter.AttendanceAdapter

import com.college.portal.studentportal.databinding.AttendanceFragmentBinding

import com.college.portal.studentportal.extensionFunctions.courses
import com.college.portal.studentportal.roomDatabase.subjectDatabase.SubjectDatabase

class AttendanceFragment : Fragment() {

    private lateinit var viewModel: AttendanceViewModel
    private val args:AttendanceFragmentArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding:AttendanceFragmentBinding? = AttendanceFragmentBinding.inflate(inflater, container, false)

        viewModel = ViewModelProvider(this,AttendanceViewModelFactory(SubjectDatabase.getDatabase(activity?.applicationContext!!)))[AttendanceViewModel::class.java]
        val view = binding?.root
        val recyclerView:RecyclerView? = binding?.subjectRV?.apply { setHasFixedSize(true) }
        val attendanceAdapter = AttendanceAdapter(args.navigationCode, requireActivity())
        val layoutManager = LinearLayoutManager(activity?.applicationContext)
        recyclerView?.layoutManager = layoutManager
        recyclerView?.adapter = attendanceAdapter

        val adapter = ArrayAdapter(requireContext(), R.layout.list_item, courses)
        binding?.acCourse?.setAdapter(adapter)

        val items1 = arrayOf("1","2","3","4","5","6")
        val adapter1 = ArrayAdapter(requireContext(), R.layout.list_item,items1)
        binding?.acSem?.setAdapter(adapter1)
        binding?.acCourse?.setOnItemClickListener { _, _, position, _ ->
            val l = viewModel.subjectList.value?.filter { it.subCourse == courses[position] }
            l?.let { attendanceAdapter.updateList(l) }
        }
        binding?.acSem?.setOnItemClickListener { _, _, position, _ ->
            val l = viewModel.subjectList.value?.filter { it.subSem == items1[position] }
            l?.let { attendanceAdapter.updateList(l) }
        }
        binding?.attendanceToolbar?.setNavigationOnClickListener {
            it.findNavController().popBackStack()
        }
        viewModel.subjectList.observe(viewLifecycleOwner){
            attendanceAdapter.updateList(it)
        }
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}