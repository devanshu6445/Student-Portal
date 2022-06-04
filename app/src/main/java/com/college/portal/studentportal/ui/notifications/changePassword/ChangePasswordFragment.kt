package com.college.portal.studentportal.ui.notifications.changePassword

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.college.portal.studentportal.data.model.ChangePassword
import com.college.portal.studentportal.databinding.ChangePasswordFragmentBinding
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog

class ChangePasswordFragment : Fragment() {

    companion object {
        //fun newInstance() = ChangePasswordFragment()
    }

    private lateinit var viewModel: ChangePasswordViewModel
    private lateinit var binding: ChangePasswordFragmentBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ChangePasswordFragmentBinding.inflate(layoutInflater,container,false)

        viewModel = ViewModelProvider(this)[ChangePasswordViewModel::class.java]
        val view = binding.root

        binding.cpConfirmPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if(binding.cpNewPassword.text.toString() != binding.cpConfirmPassword.text.toString()){
                    binding.cpConfirmPasswordWarning.visibility = View.VISIBLE
                    binding.cpChangePassword.apply {
                        isEnabled = false
                        alpha = 0.5f
                    }
                }else{
                    binding.cpConfirmPasswordWarning.visibility = View.GONE
                    binding.cpChangePassword.apply {
                        isEnabled = true
                        alpha = 1f
                    }
                }
            }
        })

        viewModel.passwordChangeStatus.observe(viewLifecycleOwner){
            when(it){
                1 -> {
                    SweetAlertDialog(activity,SweetAlertDialog.PROGRESS_TYPE).apply {
                        progressHelper.barColor = Color.parseColor("#A5DC86")
                        titleText = "Changing password..."
                        setCancelable(false)
                    }.show()
                }
                2 -> {
                    SweetAlertDialog(activity,SweetAlertDialog.SUCCESS_TYPE).apply {
                        titleText = "Your password has been changed."
                    }.setCancelClickListener {
                        activity?.finishAffinity()
                    }.setConfirmClickListener {
                        activity?.finishAffinity()
                    }.show()
                }
                3 -> {
                    SweetAlertDialog(activity,SweetAlertDialog.ERROR_TYPE).apply {
                        titleText = "Could not change the password!"
                    }.setCancelClickListener {
                        activity?.finishAffinity()
                    }.setConfirmClickListener {
                        activity?.finishAffinity()
                    }.show()
                }
            }
        }

        binding.cpChangePassword.setOnClickListener {
            viewModel.changePassword(ChangePassword(
                binding.cpCurrentPassword.text.toString(),
                binding.cpNewPassword.text.toString()
            ))
        }
        binding.materialToolbar2.setNavigationOnClickListener {
            it.findNavController().popBackStack()
        }
        return view
    }

}