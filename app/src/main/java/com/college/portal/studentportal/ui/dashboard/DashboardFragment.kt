package com.college.portal.studentportal.ui.dashboard

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.college.portal.studentportal.R
import com.college.portal.studentportal.WrapContentLinearLayoutManager
import com.college.portal.studentportal.adapter.GroupAdapter
import com.college.portal.studentportal.databinding.FragmentDashboardBinding
import com.college.portal.studentportal.roomDatabase.groups.GroupDatabase
import com.college.portal.studentportal.roomDatabase.user.CurrentUserDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class DashboardFragment : Fragment() {

    private lateinit var dashboardViewModel: DashboardViewModel
    private var _binding: FragmentDashboardBinding? = null
    private lateinit var recyclerViewGroupAdapter: GroupAdapter
    private lateinit var recyclerView: RecyclerView
    private val ioScope = CoroutineScope(Dispatchers.IO + Job())

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    companion object {
        private const val TAG = "DashboardFragment: "
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val preferences: SharedPreferences? = activity?.let {
            it.applicationContext.getSharedPreferences("userData", Context.MODE_PRIVATE)
        }

        dashboardViewModel = ViewModelProvider(
            this, DashboardViewModelFactory(
                preferences,
                CurrentUserDatabase.getDatabase(activity?.applicationContext!!),
                GroupDatabase.getInstance(activity?.applicationContext!!)
            )
        )
            .get(DashboardViewModel::class.java)
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        recyclerView = root.findViewById(R.id.groupRecyclerView)
        recyclerView.setHasFixedSize(true)
        recyclerViewGroupAdapter = context?.let { GroupAdapter(it) }!!
        val layoutManager = WrapContentLinearLayoutManager(activity?.applicationContext!!)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = recyclerViewGroupAdapter
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

        binding.homeAnnouncement.setOnClickListener {
            root.findNavController().navigate(R.id.action_navigation_dashboard_to_announcementInfoFragment)
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}