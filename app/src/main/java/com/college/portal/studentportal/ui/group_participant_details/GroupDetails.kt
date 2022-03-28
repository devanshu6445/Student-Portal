package com.college.portal.studentportal.ui.group_participant_details

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.college.portal.studentportal.adapter.GroupParticipantsAdapter
import com.college.portal.studentportal.roomDatabase.groups.BasicGroupData
import com.college.portal.studentportal.databinding.ActivityGroupDetailsBinding
import org.json.JSONObject

class GroupDetails : AppCompatActivity() {

    lateinit var binding: ActivityGroupDetailsBinding
    lateinit var requestQueue: RequestQueue
    val URL = "https://fcm.googleapis.com/fcm/send"
    lateinit var token: String
    lateinit var participantRV: RecyclerView
    lateinit var participantsAdapter: GroupParticipantsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        participantsAdapter = GroupParticipantsAdapter(applicationContext, mutableListOf())
        val groupData = intent.getSerializableExtra("group-data") as BasicGroupData
        val groupDetailsViewModel = ViewModelProvider(this, GroupDetailsViewModelFactory(groupData))
            .get(GroupDetailsViewModel::class.java)
        participantRV = binding.participantsRV
        val linearLayoutManager = LinearLayoutManager(applicationContext)
        participantRV.apply {
            setHasFixedSize(true)
            layoutManager = linearLayoutManager
        }
        participantRV.adapter = participantsAdapter
        groupDetailsViewModel.participantList.observe(this, Observer {
            participantsAdapter.updateParticipantList(it)
        })




    }
    //Notification test function START -----------
    private fun sendNotification(){
        val jsonObject = JSONObject()
        jsonObject.put("to",binding.editTextTextPersonName.text.toString())
        val notificationObject = JSONObject()
        notificationObject.put("title","Test Notification")
        notificationObject.put("body","Test Body")
        jsonObject.put("notification",notificationObject)

        val jsonObjectRequest = object: JsonObjectRequest(Request.Method.POST,
            URL,
            jsonObject,
            {
                //code here for success
            Log.d("NotiSucees","Success")
            },
            {
                //code here for error
                Log.e("NotiError",it.message.toString())
            }){
            override fun getHeaders(): MutableMap<String, String> {
                val header: MutableMap<String,String> = HashMap<String,String>()
                header.put("Content-Type","application/json")
                header.put("authorization","key=AAAAk-CL3wU:APA91bHWKyQzW6-lnthGSrxaAE7b0IFKjOZ6CoxWcFOdnl5c7MQjEMpa5powrjsk_Z8hRzFtgCv3QDEg3s_gchZgmvYC9o7pntAuC_mdfQWo5zUZeXBtYMDjYcskRRCEKTn_anZ2Q5nA")
                return header
            }
        }
        requestQueue.add(jsonObjectRequest)
    }
    //Notification test function END ----------------
}