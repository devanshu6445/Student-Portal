package com.college.portal.studentportal.ui.notifications.attendanceFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.college.portal.studentportal.R
import com.college.portal.studentportal.adapter.AttendanceAdapter
import com.college.portal.studentportal.databinding.AttendanceFragmentBinding
import com.college.portal.studentportal.extensionFunctions.courses

class AttendanceFragment : Fragment() {

    private lateinit var viewModel: AttendanceViewModel
    private lateinit var binding: AttendanceFragmentBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var attendanceAdapter: AttendanceAdapter
    private lateinit var layoutManager: LinearLayoutManager
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = AttendanceFragmentBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[AttendanceViewModel::class.java]
        val view = binding.root
        recyclerView = binding.subjectRV.apply { setHasFixedSize(true) }
        attendanceAdapter = context?.let { AttendanceAdapter(it) }!!
        layoutManager = LinearLayoutManager(activity?.applicationContext)
        recyclerView.layoutManager = this.layoutManager
        recyclerView.adapter = attendanceAdapter

        val adapter = ArrayAdapter(requireContext(), R.layout.list_item, courses)
        binding.acCourse.setAdapter(adapter)

        val items1 = arrayOf("1","2","3","4","5","6")
        val adapter1 = ArrayAdapter(requireContext(), R.layout.list_item,items1)
        binding.acSem.setAdapter(adapter1)
        binding.acCourse.setOnItemClickListener { _, _, position, _ ->
            val l = viewModel.subjectList.value?.filter { it.subCourse == courses[position] }
            l?.let { attendanceAdapter.updateList(l) }
        }
        binding.acSem.setOnItemClickListener { _, _, position, _ ->
            val l = viewModel.subjectList.value?.filter { it.subSem == items1[position] }
            l?.let { attendanceAdapter.updateList(l) }
        }

        viewModel.subjectList.observe(viewLifecycleOwner){
            attendanceAdapter.updateList(it)
        }
        return view
    }

}