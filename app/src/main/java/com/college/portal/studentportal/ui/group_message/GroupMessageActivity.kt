package com.college.portal.studentportal.ui.group_message

import android.content.Intent
import android.os.Bundle
import android.transition.Fade
import android.util.Log
import android.view.ViewAnimationUtils
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.college.portal.studentportal.R
import com.college.portal.studentportal.StudentMain
import com.college.portal.studentportal.adapter.GroupMessageAdapter
import com.college.portal.studentportal.data.model.GroupMessageData
import com.college.portal.studentportal.databinding.ActivityGroupMessageBinding
import com.college.portal.studentportal.ui.dashboard.DashboardFragment
import com.college.portal.studentportal.ui.group_details.GroupDetails
import kotlin.math.hypot

class GroupMessageActivity : AppCompatActivity() {

    var binding: ActivityGroupMessageBinding? = null
    private lateinit var groupMessageAdapter: GroupMessageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupMessageBinding.inflate(
            layoutInflater
        )
        setContentView(binding!!.root)
        supportActionBar?.hide()
        val groupMessageViewModel = ViewModelProvider(this,GroupMessageViewModelFactory())
            .get(GroupMessageViewModel::class.java)

        val layoutManager = LinearLayoutManager(applicationContext)
        val recyclerView = findViewById<RecyclerView>(R.id.RecyclerView_groupMessage)
        recyclerView.apply {
            setHasFixedSize(true)
        }
        val sendTextMessage = binding!!.groupTextMessageSend
        val groupTextMessage = binding!!.groupMessageTextView
        groupMessageAdapter = GroupMessageAdapter(applicationContext)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = groupMessageAdapter

        binding?.groupTitle?.setOnClickListener {
            startActivity(Intent(this@GroupMessageActivity,GroupDetails::class.java))
        }
        //animation test START -------------------

        val messageBoxParent = binding!!.messageBoxParent

        binding?.animeTest?.setOnClickListener {
            val finalRadius = hypot((messageBoxParent.width/2).toDouble(),(messageBoxParent.height/2).toDouble()).toFloat()
            val animation = ViewAnimationUtils.createCircularReveal(messageBoxParent,
                messageBoxParent.width,
                messageBoxParent.height/2,
                0f,
                (messageBoxParent.width).toFloat()
            ).apply {
                duration = 700
                interpolator = AccelerateDecelerateInterpolator()
            }
            binding!!.groupBottomBar.isVisible = false
            messageBoxParent.isVisible = true
            animation.start()
        }
        //animation test END ----------------------

        binding?.groupMessageToolbar?.setOnClickListener {
            onBackPressed()
        }

        groupMessageViewModel.groupMessageList.observe(this, Observer{
            Log.d("messageListCount","${it.size}")
            groupMessageAdapter.updateMessageList(it)
            Log.d("observerLog","${groupMessageAdapter.itemCount}")
        })
        sendTextMessage.setOnClickListener{
            groupMessageViewModel.sendMessage(groupTextMessage.text.toString())
        }

    }
}