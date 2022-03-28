package com.college.portal.studentportal.ui.groupMessageRequest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.college.portal.studentportal.adapter.MessageRequestAdapter
import com.college.portal.studentportal.databinding.ActivityMessageRequestsBinding
import com.college.portal.studentportal.roomDatabase.groupEverything.GroupMessageDatabase
import com.college.portal.studentportal.roomDatabase.groups.BasicGroupData

class MessageRequestsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var messageRequestAdapter: MessageRequestAdapter
    private lateinit var messageRequestViewModel: MessageRequestViewModel
    private lateinit var basicGroupData: BasicGroupData

    private lateinit var binding: ActivityMessageRequestsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMessageRequestsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        val database = GroupMessageDatabase.getDatabase()

        basicGroupData = try {
            intent.getSerializableExtra("group-data") as BasicGroupData
        } catch (e: ClassCastException) {
            BasicGroupData()
        }
        messageRequestViewModel = ViewModelProvider(this,MessageRequestViewModelFactory(database,basicGroupData))[MessageRequestViewModel::class.java]

        val linearLayoutManager = LinearLayoutManager(applicationContext).apply {
            stackFromEnd = false
        }
        recyclerView = binding.messageRequestRecyclerView

        recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = linearLayoutManager
        }
        messageRequestAdapter = MessageRequestAdapter(applicationContext)
        messageRequestAdapter
        recyclerView.adapter = messageRequestAdapter

        messageRequestViewModel.messageRequestList.observe(this){
            messageRequestAdapter.updateMessageRequestList(it)
        }
    }
}