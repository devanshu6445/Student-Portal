package com.college.portal.studentportal.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.college.portal.studentportal.R
import com.college.portal.studentportal.roomDatabase.groupEverything.MessageRequest
import com.college.portal.studentportal.ui.groupMessageRequest.MessageRequestDiffCallback
import com.college.portal.studentportal.ui.groupMessageRequest.MessageRequestRepository
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.*

class MessageRequestAdapter(private val context: Context): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val messageRequestList = mutableListOf<MessageRequest>()
    private val messageRequestRepository = MessageRequestRepository.getRepo()
    private val ioScope = CoroutineScope(Dispatchers.IO + Job())
    val isUpdated = MutableLiveData(true)
    var isEmpty = MutableLiveData(this.itemCount)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message_request,parent,false)
        return MessageRequestViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val messageRequest = messageRequestList[position]
        val messageRequestViewHolder = holder as MessageRequestViewHolder
        if (messageRequest.docURL == ""){
            messageRequestViewHolder.apply {
                requestedMessage.text = messageRequest.textMessage
                requestAttachments.isEnabled = false
                requestAttachments.alpha = 0.5f
            }
        }else{
                if(messageRequest.textMessage == ""){
                messageRequestViewHolder.apply {
                    requestedMessage.setTypeface(requestedMessage.typeface,Typeface.BOLD)
                    requestedMessage.text = context.getString(R.string.no_message_found)
                }
            }else{
                messageRequestViewHolder.apply {
                    requestedMessage.text = messageRequest.textMessage
                }
            }
        }
        messageRequestViewHolder.apply {
            requesterName.text = messageRequest.senderName
        }

        messageRequestViewHolder.approveRequest.setOnClickListener {
            ioScope.launch {
                messageRequestRepository?.messageRequestApproved(messageRequest)
            }
        }
        messageRequestViewHolder.rejectRequest.setOnClickListener {
            ioScope.launch {
                messageRequestRepository?.messageRequestRejected(messageRequest)
            }
        }

        messageRequestViewHolder.requestAttachments.setOnClickListener {
            createPopUpWindow(messageRequestViewHolder.requestAttachments,R.layout.message_request_attachments,messageRequest.docURL)
        }
    }
    @SuppressLint("InflateParams")
    private fun createPopUpWindow(view:View, layout:Int,url:String){
        val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView = layoutInflater.inflate(layout,null)
        val messageAttachment = popupView.findViewById<ImageView>(R.id.messageRequestAttachment)
        Glide.with(messageAttachment.context)
            .load(url)
            .centerCrop()
            .placeholder(R.drawable.ic_profile_placeholder)
            .into(messageAttachment)
        val width = LinearLayout.LayoutParams.MATCH_PARENT
        val height = LinearLayout.LayoutParams.MATCH_PARENT
        val focusable = true

        val popupWindow = PopupWindow(popupView,width,height,focusable)
        popupWindow.showAtLocation(view,Gravity.CENTER,0,0)
        val v = popupView.findViewById<View>(R.id.mroot)
        messageAttachment.setOnClickListener {

        }
        v.setOnClickListener {
            popupWindow.dismiss()
        }
    }

    override fun getItemCount(): Int {
        return messageRequestList.size
    }

    private class MessageRequestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val requesterName:TextView = itemView.findViewById(R.id.requesterName)
        val requestedMessage:TextView = itemView.findViewById(R.id.requestMessage)
        val approveRequest:MaterialButton = itemView.findViewById(R.id.approveRequest)
        val rejectRequest:MaterialButton = itemView.findViewById(R.id.rejectRequest)
        val requestAttachments:ImageButton = itemView.findViewById(R.id.requestAttachments)
    }

    fun updateMessageRequestList(newMessageRequestList: List<MessageRequest>){
        val obj = this
        ioScope.launch {
            val messageRequestDiffCallback = MessageRequestDiffCallback(messageRequestList,newMessageRequestList)
            val result = DiffUtil.calculateDiff(messageRequestDiffCallback)
            messageRequestList.clear()
            messageRequestList.addAll(newMessageRequestList)
            result.dispatchUpdatesTo(obj)
            withContext(Dispatchers.Main){
                isUpdated.value = true
                isUpdated.value = false
            }
        }
    }
}