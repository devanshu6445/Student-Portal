package com.college.portal.studentportal.ui.notifications.leaveRequest

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.college.portal.studentportal.databinding.SendLeaveRequestFragmentBinding
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class SendLeaveRequestFragment : Fragment() {

    private lateinit var viewModel: SendLeaveRequestViewModel
    private var binding: SendLeaveRequestFragmentBinding? = null
    private val args: SendLeaveRequestFragmentArgs by navArgs()
    private lateinit var fromDate:Date
    private lateinit var toDate:Date

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(
            this,
            SendLeaveRequestViewModelFactory(args.cUser)
        )[SendLeaveRequestViewModel::class.java]
        binding = SendLeaveRequestFragmentBinding.inflate(inflater, container, false)
        binding?.sendLeaveRequest?.setOnClickListener {
            if(binding?.leaveReason?.text.toString().isNotEmpty()){
                val diff = toDate.time - fromDate.time
                val days = TimeUnit.MILLISECONDS.toDays(diff)
                viewModel.sendRequest(
                    binding?.leaveReason?.text.toString(),
                    binding?.fromDate?.text.toString(),
                    binding?.toDate?.text.toString(),
                    days
                )
            }else
                Toast.makeText(activity?.applicationContext, "Please specify a reason!", Toast.LENGTH_SHORT).show()
        }
        lateinit var dialog: SweetAlertDialog
        viewModel.sendStatus.observe(viewLifecycleOwner) {
            when (it) {
                1 -> {
                    dialog = SweetAlertDialog(activity, SweetAlertDialog.PROGRESS_TYPE).apply {
                        progressHelper.barColor = Color.parseColor("#A5DC86")
                        titleText = "Sending request"
                        setCancelable(false)
                    }
                    dialog.show()
                }
                2 -> {
                    SweetAlertDialog(activity, SweetAlertDialog.SUCCESS_TYPE).apply {
                        titleText = "Request sent"
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
        val fromOnDateChangeListener =
            DatePicker.OnDateChangedListener { _, year, monthOfYear, dayOfMonth ->
                val c = Calendar.getInstance().apply {
                    set(year,monthOfYear,dayOfMonth)
                }
                fromDate = c.time
                val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(c.time)
                binding?.fromDate?.text = formatter
            }

        val toDateChangeListener =
            DatePicker.OnDateChangedListener { _, year, monthOfYear, dayOfMonth ->
                val c = Calendar.getInstance().apply {
                    set(year,monthOfYear,dayOfMonth)
                }
                toDate = c.time
                val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(c.time)
                binding?.toDate?.text = formatter
            }
        binding?.selectFromDate?.setOnClickListener {
            binding?.leaveDatePicker?.visibility = View.VISIBLE
            binding?.leaveDatePicker?.setOnDateChangedListener(fromOnDateChangeListener)
        }
        binding?.selectToDate?.setOnClickListener {
            binding?.leaveDatePicker?.visibility = View.VISIBLE
            binding?.leaveDatePicker?.setOnDateChangedListener(toDateChangeListener)
        }
        binding?.root?.setOnClickListener {
            binding?.leaveDatePicker?.visibility = View.GONE
        }
        binding?.leaveRequestToolbar?.setNavigationOnClickListener {
            it.findNavController().popBackStack()
        }

        binding?.leaveDatePicker?.also {
            val stringBuilder = StringBuilder()
            stringBuilder.append(it.dayOfMonth)
            stringBuilder.append("/")
            stringBuilder.append(it.month+1)
            stringBuilder.append("/")
            stringBuilder.append(it.year)
            val date = stringBuilder.toString()
            binding?.toDate?.text = date
            binding?.fromDate?.text = date
        }
        return binding?.root
    }
}