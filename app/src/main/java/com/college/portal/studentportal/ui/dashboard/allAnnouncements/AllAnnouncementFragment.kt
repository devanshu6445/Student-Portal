package com.college.portal.studentportal.ui.dashboard.allAnnouncements

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.college.portal.studentportal.WrapContentLinearLayoutManager
import com.college.portal.studentportal.adapter.AnnouncementAdapter
import com.college.portal.studentportal.databinding.AllAnnouncementFragmentBinding
import com.college.portal.studentportal.roomDatabase.announcement.AnnouncementDatabase

class AllAnnouncementFragment : Fragment() {



    private lateinit var viewModel: AllAnnouncementViewModel
    private var binding:AllAnnouncementFragmentBinding? = null
    private var recyclerView:RecyclerView? = null
    private var announcementAdapter:AnnouncementAdapter? = null
    private val args:AllAnnouncementFragmentArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val database = AnnouncementDatabase.getDatabase(activity?.applicationContext!!)
        binding = AllAnnouncementFragmentBinding.inflate(inflater,container,false)
        viewModel = ViewModelProvider(this,AllAnnouncementViewModel.AllAnnouncementViewModelFactory(database,args.currentUser))[AllAnnouncementViewModel::class.java]

        recyclerView = binding?.announcementRV
        recyclerView?.layoutManager = WrapContentLinearLayoutManager(activity?.applicationContext!!)
        announcementAdapter = AnnouncementAdapter(requireActivity())
        recyclerView?.adapter = announcementAdapter

        binding?.attendanceToolbar?.setNavigationOnClickListener {
            it.findNavController().popBackStack()
        }
        viewModel.announcementList.observe(viewLifecycleOwner){
            announcementAdapter?.updateList(it)
        }
        return binding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
        announcementAdapter = null
        recyclerView?.adapter = null
        recyclerView = null
    }
}