package com.college.portal.studentportal.adapter

import android.Manifest
import android.annotation.SuppressLint
import android.content.*
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.college.portal.studentportal.BuildConfig
import com.college.portal.studentportal.R
import com.college.portal.studentportal.roomDatabase.groupEverything.GroupMessageDatabase
import com.college.portal.studentportal.roomDatabase.groupEverything.GroupMessageInfo
import com.college.portal.studentportal.roomDatabase.groupEverything.Participant
import com.college.portal.studentportal.ui.group_message.GroupMessageRepository
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import java.io.File

class GroupMessageAdapter(
    private val context: Context,
    private var messageList1: List<GroupMessageInfo>,
    activity: AppCompatActivity, // may be a memory leak don't know
    private val database: GroupMessageDatabase,
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val ioScope = CoroutineScope(Dispatchers.IO + Job())
    private lateinit var holder: RecyclerView.ViewHolder
    private lateinit var currentMessageObject: GroupMessageInfo

    private val groupMessageRepo = GroupMessageRepository.getInstance()
    private var currentParticipant: Participant? = null

    companion object{
        private const val MESSAGE_IMAGE_VIEW = 1
        private const val MESSAGE_TEXT_VIEW = 2
        private const val MESSAGE_DOCUMENT_VIEW = 3
        private const val TAG = "GroupMessageAdapter: "
    }

    init {
        ioScope.launch {
            groupMessageRepo?.getCurrentParticipant()?.collect {
                currentParticipant = it
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            MESSAGE_IMAGE_VIEW -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_group_message_image, parent, false)
                ImageMessageViewHolder(view)
            }
            MESSAGE_DOCUMENT_VIEW -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_document_group_message,parent,false)
                DocumentMessageViewHolder(view)
            }
            else ->{
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_group_message_text, parent, false)
                TextMessageViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        //val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        this.holder = holder
        val message = messageList1[position]
        currentMessageObject = message
        when(holder){
            is ImageMessageViewHolder -> {loadImageMessageContent(holder, position)}
            is DocumentMessageViewHolder -> {documentMessage(holder,position)}
            is TextMessageViewHolder -> {
                holder.itemView.setOnLongClickListener {
                    val role = currentParticipant?.role
                    val items: Array<String> = if(role == "admin"||role == "teacher"||role == "moderator")
                        arrayOf("Message Info","Copy Message","Delete Message","Edit Message")
                    else
                        arrayOf("Message Info","Copy Message")
                    MaterialAlertDialogBuilder(holder.itemView.context,R.style.ThemeOverlay_App_MaterialAlertDialog)
                        .setItems(items){_,which ->
                            when(which){
                                0 -> {
                                    MaterialAlertDialogBuilder(holder.itemView.context)
                                        .setTitle("Message Info")
                                        .setMessage(
                                            "Message: ${message.textMessage} \n Sent By: ${message.senderName} \nApproved By(UID): ${message.approvedBy} \nSent time: ${message.messageTimeStamp}"
                                        )
                                        .setNeutralButton("Cancel"){ dialog, _ ->
                                            dialog.dismiss()
                                        }
                                        .show()
                                }
                                1 -> {
                                    val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val data = ClipData.newPlainText("message",message.textMessage)
                                    clipboardManager.setPrimaryClip(data)
                                    Toast.makeText(context, "Copied", Toast.LENGTH_SHORT).show()
                                }
                                2 -> {
                                    groupMessageRepo?.deleteMessage(message)
                                }
                                3 -> {
                                    Toast.makeText(context, items[which], Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                        .show()
                    return@setOnLongClickListener false
                }
                loadTextMessageContent(holder,position)
            }
        }
    }

    private fun documentMessage(holder: DocumentMessageViewHolder, position: Int) {
        val message = messageList1[position]
        holder.apply {
            documentSize.text = message.docSize
            documentType.text = message.docType
            documentMessageSenderName.text = message.senderName
            Glide.with(documentMessageProfileImage.context)
                .load(message.senderImageURL)
                .placeholder(R.drawable.ic_profile_placeholder)
                .error(R.drawable.ic_profile_placeholder)
                .into(documentMessageProfileImage)
        }
        if (message.docURL.startsWith("http")) {
            holder.documentDownload.visibility = View.VISIBLE
            holder.documentDownload.setOnClickListener {
                holder.documentDownloadProgress.visibility = View.VISIBLE
                it.visibility = View.INVISIBLE
                checkAndRequestPermission(message,holder)
            }
        } else {
            holder.documentDownload.visibility = View.GONE
            holder.documentName.setOnClickListener {
                val file = File(message.docURL)
                val uri = FileProvider.getUriForFile(
                    context,
                    BuildConfig.APPLICATION_ID + ".provider",
                    file)
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(uri,message.mimeType)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                try {
                    context.startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(context, "No app found to open the file", Toast.LENGTH_SHORT).show()
                }
            }
        }
        if (message.textMessage.length >20){
            holder.documentName.text = message.textMessage.substring(0,20)
        }else{
            holder.documentName.text = message.textMessage
        }


    }
    private fun checkAndRequestPermission(message:GroupMessageInfo,holder: DocumentMessageViewHolder){
        if(context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            permissionResult.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }else{
            ioScope.launch {
                documentDownload(message,holder)
            }
        }
    }
    private val permissionResult = activity.registerForActivityResult(ActivityResultContracts.RequestPermission()){ isGranted ->
        val documentHolder = holder as DocumentMessageViewHolder
        if(isGranted){
            ioScope.launch {
                documentDownload(currentMessageObject, holder as DocumentMessageViewHolder)
            }
        }else{
            documentHolder.apply {
                documentDownload.visibility = View.VISIBLE
                documentDownloadProgress.visibility = View.INVISIBLE
            }
            Toast.makeText(context, "Please Grant the permission before proceeding", Toast.LENGTH_SHORT).show()
        }
    }
    @Suppress("DEPRECATION")
    private suspend fun documentDownload(message: GroupMessageInfo,holder: DocumentMessageViewHolder){

        lateinit var storageReference: StorageReference
        try {
            storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(message.docURL)
        }catch (e: IllegalArgumentException){
            Log.e(TAG, "documentDownload: ${e.message}",e )
            return
        }
        val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).absolutePath+"/"+message.textMessage)
        kotlin.runCatching {
            file.createNewFile()
        }
        storageReference
            .getFile(Uri.fromFile(file))
            .addOnSuccessListener {
                ioScope.launch {
                    val message1 = message.apply {
                        docURL = file.absolutePath
                    }
                    database.getGroupMessageDao().updateMessage(message1)
                    withContext(Dispatchers.Main){
                        holder.documentDownloadProgress.visibility = View.GONE
                        Toast.makeText(context, "File Downloaded", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .addOnProgressListener {
                val progress = ((100.0*(it.bytesTransferred)/it.totalByteCount)).toInt()
                    holder.documentDownloadProgress.progress = progress
            }
    }
    override fun getItemCount(): Int {
        return messageList1.size
    }

    override fun getItemViewType(position: Int): Int {
        val groupMessageData = messageList1[position]
        Log.d("messageAdapter", groupMessageData.textMessage)

        return if (groupMessageData.docURL != "") {
            if(groupMessageData.mimeType.startsWith("image/"))
                MESSAGE_IMAGE_VIEW
            else
                MESSAGE_DOCUMENT_VIEW
        }else {
            Log.d("messageAdapter","textView")
            MESSAGE_TEXT_VIEW
        }
    }

    /*fun loadCheck(delay:Long){
        when{
            oldList.size+25<newList.size ->{
                messageLoadingProgressBar.visibility = View.VISIBLE
                load(delay = delay)
            }
            oldList.size<newList.size ->{
                messageLoadingProgressBar.visibility = View.VISIBLE
                load(newList.size-oldList.size,delay)
            }
            oldList.size>newList.size -> {
                refreshList()
            }
            else -> {
                Toast.makeText(context, "Can't load any more messages", Toast.LENGTH_SHORT).show()
            }
        }
    }*/

    /*private fun refreshList(){
        val obj = this
        val groupMessageDiffUtilCallback = GroupMessageDiffUtilCallback(messageList1,oldList)
            val result = DiffUtil.calculateDiff(groupMessageDiffUtilCallback)

            result.dispatchUpdatesTo(obj)
    }*/

    /*private fun load(toLoad:Int = 25,delay:Long = 100){
        val oldSize = oldList.size
        val newSize = oldSize+toLoad
        oldList.clear()
        for(i in newSize-1 downTo 0){
            oldList.add(newList[i])
        }
        val obj = this
        val groupMessageDiffUtilCallback = GroupMessageDiffUtilCallback(messageList1,oldList)
        ioScope.launch(Dispatchers.Main) {
            *//*val result = DiffUtil.calculateDiff(groupMessageDiffUtilCallback)*//*
            delay(delay)

            obj.notifyDataSetChanged()

            messageLoadingProgressBar.visibility = View.GONE
            *//*result.dispatchUpdatesTo(obj)*//*
        }
    }*/

    @SuppressLint("NotifyDataSetChanged")
    fun updateMessageList(messageList: List<GroupMessageInfo>){

        /*newList.clear()
        newList.addAll(messageList)
        try {
            loadCheck(0)
        } catch (e: IndexOutOfBoundsException) {

        }*/
        messageList1 = messageList
        notifyDataSetChanged()
    }

    private fun createPOP(view:View,layout:Int,url:String){
        val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView = layoutInflater.inflate(layout,null)
        val imageMessageFullView = popupView.findViewById<ImageView>(R.id.imageMessageFullView)
        lateinit var popupWindow: PopupWindow
        Glide.with(imageMessageFullView.context)
            .load(url)
            .into(imageMessageFullView)
        val width = LinearLayout.LayoutParams.MATCH_PARENT
        val height = LinearLayout.LayoutParams.MATCH_PARENT
        popupWindow = PopupWindow(popupView,width,height,true).apply {
            animationStyle = R.style.popup_window_animation_phone
        }
        popupWindow.showAtLocation(view, Gravity.CENTER,0,0)
        popupView.findViewById<ImageView>(R.id.close_full_image_message_view).setOnClickListener {
            popupWindow.dismiss()
        }
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
            .load(messageData.docURL)
            .placeholder(R.drawable.ic_profile_placeholder) //TODO: change placeholder drawable
            .error(R.drawable.ic_profile_placeholder)
            .priority(Priority.HIGH)
            .into(imageMessageViewHolder.imageMessageView)

        imageMessageViewHolder.imageMessageView.setOnClickListener {
            createPOP(it,R.layout.image_message_full_view,messageData.docURL)
        }

        Glide.with(imageMessageViewHolder.profileImageSender.context)
            .load(messageData.senderImageURL)
            .placeholder(R.drawable.ic_profile_placeholder)
            .error(R.drawable.ic_profile_placeholder)
            .into(imageMessageViewHolder.profileImageSender)

        imageMessageViewHolder.imageMessageSenderName.text = messageData.senderName
    }

    class ImageMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val imageMessageView:ShapeableImageView = itemView.findViewById(R.id.group_imageMessage)
        val imageMessageSenderName:TextView = itemView.findViewById(R.id.group_imageMessage_Sender)
        val profileImageSender: ShapeableImageView = itemView.findViewById(R.id.imageMessage_senderImage)
    }

    class TextMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val textMessageView:TextView = itemView.findViewById(R.id.group_textMessage)
        val textMessageSenderName:TextView = itemView.findViewById(R.id.group_textMessage_senderName)
        val profileImageSender: ShapeableImageView = itemView.findViewById(R.id.textMessage_senderImage)
    }

    class DocumentMessageViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val documentName:TextView = itemView.findViewById(R.id.documentName)
        val documentSize:TextView = itemView.findViewById(R.id.documentSize)
        val documentType:TextView = itemView.findViewById(R.id.documentMimeType)
        val documentDownload:ImageButton = itemView.findViewById(R.id.documentDownload)
        val documentDownloadProgress:ProgressBar = itemView.findViewById(R.id.documentDownloadProgress)
        val documentMessageProfileImage:ShapeableImageView = itemView.findViewById(R.id.documentMessageProfileImage)
        val documentMessageSenderName:TextView = itemView.findViewById(R.id.documentMessageSenderName)
    }
}