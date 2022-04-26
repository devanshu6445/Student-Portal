package com.college.portal.studentportal.ui.notifications

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.college.portal.studentportal.R
import com.college.portal.studentportal.databinding.FragmentNotificationsBinding
import com.college.portal.studentportal.roomDatabase.user.CurrentUserDatabase
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog

class NotificationsFragment : Fragment() {

    private lateinit var notificationsViewModel: NotificationsViewModel
    private var _binding: FragmentNotificationsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.

    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        val database = CurrentUserDatabase.getDatabase(activity?.applicationContext!!)
        notificationsViewModel =
            ViewModelProvider(this,NotificationViewModelFactory(database))[NotificationsViewModel::class.java]

        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        notificationsViewModel.loggedInUser.observe(viewLifecycleOwner){
            binding.usernameTextView.text = it.userName
            Glide.with(binding.profileCircleImageView.context)
                .load(it.userImageUrl)
                .error(R.drawable.sample)
                .centerCrop()
                .into(binding.profileCircleImageView)
        }
        binding.logout.setOnClickListener {
            notificationsViewModel.logout()
        }
        notificationsViewModel.logoutProgress.observe(viewLifecycleOwner){
            lateinit var dialog:SweetAlertDialog
            when(it){
                1 -> {
                    dialog = SweetAlertDialog(activity,SweetAlertDialog.PROGRESS_TYPE).apply {
                        progressHelper.barColor = Color.parseColor("#A5DC86")
                        titleText = "Logging out"
                        setCancelable(false)
                    }
                    dialog.show()
                }
                2 -> {
                    SweetAlertDialog(activity,SweetAlertDialog.SUCCESS_TYPE).apply {
                        titleText = "You have been logged out"
                    }.setCancelClickListener {
                        activity?.finishAffinity()
                    }.setConfirmClickListener {
                        activity?.finishAffinity()
                    }.show()
                }
            }
        }

        binding.editProfile.setOnClickListener {
            root.findNavController().navigate(R.id.action_navigation_notifications_to_editStudentDetailsFragment)
        }
        return root
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}