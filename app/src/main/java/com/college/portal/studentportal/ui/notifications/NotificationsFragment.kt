package com.college.portal.studentportal.ui.notifications

import android.app.ActivityManager
import android.content.Context.ACTIVITY_SERVICE
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.college.portal.studentportal.R
import com.college.portal.studentportal.databinding.FragmentNotificationsBinding
import com.college.portal.studentportal.roomDatabase.assignment.Assignment
import com.college.portal.studentportal.roomDatabase.user.CurrentUserDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog
import org.json.JSONArray

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
            when(it.userDesignation){
                "teacher","admin","HOD" -> {
                    binding.myAttendance.visibility = View.GONE
                    binding.myLeaveRequests.visibility = View.GONE
                    binding.leaveRequest.visibility = View.GONE
                    if(it.userDesignation == "teacher")
                        binding.leaveRequestApprove.visibility = View.GONE
                }
                "moderator","student" -> {
                    binding.apply {
                        markAttendance.visibility = View.GONE
                        leaveRequestApprove.visibility = View.GONE
                        assignAnAssignment.visibility = View.GONE
                        announcement.visibility = View.GONE
                        showStudentAttendance.visibility = View.GONE
                    }
                }
            }
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
                    }.setConfirmClickListener {
                        (context?.getSystemService(ACTIVITY_SERVICE) as ActivityManager).clearApplicationUserData()
                        activity?.finishAffinity()
                    }.show()
                }
            }
        }
        binding.markAttendance.setOnClickListener {
            val action = NotificationsFragmentDirections.actionNavigationNotificationsToAttendanceFragment(0)
            root.findNavController().navigate(action)
        }
        binding.editProfile.setOnClickListener {
            root.findNavController().navigate(R.id.action_navigation_notifications_to_editStudentDetailsFragment)
        }
        binding.changePassword.setOnClickListener {
            root.findNavController().navigate(R.id.action_navigation_notifications_to_changePasswordFragment)
        }
        binding.myAttendance.setOnClickListener {
            val action = NotificationsFragmentDirections.actionNavigationNotificationsToSelectSubjectFragment(notificationsViewModel.loggedInUser.value!!)
            root.findNavController().navigate(action)
        }
        binding.announcement.setOnClickListener {
            root.findNavController().navigate(R.id.action_navigation_notifications_to_postAnnouncement)
        }
        binding.showStudentAttendance.setOnClickListener {
            val action = NotificationsFragmentDirections.actionNavigationNotificationsToAttendanceFragment(1)
            root.findNavController().navigate(action)
        }
        binding.aboutUs.setOnClickListener {
            root.findNavController().navigate(R.id.action_navigation_notifications_to_announcementInfoFragment)
        }

        binding.leaveRequest.setOnClickListener {
            val action = NotificationsFragmentDirections.actionNavigationNotificationsToSendLeaveRequestFragment(notificationsViewModel.loggedInUser.value!!)
            root.findNavController().navigate(action)
        }
        binding.myLeaveRequests.setOnClickListener {
            val action = NotificationsFragmentDirections.actionNavigationNotificationsToMyLeaveRequestsFragment(notificationsViewModel.loggedInUser.value!!)
            root.findNavController().navigate(action)
        }
        binding.leaveRequestApprove.setOnClickListener {
            val action = NotificationsFragmentDirections.actionNavigationNotificationsToLeaveRequestApprovingFragment(notificationsViewModel.loggedInUser.value!!)
            it.findNavController().navigate(action)
        }
        binding.assignAnAssignment.setOnClickListener {
            val action = NotificationsFragmentDirections.actionNavigationNotificationsToPostAssignmentFragment(notificationsViewModel.loggedInUser.value!!)
            it.findNavController().navigate(action)
        }
        binding.language.setOnClickListener {
            Toast.makeText(activity?.applicationContext, "Coming soon..", Toast.LENGTH_SHORT).show()
        }
        return root
    }

    companion object{
        private const val TAG = "NotificationsFragment: "
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}