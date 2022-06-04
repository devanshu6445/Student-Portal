package com.college.portal.studentportal.ui.notifications.postAssignment

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.college.portal.studentportal.R
import com.college.portal.studentportal.data.model.MediaFile
import com.college.portal.studentportal.databinding.PostAnnouncementFragmentBinding
import com.college.portal.studentportal.databinding.PostAssignmentFragmentBinding
import com.college.portal.studentportal.extensionFunctions.classes
import com.college.portal.studentportal.extensionFunctions.courses
import com.college.portal.studentportal.extensionFunctions.getName
import com.college.portal.studentportal.roomDatabase.assignment.Assignment
import com.college.portal.studentportal.roomDatabase.subjectDatabase.SubjectDatabase
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class PostAssignmentFragment : Fragment() {

    private lateinit var viewModel: PostAssignmentViewModel
    private var binding:PostAssignmentFragmentBinding? = null
    private val fileList = Stack<MediaFile>()
    private val args by navArgs<PostAssignmentFragmentArgs>()
    private var deadline = ""

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this,PostAssignmentViewModelFactory(args.cUser))[PostAssignmentViewModel::class.java]
        binding = PostAssignmentFragmentBinding.inflate(inflater,container,false)
        val subjectDatabase = SubjectDatabase.getDatabase(activity?.applicationContext!!)
        val getFiles = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            fileList.clear()
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
                        Log.d("PostAnn", "onCreateView: ${uri?.getName(activity?.applicationContext!!)}")
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
        val courseAdapter = ArrayAdapter(requireContext(),R.layout.list_item, courses)
        binding?.assignmentCourse?.setAdapter(courseAdapter)

        val sectionAdapter = ArrayAdapter(requireContext(),R.layout.list_item, classes)
        binding?.assignmentSection?.setAdapter(sectionAdapter)

        binding?.assignmentCourse?.setOnItemClickListener { parent, view, position, id ->
            lifecycleScope.launch(Dispatchers.IO) {
                viewModel.getSubjectList(subjectDatabase, courses[position])
            }
        }

        viewModel.subjectList.observe(viewLifecycleOwner){
            binding?.assignmentSubject?.setText("")
            binding?.assignmentSubject?.setAdapter(ArrayAdapter(requireContext(),R.layout.list_item,it))
        }

        binding?.addAttachments?.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true)
                type = "*/*"
            }
            getFiles.launch(intent)
        }
        binding?.postTHeAssignment?.setOnClickListener {
            val assignment = Assignment().apply {
                assignmentID = UUID.randomUUID().toString()
                assignmentSubjectCode = binding?.assignmentSubject?.text.toString()
                assignmentTitle = binding?.assignmentTitle?.text.toString()
                assignmentSection = binding?.assignmentSection?.text.toString()
                assignmentCourse = binding?.assignmentCourse?.text.toString()
                assignmentInstruction = binding?.assignmentInstruction?.text.toString()
                assignmentDeadline = deadline
                assignmentTimestamp = System.currentTimeMillis()
                assignmentGivenBy = args.cUser.userName
            }
            deadline = ""
            viewModel.uploadAssignment(fileList,assignment)
        }
        binding?.assignmentDeadline?.setOnClickListener {
            binding?.assignmentDeadlinePicker?.visibility = View.VISIBLE
        }
        binding?.root?.setOnClickListener {
            binding?.assignmentDeadlinePicker?.visibility = View.GONE
        }
        binding?.assignmentDeadlinePicker?.setOnDateChangedListener { view, year, monthOfYear, dayOfMonth ->
            val c = Calendar.getInstance().apply {
                set(year,monthOfYear,dayOfMonth)
            }

            val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(c.time)
            deadline = formatter
        }
        lateinit var dialog: SweetAlertDialog
        viewModel.status.observe(viewLifecycleOwner) {
            when (it) {
                1 -> {
                    dialog = SweetAlertDialog(activity, SweetAlertDialog.PROGRESS_TYPE).apply {
                        progressHelper.barColor = Color.parseColor("#A5DC86")
                        titleText = "Sending assignment"
                        setCancelable(false)
                    }
                    dialog.show()
                }
                2 -> {
                    SweetAlertDialog(activity, SweetAlertDialog.SUCCESS_TYPE).apply {
                        titleText = "Assignment sent."
                    }.setCancelClickListener { d ->
                        dialog.dismiss()
                        d.dismissWithAnimation()
                    }.setConfirmClickListener { d ->
                        dialog.dismiss()
                        d.dismissWithAnimation()
                    }.show()
                }
                3 -> {
                    SweetAlertDialog(activity, SweetAlertDialog.ERROR_TYPE).apply {
                        titleText = "Please try again!"
                    }.setCancelClickListener { d ->
                        dialog.dismiss()
                        d.dismissWithAnimation()
                    }.setConfirmClickListener { d ->
                        dialog.dismiss()
                        d.dismissWithAnimation()
                    }.show()
                }
            }
        }
        binding?.attendanceToolbar?.setNavigationOnClickListener {
            it.findNavController().popBackStack()
        }
        return binding?.root
    }

}