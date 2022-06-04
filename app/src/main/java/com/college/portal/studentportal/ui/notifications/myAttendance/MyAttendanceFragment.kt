package com.college.portal.studentportal.ui.notifications.myAttendance

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.applandeo.materialcalendarview.EventDay
import com.applandeo.materialcalendarview.listeners.OnCalendarPageChangeListener
import com.college.portal.studentportal.R
import com.college.portal.studentportal.databinding.MyAttendanceFragmentBinding
import com.college.portal.studentportal.extensionFunctions.monthArray
import com.college.portal.studentportal.roomDatabase.attendance.AttendanceDatabase
import java.text.SimpleDateFormat
import java.util.*

class MyAttendanceFragment : Fragment() {

    private lateinit var binding: MyAttendanceFragmentBinding
    private lateinit var viewModel: MyAttendanceViewModel
    private val args: MyAttendanceFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MyAttendanceFragmentBinding.inflate(inflater,container,false)
        val database = AttendanceDatabase.getDatabase(activity?.applicationContext!!,args.subject.subCode)
        viewModel = ViewModelProvider(this,MyAttendanceViewModelFactory(database,args.subject,args.studentUid))[MyAttendanceViewModel::class.java]
        val view = binding.root

        viewModel.attendanceList.observe(viewLifecycleOwner){

            val highLighted = mutableListOf<EventDay>()
            it.forEach {it1 ->
                if(it1.isPresent){
                    highLighted.add(
                        EventDay(Calendar.getInstance().apply {
                            set(it1.year, monthArray.indexOf(it1.month),it1.day)
                        },R.color.green)

                    )
                }else{
                    highLighted.add(
                        EventDay(Calendar.getInstance().apply {
                            set(it1.year, monthArray.indexOf(it1.month),it1.day)
                        },R.color.red_btn_bg_color)
                    )
                }
            }
            binding.calendarView.setEvents(highLighted)
        }
        viewModel.monthPercentage.observe(viewLifecycleOwner){
            val percentage = context?.getString(R.string.this_month_attendance_percentage) + it +"%"
            binding.currentMonthAttendancePer.text = percentage
        }
        viewModel.overallPercentage.observe(viewLifecycleOwner){
            val percentage = context?.getString(R.string.overall_attendance_percentage) + it +"%"
            binding.overallAttendancePer.text = percentage
        }
        val onCalendarPageChangeListener = OnCalendarPageChangeListener {
            val calendar = binding.calendarView.currentPageDate
            val date = calendar.time
            val month = SimpleDateFormat("MMMM", Locale.UK).format(date)
            viewModel.getAll(month)
            viewModel.getMonthAttendancePercentage(month)
        }
        binding.calendarView.setOnPreviousPageChangeListener(onCalendarPageChangeListener)
        binding.calendarView.setOnForwardPageChangeListener(onCalendarPageChangeListener)
        binding.attendanceToolbar.setNavigationOnClickListener {
            it.findNavController().popBackStack()
        }
        return view
    }

}