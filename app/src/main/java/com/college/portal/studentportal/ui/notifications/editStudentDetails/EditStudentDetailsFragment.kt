package com.college.portal.studentportal.ui.notifications.editStudentDetails

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.college.portal.studentportal.R
import com.college.portal.studentportal.data.model.EditedProfile
import com.college.portal.studentportal.databinding.EditStudentDetailsFragmentBinding
import com.college.portal.studentportal.extensionFunctions.validPassword
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog

class EditStudentDetailsFragment : Fragment() {

    /*companion object {
        fun newInstance() = EditStudentDetailsFragment()
    }*/

    private lateinit var viewModel: EditStudentDetailsViewModel
    private lateinit var binding:EditStudentDetailsFragmentBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        binding = EditStudentDetailsFragmentBinding.inflate(inflater,container,false)
        viewModel = ViewModelProvider(this)[EditStudentDetailsViewModel::class.java]
        val view = binding.root

        //Work after this
        val date = binding.epUserDOBDate
        val month = binding.epUserDOBMonth
        val year = binding.epUserDOBYear

        binding.epUserProfileUpdate.setOnClickListener {
            val profile = EditedProfile(
                binding.epUserEmail.text.toString(),
                binding.epUserAddress.text.toString(),
                "${date.text} ${month.text} ${year.text}",
                "6395311466"
            )
            viewModel.updateProfile(profile,binding.epUserPassword.text.toString())
        }
        binding.epProfileImage.setOnClickListener {

        }
        binding.epUserPassword.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                val result = validPassword(binding.epUserPassword.text.toString())
                if(!result){
                    binding.epUserPassword.setTextColor(ContextCompat.getColor(activity!!,R.color.red_btn_bg_color))
                    binding.epPasswordWarning.visibility = View.VISIBLE
                }
                else{
                    binding.epUserPassword.setTextColor(Color.BLACK)
                    binding.epPasswordWarning.visibility = View.GONE
                }
            }

        })
        binding.materialToolbar2.setNavigationOnClickListener {
            activity?.onBackPressed()
        }
        val profileChangeObserver = Observer<Int>{
            when(it){
                1 ->{
                    SweetAlertDialog(activity,SweetAlertDialog.PROGRESS_TYPE).apply {
                        progressHelper.barColor = Color.parseColor("#A5DC86")
                        titleText = "Please wait.."
                        setCancelable(false)
                    }.show()
                }
                2 -> {
                    SweetAlertDialog(activity,SweetAlertDialog.SUCCESS_TYPE).apply {
                        titleText = "Your profile has been updated."
                    }.setCancelClickListener {
                        activity?.finishAffinity()
                    }.setConfirmClickListener {
                        activity?.finishAffinity()
                    }.show()
                }
                3 -> {
                    SweetAlertDialog(activity,SweetAlertDialog.ERROR_TYPE).apply {
                        titleText = viewModel.error.message
                    }.setCancelClickListener {
                        activity?.finishAffinity()
                    }.setConfirmClickListener {
                        activity?.finishAffinity()
                    }.show()
                }
            }
        }
        viewModel.profileUpdateStatus.observe(viewLifecycleOwner,profileChangeObserver)
        return view
    }
}