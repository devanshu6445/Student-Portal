package com.college.portal.studentportal.ui.group_participant_details

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.college.portal.studentportal.R
import com.college.portal.studentportal.adapter.GroupParticipantsAdapter
import com.college.portal.studentportal.databinding.ActivityGroupDetailsBinding
import com.college.portal.studentportal.roomDatabase.groupEverything.GroupMessageDatabase
import com.college.portal.studentportal.roomDatabase.groups.BasicGroupData
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog

class GroupDetails : AppCompatActivity() {

    private var binding: ActivityGroupDetailsBinding? = null
    //private lateinit var requestQueue: RequestQueue
    //private lateinit var token: String
    private var participantRV: RecyclerView? = null
    private var participantsAdapter: GroupParticipantsAdapter? = null

    companion object{
        //private const val URL = "https://fcm.googleapis.com/fcm/send"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupDetailsBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        supportActionBar?.hide()
        val groupData = intent.getSerializableExtra("group-data") as BasicGroupData
        val database = GroupMessageDatabase.createDatabase(applicationContext,groupData.groupID)
        val groupDetailsViewModel = ViewModelProvider(this, GroupDetailsViewModelFactory(database,groupData))[GroupDetailsViewModel::class.java]
        binding?.groupImage?.let {
            Glide.with(binding?.groupImage!!.context)
                .load(groupData.groupImage)
                .error(R.drawable.sample)
                .into(it)
        }
        binding?.groupName?.text = groupData.groupName
        binding?.appBarLayoutGD?.setNavigationOnClickListener {
            onBackPressed()
        }
        participantsAdapter = GroupParticipantsAdapter(applicationContext, mutableListOf())
        participantRV = binding?.participantsRV
        val linearLayoutManager = LinearLayoutManager(applicationContext)
        participantRV?.apply {
            setHasFixedSize(true)
            layoutManager = linearLayoutManager
        }
        participantRV?.adapter = participantsAdapter
        groupDetailsViewModel.participantList.observe(this) {
            participantsAdapter?.updateParticipantList(it)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        participantRV?.adapter = null
        participantsAdapter = null
        participantRV = null
        binding = null
    }
    //Notification test function START -----------
    /*private fun sendNotification(){
        val jsonObject = JSONObject()
        jsonObject.put("to",binding.editTextTextPersonName.text.toString())
        val notificationObject = JSONObject()
        notificationObject.put("title","Test Notification")
        notificationObject.put("body","Test Body")
        jsonObject.put("notification",notificationObject)

        val jsonObjectRequest = object: JsonObjectRequest(Method.POST,
            URL,
            jsonObject,
            {
                //code here for success
            Log.d("NotificationSuccess","Success")
            },
            {
                //code here for error
                Log.e("NotificationError",it.message.toString())
            }){
            override fun getHeaders(): MutableMap<String, String> {
                val header: MutableMap<String,String> = HashMap<String,String>()
                header.put("Content-Type","application/json")
                header.put("authorization","key=AAAAk-CL3wU:APA91bHWKyQzW6-lnthGSrxaAE7b0IFKjOZ6CoxWcFOdnl5c7MQjEMpa5powrjsk_Z8hRzFtgCv3QDEg3s_gchZgmvYC9o7pntAuC_mdfQWo5zUZeXBtYMDjYcskRRCEKTn_anZ2Q5nA")
                return header
            }
        }
        requestQueue.add(jsonObjectRequest)
    }*/
    //Notification test function END ----------------
}