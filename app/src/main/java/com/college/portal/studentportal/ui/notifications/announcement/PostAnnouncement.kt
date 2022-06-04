package com.college.portal.studentportal.ui.notifications.announcement

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.college.portal.studentportal.R
import com.college.portal.studentportal.data.model.Announcement
import com.college.portal.studentportal.data.model.MediaFile
import com.college.portal.studentportal.databinding.PostAnnouncementFragmentBinding
import com.college.portal.studentportal.extensionFunctions.announcementType
import com.college.portal.studentportal.extensionFunctions.classes
import com.college.portal.studentportal.extensionFunctions.courses
import com.college.portal.studentportal.extensionFunctions.getName
import com.college.portal.studentportal.roomDatabase.announcement.AnnouncementDatabase
import java.util.*

class PostAnnouncement : Fragment() {

    private lateinit var viewModel: PostAnnouncementViewModel
    private var binding:PostAnnouncementFragmentBinding? = null
    private val announcement = Announcement()
    private val fileList = Stack<MediaFile>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = PostAnnouncementFragmentBinding.inflate(inflater,container,false)
        val database = AnnouncementDatabase.getDatabase(activity?.applicationContext!!)
        viewModel = ViewModelProvider(this,PostAnnouncementViewModelFactory(database))[PostAnnouncementViewModel::class.java]

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
            viewModel.postAnnouncement(announcement,fileList)
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
        binding?.announcementCourse?.setOnItemClickListener { _, _, position, _ ->
            announcement.announcementCourse = courses[position]
        }
        binding?.announcementCCC?.setOnItemClickListener { _, _, position, _ ->
            announcement.announcementClass = classes[position]
        }
        binding?.attendanceToolbar?.setNavigationOnClickListener {
            it.findNavController().popBackStack()
        }
        val getFiles = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result ->
            if(result.resultCode == Activity.RESULT_OK){
                val data = result.data
                if(data?.clipData != null){
                    val count = data.clipData?.itemCount ?: 0
                    for (i in 0 until count){
                        val uri = data.clipData?.getItemAt(i)?.uri
                        if (uri != null) {
                            fileList.push(uri.getName(activity?.applicationContext!!)
                                ?.let { MediaFile(uri, it) })
                        }
                    }
                }else if(data?.data != null){
                    val uri = data.data
                    if (uri != null) {
                        fileList.push(uri.getName(activity?.applicationContext!!)
                            ?.let { MediaFile(uri, it) })
                    }
                    Toast.makeText(activity?.applicationContext, uri?.getName(activity?.applicationContext!!), Toast.LENGTH_SHORT).show()
                }
            }
        }
        binding?.addAttachments?.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true)
                type = "*/*"
            }
            getFiles.launch(intent)
        }
        return binding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}