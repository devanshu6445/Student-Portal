package com.college.portal.studentportal.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.college.portal.studentportal.R
import com.college.portal.studentportal.roomDatabase.groupEverything.MessageRequest
import com.college.portal.studentportal.ui.groupMessageRequest.MessageRequestDiffCallback

class MessageRequestAdapter(private val context: Context): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val messageRequestList = mutableListOf<MessageRequest>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message_request,parent,false)
        return MessageRequestViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        Log.d("MessageRequestAdapter","Called")
        val messageRequest = messageRequestList[position]
        val messageRequestViewHolder = holder as MessageRequestViewHolder
        messageRequestViewHolder.apply {
            requesterName.text = messageRequest.senderName
            requestedMessage.text = messageRequest.textMessage
        }
    }

    override fun getItemCount(): Int {
        return messageRequestList.size
    }

    private class MessageRequestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val requesterName:TextView = itemView.findViewById(R.id.requesterName)
        val requestedMessage:TextView = itemView.findViewById(R.id.requestMessage)
    }

    fun updateMessageRequestList(newMessageRequestList: List<MessageRequest>){
        val messageRequestDiffCallback = MessageRequestDiffCallback(messageRequestList,newMessageRequestList)
        val result = DiffUtil.calculateDiff(messageRequestDiffCallback)
        messageRequestList.clear()
        messageRequestList.addAll(newMessageRequestList)
        result.dispatchUpdatesTo(this)
    }
}