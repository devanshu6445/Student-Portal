package com.college.portal.studentportal.ui.notifications.myAttendance

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.college.portal.studentportal.WrapContentLinearLayoutManager
import com.college.portal.studentportal.adapter.SubjectAdapter
import com.college.portal.studentportal.databinding.AttendanceFragmentBinding

class SelectSubjectFragment: Fragment() {

    private var binding: AttendanceFragmentBinding? = null
    private var recyclerView:RecyclerView? = null
    private lateinit var viewModel:SelectSubjectViewModel
    private val args:SelectSubjectFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = AttendanceFragmentBinding.inflate(inflater,container,false)
        viewModel = ViewModelProvider(this,SelectSubjectViewModelFactory(args.currentUser))[SelectSubjectViewModel::class.java]
        val view = binding?.root
        binding?.attendanceToolbar?.title = "Select Subject"
        binding?.linearLayout2?.visibility = View.GONE
        recyclerView = binding?.subjectRV
        recyclerView?.layoutManager = WrapContentLinearLayoutManager(activity?.applicationContext!!)
        val subjectAdapter = SubjectAdapter()
        recyclerView?.adapter = subjectAdapter
        viewModel.subjectList.observe(viewLifecycleOwner){
            subjectAdapter.updateList(it)
        }
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
        recyclerView?.adapter = null
        recyclerView = null
    }
}