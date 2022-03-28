package com.college.portal.studentportal.ui.group_message

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.view.ViewAnimationUtils
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.PopupMenu
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.college.portal.studentportal.R
import com.college.portal.studentportal.adapter.GroupMessageAdapter
import com.college.portal.studentportal.databinding.ActivityGroupMessageBinding
import com.college.portal.studentportal.roomDatabase.groupEverything.GroupMessageDatabase
import com.college.portal.studentportal.roomDatabase.groups.BasicGroupData
import com.college.portal.studentportal.roomDatabase.user.CurrentUserDatabase
import com.college.portal.studentportal.ui.groupMessageRequest.MessageRequestsActivity
import com.college.portal.studentportal.ui.group_participant_details.GroupDetails

class GroupMessageActivity : AppCompatActivity() {

    private var binding: ActivityGroupMessageBinding? = null
    private lateinit var groupMessageAdapter: GroupMessageAdapter
    private lateinit var groupMessageViewModel: GroupMessageViewModel
    private lateinit var groupData:BasicGroupData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupMessageBinding.inflate(
            layoutInflater
        )
        groupData = intent.getSerializableExtra("group-data") as BasicGroupData
        setContentView(binding!!.root)
        supportActionBar?.hide()
        val database = GroupMessageDatabase.createDatabase(applicationContext,groupData.groupID)
        val currentUserDatabase = CurrentUserDatabase.getDatabase(applicationContext)
        groupMessageViewModel =
            ViewModelProvider(
                this,
                GroupMessageViewModelFactory(groupData, database, currentUserDatabase)
            )[GroupMessageViewModel::class.java]

        val layoutManager = LinearLayoutManager(applicationContext).apply {
            stackFromEnd = true
        }
        val recyclerView = findViewById<RecyclerView>(R.id.RecyclerView_groupMessage)
        recyclerView.apply {
            setHasFixedSize(true)
        }
        val getImage = registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) {
            groupMessageViewModel.sendMessage(it)
        }
        val sendTextMessage = binding!!.groupTextMessageSend
        val groupTextMessage = binding!!.groupMessageTextView

        recyclerView.layoutManager = layoutManager


        binding?.groupTitle?.setOnClickListener {
            startActivity(Intent(this@GroupMessageActivity, GroupDetails::class.java).apply {
                putExtra("group-data", groupData)
            })
        }

        val imageButton = binding!!.optionMenu

        imageButton.setOnClickListener {
            when(groupMessageViewModel.currentParticipant?.role){
                "admin" ->{
                    if (binding?.messageBoxParent != null) {
                        Toast.makeText(this,"Okay!",Toast.LENGTH_SHORT).show()
                        createOptionMenuForStudent(
                            R.menu.participants_menu_for_teacher,
                            imageButton,
                            binding!!.messageBoxParent,
                            applicationContext,
                            binding!!.groupBottomBar
                        )

                    }
                }
                "teacher" -> {
                    if (binding?.messageBoxParent != null) {
                        createOptionMenuForStudent(
                            R.menu.participants_menu_for_teacher,
                            imageButton,
                            binding!!.messageBoxParent,
                            applicationContext,
                            binding!!.groupBottomBar
                        )

                    }
                }
                "moderator" -> {
                    if (binding?.messageBoxParent != null) {
                        createOptionMenuForStudent(
                            R.menu.participant_menu_for_moderator,
                            imageButton,
                            binding!!.messageBoxParent,
                            applicationContext,
                            binding!!.groupBottomBar
                        )

                    }
                }
                "student" -> {
                    if (binding?.messageBoxParent != null) {
                        createOptionMenuForStudent(
                            R.menu.participant_menu_for_student,
                            imageButton,
                            binding!!.messageBoxParent,
                            applicationContext,
                            binding!!.groupBottomBar
                        )

                    }
                }
            }
        }

        binding?.groupMessageToolbar?.setNavigationOnClickListener {
            onBackPressed()
        }
        groupMessageAdapter = GroupMessageAdapter(applicationContext, mutableListOf())
        recyclerView.adapter = groupMessageAdapter

        binding?.groupMessageImage?.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this@GroupMessageActivity,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                getImage.launch("image/*")
            }
        }
        sendTextMessage.setOnClickListener {
            groupMessageViewModel.sendMessage(groupTextMessage.text.toString())
        }
        groupMessageViewModel.groupMessageList.observe(this) {
            groupMessageAdapter.updateMessageList(it)
            layoutManager.smoothScrollToPosition(
                recyclerView,
                null,
                groupMessageAdapter.itemCount)
        }
        groupMessageViewModel.messageSent.observe(this){
            if(it){
                Toast.makeText(this,"Message Sent",Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this,"Message not sent",Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun createOptionMenuForStudent(menuId: Int,
                                   view: View,
                                   messageBoxParent: RelativeLayout,
                                   context: Context,
                                   groupBottomBar: RelativeLayout
    ) {

        val popupMenu = PopupMenu(context, view)
        popupMenu.menuInflater.inflate(menuId, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { p0 ->
            if (p0 != null) {
                when (p0.itemId) {
                    R.id.requestMessage -> {
                        val animation = ViewAnimationUtils.createCircularReveal(
                            messageBoxParent,
                            messageBoxParent.width,
                            messageBoxParent.height / 2,
                            0f,
                            (messageBoxParent.width).toFloat()
                        ).apply {
                            duration = 300
                            interpolator = AccelerateDecelerateInterpolator()
                        }
                        groupBottomBar.isVisible = false
                        messageBoxParent.isVisible = true
                        animation.start()
                    }
                    R.id.group_info -> {
                        Toast.makeText(view.context, "Group Info Clicked", Toast.LENGTH_SHORT)
                            .show()
                    }

                    R.id.banParticipant ->{ Toast.makeText(context,"Ban Participant",Toast.LENGTH_SHORT).show()}

                    R.id.show_profile -> {Toast.makeText(context,"Show Profile",Toast.LENGTH_SHORT).show()}

                    R.id.allRequests -> {
                        Toast.makeText(context,"All message requests",Toast.LENGTH_SHORT).show()
                        val intent = Intent(this,MessageRequestsActivity::class.java).apply {
                            putExtra("group-data",groupData)
                        }
                        startActivity(intent)
                    }

                    R.id.demoteUser -> {Toast.makeText(context,"Demote user",Toast.LENGTH_SHORT).show()}

                    R.id.promoteUser -> {Toast.makeText(context,"Promote User",Toast.LENGTH_SHORT).show()}

                    R.id.demoteYourself -> {Toast.makeText(context,"Demote Yourself",Toast.LENGTH_SHORT).show()}
                    else -> {
                        Toast.makeText(context,"Unknown click",Toast.LENGTH_SHORT).show()
                    }
                }
            }
            true
        }
        popupMenu.show()
    }

}