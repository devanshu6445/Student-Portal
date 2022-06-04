package com.college.portal.studentportal.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.college.portal.studentportal.R
import com.college.portal.studentportal.data.model.LeaveRequest
import com.college.portal.studentportal.roomDatabase.user.CurrentUserDatabase
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

class LeaveRequestAdapter(private val context:Context): RecyclerView.Adapter<RecyclerView.ViewHolder>(),RecyclerViewAdapterUpdate<LeaveRequest> {
    private val leaveRequestList = mutableListOf<LeaveRequest>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.leave_request_item,parent,false))
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder = holder as ViewHolder
        val leaveRequest = leaveRequestList[position]
        if(leaveRequest.status.isEmpty()){
            viewHolder.leaveRequestPending.visibility = View.VISIBLE
            viewHolder.leaveApprovedStatus.visibility = View.GONE
        } else{
            viewHolder.leaveRequestPending.visibility = View.GONE
            viewHolder.leaveApprovedStatus.visibility = View.VISIBLE
            if(leaveRequest.status == "approved"){
                viewHolder.leaveApprovedStatus.setImageDrawable(AppCompatResources.getDrawable(context,R.drawable.ic_approve_circle))
            }else{
                viewHolder.leaveApprovedStatus.setImageDrawable(AppCompatResources.getDrawable(context,R.drawable.ic_reject_circle))
            }
        }
        val fromDateS  = StringBuilder().append("From: ").append(leaveRequest.fromDate).toString()
        val toDateS  = StringBuilder().append("To: ").append(leaveRequest.toDate).toString()
        viewHolder.apply {
            fromDate.text = fromDateS
            toDate.text = toDateS
        }

        viewHolder.leaveReason.text = leaveRequest.leaveReason
    }

    override fun getItemCount(): Int {
        return leaveRequestList.size
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val leaveReason:TextView = itemView.findViewById(R.id.reasonLR)
        val leaveApprovedStatus:ImageView = itemView.findViewById(R.id.leaveRequestApproveStatus)
        val leaveRequestPending:TextView = itemView.findViewById(R.id.leaveRequestPending)
        val fromDate:TextView = itemView.findViewById(R.id.fromDateLR)
        val toDate:TextView = itemView.findViewById(R.id.toDateLR)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun updateList(updatedList: List<LeaveRequest>) {
        leaveRequestList.clear()
        leaveRequestList.addAll(updatedList)
        notifyDataSetChanged()
    }
}

class LeaveRequestApprovingAdapter(private val database: CurrentUserDatabase):RecyclerView.Adapter<RecyclerView.ViewHolder>(),RecyclerViewAdapterUpdate<LeaveRequest>{
    private val leaveRequestList = mutableListOf<LeaveRequest>()
    var requestToUpload:LeaveRequest? = null
    val approved = MutableLiveData(false)
    val declined = MutableLiveData(false)
    private val ioScope = CoroutineScope(Dispatchers.IO+ Job())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return LeaveRequestViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.leave_request_approve_item,parent,false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder = holder as LeaveRequestViewHolder
        val leaveRequest = leaveRequestList[position]

        viewHolder.apply {
            requestName.text = leaveRequest.requesterName
            leaveReason.text = leaveRequest.leaveReason
        }

        viewHolder.approved.setOnClickListener {
            requestToUpload = leaveRequest.apply {
                status = "approved"
            }
            approved.value = true
            approved.value = false
        }
        viewHolder.declined.setOnClickListener {
            requestToUpload = leaveRequest.apply {
                status = "declined"
            }
            declined.value = true
            declined.value = false
        }
        ioScope.launch {
            val uid = leaveRequest.requesterUid.trim()
            val requester = database.getCurrentUserDao().getStudent(uid)
            withContext(Dispatchers.Main){
                Glide.with(viewHolder.requesterProfileImage.context)
                    .load(requester.userImageUrl?:"URL")
                    .placeholder(R.drawable.sample)
                    .error(R.drawable.ic_avatar_svgrepo_com)
                    .priority(Priority.HIGH)
                    .into(viewHolder.requesterProfileImage)
            }
        }
        val date = StringBuilder("From: ")
            .append(leaveRequest.fromDate)
            .append(" To ")
            .append(leaveRequest.toDate)
            .toString()
        val spanDate = SpannableString(date)
        spanDate.setSpan(UnderlineSpan(),0,spanDate.length,0)
        viewHolder.durationInDays.text = leaveRequest.duration
        viewHolder.dateDuration.text = spanDate
    }

    override fun getItemCount(): Int {
        return leaveRequestList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun updateList(updatedList: List<LeaveRequest>) {
        leaveRequestList.clear()
        leaveRequestList.addAll(updatedList)
        notifyDataSetChanged()
    }

    private class LeaveRequestViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val requestName:TextView = itemView.findViewById(R.id.requesterName)
        val requesterProfileImage:ImageView = itemView.findViewById(R.id.requesterProfileImage)
        val leaveReason:TextView = itemView.findViewById(R.id.leaveRequestReason)
        val dateDuration:TextView = itemView.findViewById(R.id.durationDate)
        val durationInDays:TextView = itemView.findViewById(R.id.durationInDays)
        val approved:Button = itemView.findViewById(R.id.approveRequest)
        val declined:Button = itemView.findViewById(R.id.rejectRequest)
    }
}

interface RecyclerViewAdapterUpdate<T>{

    fun updateList(updatedList:List<T>)
}