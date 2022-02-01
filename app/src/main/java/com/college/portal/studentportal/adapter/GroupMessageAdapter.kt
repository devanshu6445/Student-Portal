package com.college.portal.studentportal.adapter

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.college.portal.studentportal.R
import com.college.portal.studentportal.data.model.GroupMessageData
import com.google.android.material.imageview.ShapeableImageView

class GroupMessageAdapter(private val context:Context): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var messageList1 = mutableListOf<GroupMessageData>()

    companion object{
        private const val MESSAGE_IMAGE_VIEW = 1
        private const val MESSAGE_TEXT_VIEW = 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == MESSAGE_IMAGE_VIEW) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_group_message_image, parent, false)
            ImageMessageViewHolder(view)
        }else{
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_group_message_text, parent, false)
            TextMessageViewHolder(view)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateMessageList(messageList: MutableList<GroupMessageData>){
        messageList1 = messageList
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.setIsRecyclable(false)
        if (holder is ImageMessageViewHolder){
            loadImageMessageContent(holder,position)
        } else if (holder is TextMessageViewHolder) {
            loadTextMessageContent(holder,position)
        }
    }

    override fun getItemCount(): Int {
        return messageList1.size
    }

    private fun loadTextMessageContent(textMessageViewHolder: TextMessageViewHolder, position: Int){
         val messageData = messageList1[position]

        textMessageViewHolder.textMessageSenderName.text = messageData.senderName

        textMessageViewHolder.textMessageView.text = messageData.textMessage

        Glide.with(textMessageViewHolder.profileImageSender.context)
            .load(messageData.senderImageURL)
            .placeholder(R.drawable.ic_profile_placeholder)
            .error(R.drawable.ic_profile_placeholder)
            .into(textMessageViewHolder.profileImageSender)
    }

    private fun loadImageMessageContent(imageMessageViewHolder: ImageMessageViewHolder,position: Int){

        val messageData = messageList1[position]
        Glide.with(imageMessageViewHolder.imageMessageView.context)
            .load(messageData.imageURL)
            .placeholder(R.drawable.ic_profile_placeholder) //TODO: change placeholder drawable
            .error(R.drawable.ic_profile_placeholder)
            .priority(Priority.HIGH)
            .into(imageMessageViewHolder.imageMessageView)




        Glide.with(imageMessageViewHolder.profileImageSender.context)
            .load(messageData.senderImageURL)
            .placeholder(R.drawable.ic_profile_placeholder)
            .error(R.drawable.ic_profile_placeholder)
            .into(imageMessageViewHolder.profileImageSender)

        imageMessageViewHolder.imageMessageSenderName.text = messageData.senderName
    }

    override fun getItemViewType(position: Int): Int {
        val groupMessageData = messageList1[position]
        Log.d("messageAdapter", groupMessageData.textMessage)

        if (!groupMessageData.imageURL.equals("")) {
            Log.d("messageAdapter","imageView")
            return MESSAGE_IMAGE_VIEW
        }else {
            Log.d("messageAdapter","textView")
            return MESSAGE_TEXT_VIEW
        }
    }
    class ImageMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val imageMessageView:ShapeableImageView = itemView.findViewById<ShapeableImageView>(R.id.group_imageMessage)
        val imageMessageSenderName:TextView = itemView.findViewById<TextView>(R.id.group_imageMessage_Sender)
        val profileImageSender: ShapeableImageView = itemView.findViewById<ShapeableImageView>(R.id.imageMessage_senderImage)
    }

    class TextMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val textMessageView:TextView = itemView.findViewById<TextView>(R.id.group_textMessage)
        val textMessageSenderName:TextView = itemView.findViewById<TextView>(R.id.group_textMessage_senderName)
        val profileImageSender: ShapeableImageView = itemView.findViewById<ShapeableImageView>(R.id.textMessage_senderImage)
    }
}