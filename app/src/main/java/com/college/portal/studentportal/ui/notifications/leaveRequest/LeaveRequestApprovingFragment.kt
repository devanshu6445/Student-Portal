package com.college.portal.studentportal.ui.notifications.leaveRequest

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.college.portal.studentportal.WrapContentLinearLayoutManager
import com.college.portal.studentportal.adapter.LeaveRequestApprovingAdapter
import com.college.portal.studentportal.databinding.LeaveRequestApprovingFragmentBinding
import com.college.portal.studentportal.roomDatabase.leaveRequest.LeaveDatabase
import com.college.portal.studentportal.roomDatabase.user.CurrentUserDatabase

class LeaveRequestApprovingFragment : Fragment() {

    private lateinit var viewModel: LeaveRequestApprovingViewModel
    private var binding: LeaveRequestApprovingFragmentBinding? = null
    private val args by navArgs<LeaveRequestApprovingFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LeaveRequestApprovingFragmentBinding.inflate(inflater, container, false)
        val database = CurrentUserDatabase.getDatabase(activity?.applicationContext!!,"studentDatabase")
        viewModel = ViewModelProvider(
            this,
            LeaveRequestApprovingViewModelFactory(
                LeaveDatabase.getDatabase(activity?.applicationContext!!),
                args.cUser
            )
        )[LeaveRequestApprovingViewModel::class.java]
        val recyclerView = binding?.leaveRequestApproveRV
        recyclerView?.layoutManager = WrapContentLinearLayoutManager(activity?.applicationContext!!)
        val adapter = LeaveRequestApprovingAdapter(database)
        recyclerView?.adapter = adapter
        viewModel.leaveRequestList.observe(viewLifecycleOwner){
            adapter.updateList(it)
        }
        adapter.approved.observe(viewLifecycleOwner){
            if(it) adapter.requestToUpload?.let { it1 -> viewModel.leaveRequestUpdate(it1) }
        }
        adapter.declined.observe(viewLifecycleOwner){
            if(it) adapter.requestToUpload?.let { it1 -> viewModel.leaveRequestUpdate(it1) }
        }
        return binding?.root
    }

}