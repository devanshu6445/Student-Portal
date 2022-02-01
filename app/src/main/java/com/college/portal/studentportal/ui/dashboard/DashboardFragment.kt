package com.college.portal.studentportal.ui.dashboard

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.college.portal.studentportal.R
import com.college.portal.studentportal.adapter.GroupAdapter
import com.college.portal.studentportal.databinding.FragmentDashboardBinding

class DashboardFragment : Fragment() {

    private lateinit var dashboardViewModel: DashboardViewModel
    private var _binding: FragmentDashboardBinding? = null
    private lateinit var recyclerViewGroupAdapter: GroupAdapter
    private lateinit var recyclerView: RecyclerView

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val preferences: SharedPreferences? = activity?.let {
            it.applicationContext.getSharedPreferences("userData",Context.MODE_PRIVATE)
        }

        dashboardViewModel = ViewModelProvider(this, DashboardViewModelFactory(preferences))
            .get(DashboardViewModel::class.java)
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        recyclerView = root.findViewById<RecyclerView>(R.id.groupRecyclerView)
        recyclerView.setHasFixedSize(true)
        recyclerViewGroupAdapter = context?.let { GroupAdapter(it) }!!
        val layoutManager = LinearLayoutManager(activity?.applicationContext)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = recyclerViewGroupAdapter

        dashboardViewModel.groupList.observe(viewLifecycleOwner,{
            recyclerViewGroupAdapter.updateList(it)
        })

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}