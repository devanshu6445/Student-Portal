package com.college.portal.studentportal.ui.group_message

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.college.portal.studentportal.R
import com.college.portal.studentportal.adapter.GroupMessageAdapter
import com.college.portal.studentportal.data.model.GroupMessageData
import com.college.portal.studentportal.databinding.ActivityGroupMessageBinding

class GroupMessageActivity : AppCompatActivity() {

    var binding: ActivityGroupMessageBinding? = null
    private lateinit var groupMessageAdapter: GroupMessageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupMessageBinding.inflate(
            layoutInflater
        )

        setContentView(binding!!.root)
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