package com.college.portal.studentportal.ui.groupMessageRequest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
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

    companion object{
        private const val TAG = "MessageRequestActivity: "
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMessageRequestsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        val database = GroupMessageDatabase.getDatabase()

        basicGroupData = try {
            intent.getSerializableExtra("group-data") as BasicGroupData
        } catch (e: ClassCastException) {
            Log.e(TAG, "onCreate: ", e)
            throw e
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
        recyclerView.adapter = messageRequestAdapter

        messageRequestViewModel.messageRequestList.observe(this){
            messageRequestAdapter.updateMessageRequestList(it)
        }
        messageRequestAdapter.isEmpty.observe(this){
            if(it == 0){
                binding.emptyMessage.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            }else{
                binding.emptyMessage.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            }
        }
        messageRequestAdapter.isUpdated.observe(this){
            if (it){
                /*linearLayoutManager.smoothScrollToPosition(
                    recyclerView,
                    null,
                    0)*/
                binding.progressBar.visibility = View.VISIBLE
            }else{
                binding.progressBar.visibility = View.GONE
            }
        }
    }
}