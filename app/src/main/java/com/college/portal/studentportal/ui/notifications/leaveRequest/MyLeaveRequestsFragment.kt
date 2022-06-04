package com.college.portal.studentportal.ui.notifications.leaveRequest

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.college.portal.studentportal.WrapContentLinearLayoutManager
import com.college.portal.studentportal.adapter.LeaveRequestAdapter
import com.college.portal.studentportal.databinding.MyLeaveRequestsFragmentBinding
import com.college.portal.studentportal.roomDatabase.leaveRequest.LeaveDatabase

class MyLeaveRequestsFragment : Fragment() {

    private lateinit var viewModel: MyLeaveRequestsViewModel
    private val args:MyLeaveRequestsFragmentArgs by navArgs()
    private var binding:MyLeaveRequestsFragmentBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this,MyLeaveRequestsViewModelFactory(LeaveDatabase.getDatabase(activity?.applicationContext!!),args.cUser))[MyLeaveRequestsViewModel::class.java]
        binding = MyLeaveRequestsFragmentBinding.inflate(inflater,container,false)
        val recyclerView = binding?.myLeaveRequestsRV
        recyclerView?.layoutManager = WrapContentLinearLayoutManager(activity?.applicationContext!!)
        val adapter = LeaveRequestAdapter(activity?.applicationContext!!)
        recyclerView?.adapter = adapter
        viewModel.leaveRequestList.observe(viewLifecycleOwner){
            adapter.updateList(it)
        }
        binding?.leaveRequestToolbar?.setNavigationOnClickListener {
            it.findNavController().popBackStack()
        }
        return binding?.root
    }
}