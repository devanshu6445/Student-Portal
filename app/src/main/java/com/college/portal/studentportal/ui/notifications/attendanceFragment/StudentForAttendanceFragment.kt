package com.college.portal.studentportal.ui.notifications.attendanceFragment

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.college.portal.studentportal.adapter.StudentAttendanceAdapter
import com.college.portal.studentportal.databinding.StudentForAttendanceFragmentBinding
import com.college.portal.studentportal.roomDatabase.user.CurrentUserDatabase
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog

class StudentForAttendanceFragment : Fragment() {

    private lateinit var viewModel: StudentForAttendanceViewModel
    //private lateinit var binding: StudentForAttendanceFragmentBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var studentAttendanceAdapter: StudentAttendanceAdapter
    private val args: StudentForAttendanceFragmentArgs by navArgs()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val database =
            CurrentUserDatabase.getDatabase(activity?.applicationContext!!, "studentDatabase")
        viewModel = ViewModelProvider(
            this,
            StudentForAttendanceViewModelFactory(
                args.subject.subSem,
                args.subject.subCourse,
                args.section,
                database
            )
        )[StudentForAttendanceViewModel::class.java]
        val binding = StudentForAttendanceFragmentBinding.inflate(inflater, container, false)
        val view = binding.root
        recyclerView = binding.studentLRV
        layoutManager = LinearLayoutManager(activity?.applicationContext)
        studentAttendanceAdapter =
            context?.let { StudentAttendanceAdapter(it, args.subject.subCode) }!!
        recyclerView.layoutManager = this.layoutManager
        recyclerView.adapter = studentAttendanceAdapter

        viewModel.studentList.observe(viewLifecycleOwner) {
            studentAttendanceAdapter.updateList(it)
        }
        binding.submitAttendance.setOnClickListener {
            viewModel.submit(studentAttendanceAdapter.presentStudentList, args.subject.subCode)
        }
        binding.attendanceToolbar.setNavigationOnClickListener {
            it.findNavController().popBackStack()
        }
            lateinit var dialog: SweetAlertDialog
        viewModel.uploadStatus.observe(viewLifecycleOwner){
            when(it){
                1 -> {
                    dialog = SweetAlertDialog(activity, SweetAlertDialog.PROGRESS_TYPE).apply {
                        progressHelper.barColor = Color.parseColor("#A5DC86")
                        titleText = "Uploading attendance"
                        setCancelable(false)
                    }
                    dialog.show()
                }
                2 -> {
                    dialog.dismiss()
                    SweetAlertDialog(activity, SweetAlertDialog.SUCCESS_TYPE).apply {
                        titleText = "Attendance uploaded.\n If there is any kind of mistake please contact the administrator."
                    }.setConfirmClickListener {it1 ->
                        it1.dismissWithAnimation()
                        binding.root.findNavController().popBackStack()
                    }.show()
                }
            }
        }
        return view
    }


}