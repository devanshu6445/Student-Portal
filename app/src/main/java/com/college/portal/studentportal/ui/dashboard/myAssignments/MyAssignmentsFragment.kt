package com.college.portal.studentportal.ui.dashboard.myAssignments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.college.portal.studentportal.WrapContentLinearLayoutManager
import com.college.portal.studentportal.adapter.AssignmentAdapter
import com.college.portal.studentportal.databinding.MyAssignmentsFragmentBinding
import com.college.portal.studentportal.roomDatabase.assignment.AssignmentDatabase

class MyAssignmentsFragment : Fragment() {

    private lateinit var viewModel: MyAssignmentsViewModel
    private var binding:MyAssignmentsFragmentBinding? = null
    private var recyclerView:RecyclerView? = null
    private val args:MyAssignmentsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this,MyAssignmentViewModelFactory(args.cUser,AssignmentDatabase.getDatabase(activity?.applicationContext!!)))[MyAssignmentsViewModel::class.java]
        binding = MyAssignmentsFragmentBinding.inflate(inflater,container,false)
        recyclerView = binding?.myAssignmentsRV
        recyclerView?.layoutManager = WrapContentLinearLayoutManager(activity?.applicationContext!!)
        val adapter = AssignmentAdapter(requireActivity())
        recyclerView?.adapter = adapter

        binding?.assignmentsToolbar?.setNavigationOnClickListener {
            it.findNavController().popBackStack()
        }
        viewModel.assignmentList.observe(viewLifecycleOwner){
            adapter.updateList(it)
        }
        return binding?.root
    }

}