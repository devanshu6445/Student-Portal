package com.college.portal.studentportal.ui.dashboard

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.college.portal.studentportal.R
import com.college.portal.studentportal.WrapContentLinearLayoutManager
import com.college.portal.studentportal.adapter.GroupAdapter
import com.college.portal.studentportal.databinding.FragmentDashboardBinding
import com.college.portal.studentportal.roomDatabase.announcement.AnnouncementDatabase
import com.college.portal.studentportal.roomDatabase.groups.GroupDatabase
import com.college.portal.studentportal.roomDatabase.user.CurrentUserDatabase
import kotlinx.coroutines.*

class DashboardFragment : Fragment() {

    private lateinit var dashboardViewModel: DashboardViewModel
    private var _binding: FragmentDashboardBinding? = null
    private lateinit var recyclerViewGroupAdapter: GroupAdapter
    private var recyclerView: RecyclerView? = null
    private val ioScope = CoroutineScope(Dispatchers.IO + Job())

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dashboardViewModel = ViewModelProvider(
            this, DashboardViewModelFactory(
                CurrentUserDatabase.getDatabase(activity?.applicationContext!!),
                GroupDatabase.getInstance(activity?.applicationContext!!),
                AnnouncementDatabase.getDatabase(activity?.applicationContext!!),
                CurrentUserDatabase.getDatabase(activity?.applicationContext!!,"studentDatabase")
            )
        )[DashboardViewModel::class.java]
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        recyclerView = root.findViewById(R.id.groupRecyclerView)
        recyclerView?.setHasFixedSize(true)
        recyclerViewGroupAdapter = context?.let { GroupAdapter(it) }!!
        val layoutManager = WrapContentLinearLayoutManager(activity?.applicationContext!!)
        recyclerView?.layoutManager = layoutManager
        recyclerView?.adapter = recyclerViewGroupAdapter
        binding.searchGroups.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //no need right now
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                //no need right now
            }

            override fun afterTextChanged(s: Editable?) {
                if (binding.searchGroups.text.isEmpty()) {
                    dashboardViewModel.groupList.value?.let { recyclerViewGroupAdapter.updateList(it) }
                } else {
                    ioScope.launch {
                        val list =
                            dashboardViewModel.searchGroupData(binding.searchGroups.text.toString())
                        recyclerViewGroupAdapter.updateList(list)
                    }
                }
            }
        })
        dashboardViewModel.groupList.observe(viewLifecycleOwner) {
            recyclerViewGroupAdapter.updateList(it)
        }
        binding.closeHomeAnnouncement.setOnClickListener {
            val animation = AnimationUtils.loadAnimation(activity?.applicationContext,R.anim.slide_up)
            binding.homeAnnouncement.startAnimation(animation)
            binding.homeAnnouncement.visibility = View.GONE
        }
        lifecycleScope.launch {
            check()
        }
        binding.homeAnnouncement.setOnClickListener {
            root.findNavController().navigate(R.id.action_navigation_dashboard_to_announcementInfoFragment)
        }
        dashboardViewModel.latestAnnouncement.observe(viewLifecycleOwner){
            if(it != null){
                binding.apply {
                    homeAnnouncement.visibility = View.VISIBLE
                    homeAnnouncementText.text = it.announcementText
                    homeAnnouncementSentBy.text = it.announcementCreator
                }
            }else{
                binding.homeAnnouncement.visibility = View.GONE
            }
        }


        binding.allAnnouncements.setOnClickListener {
            try {
                val action = DashboardFragmentDirections.actionNavigationDashboardToAllAnnouncementFragment(dashboardViewModel.loggedInUser)
                it.findNavController().navigate(action)
            } catch (e: UninitializedPropertyAccessException) {
                Toast.makeText(activity?.applicationContext, e.message, Toast.LENGTH_SHORT).show()
            }
        }
        binding.assignments.setOnClickListener {
            val action = DashboardFragmentDirections.actionNavigationDashboardToMyAssignmentsFragment(dashboardViewModel.loggedInUser)
            it.findNavController().navigate(action)
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        recyclerView?.adapter = null
        recyclerView = null
    }
    private suspend fun check(){
        try {
            when(dashboardViewModel.loggedInUser.userDesignation){
                "teacher","admin","HOD" -> {
                    binding.assignments.visibility = View.GONE
                    return
                }
                "moderator","student" -> {
                    //do the work here for different types of user

                    return
                }
            }
        }catch (e:UninitializedPropertyAccessException){
            delay(1000)
            check()
        }
    }

}