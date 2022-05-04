package com.college.portal.studentportal.ui.notifications.announcement

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import com.college.portal.studentportal.R
import com.college.portal.studentportal.data.model.Announcement
import com.college.portal.studentportal.databinding.PostAnnouncementFragmentBinding
import com.college.portal.studentportal.extensionFunctions.announcementType
import com.college.portal.studentportal.extensionFunctions.classes
import com.college.portal.studentportal.extensionFunctions.courses

class PostAnnouncement : Fragment() {

    private lateinit var viewModel: PostAnnouncementViewModel
    private var binding:PostAnnouncementFragmentBinding? = null
    private val announcement = Announcement()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = PostAnnouncementFragmentBinding.inflate(inflater,container,false)
        viewModel = ViewModelProvider(this)[PostAnnouncementViewModel::class.java]

        val announcementTypeAdapter = ArrayAdapter(requireContext(), R.layout.list_item,
            announcementType)
        binding?.announcementType?.setAdapter(announcementTypeAdapter)

        val announcementCourseAdapter = ArrayAdapter(requireContext(),R.layout.list_item, courses)
        binding?.announcementCourse?.setAdapter(announcementCourseAdapter)

        val announcementClassAdapter = ArrayAdapter(requireContext(),R.layout.list_item, classes)
        binding?.announcementCCC?.setAdapter(announcementClassAdapter)

        binding?.postTheAnnouncement?.setOnClickListener {
            announcement.announcementText = binding?.announcementTT?.text.toString()
            Toast.makeText(activity?.applicationContext, announcement.announcementText, Toast.LENGTH_SHORT).show()
            viewModel.postAnnouncement(announcement)
        }
        viewModel.uploadStatus.observe(viewLifecycleOwner){
            when(it){
                1 -> {
                    Toast.makeText(activity?.applicationContext, "Announcement uploaded.", Toast.LENGTH_SHORT).show()
                }
                2 -> {
                    Toast.makeText(activity?.applicationContext, viewModel.exception.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
        binding?.announcementType?.setOnItemClickListener { _, _, position, _ ->
            announcement.announcementType = announcementType[position]
            when(position){
                1 -> {
                    announcement.announcementCourse = binding?.announcementCourse?.text.toString()
                    announcement.announcementClass = null
                    binding?.menu3?.visibility = View.VISIBLE
                    binding?.classOfAnnouncement?.visibility = View.GONE
                }
                2 -> {
                    announcement.apply {
                        announcementCourse = binding?.announcementCourse?.text.toString()
                        announcementClass = binding?.announcementCCC?.text.toString()
                    }
                    binding?.menu3?.visibility = View.VISIBLE
                    binding?.classOfAnnouncement?.visibility = View.VISIBLE
                }
                else -> {
                    announcement.announcementCourse = null
                    announcement.announcementClass = null
                    binding?.menu3?.visibility = View.GONE
                    binding?.classOfAnnouncement?.visibility = View.GONE
                }
            }

        }
        binding?.announcementCourse?.setOnItemClickListener { parent, view, position, id ->
            announcement.announcementCourse = courses[position]
        }
        binding?.announcementCCC?.setOnItemClickListener { _, _, position, _ ->
            announcement.announcementClass = classes[position]
        }
        return binding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}