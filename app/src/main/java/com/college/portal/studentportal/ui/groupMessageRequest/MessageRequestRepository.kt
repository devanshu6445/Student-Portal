package com.college.portal.studentportal.ui.groupMessageRequest

import android.util.Log
import com.college.portal.studentportal.data.model.MessageRequestNetwork
import com.college.portal.studentportal.data.model.SendMessageObject
import com.college.portal.studentportal.roomDatabase.groupEverything.GroupMessageDatabase
import com.college.portal.studentportal.roomDatabase.groupEverything.MessageRequest
import com.college.portal.studentportal.roomDatabase.groups.BasicGroupData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class MessageRequestRepository private constructor(private val database: GroupMessageDatabase,
                                                   private val basicGroupData: BasicGroupData){

    private val firebaseDatabase = Firebase.database.reference
    private lateinit var messageRequestReference: DatabaseReference
    private lateinit var messageRequestChildEventListener: ChildEventListener
    private val user = FirebaseAuth.getInstance().currentUser

    private val ioScope = CoroutineScope(Dispatchers.IO + Job())

    companion object{
        private const val TAG = "MessageRequestRepository: "
        @Volatile
        private var INSTANCE: MessageRequestRepository? = null

        //will create a single instance of MessageRequestRepo
        fun createRepo(database: GroupMessageDatabase,
                       basicGroupData: BasicGroupData): MessageRequestRepository {

            return INSTANCE?:synchronized(this){
                val instance = MessageRequestRepository(database,basicGroupData)
                INSTANCE = instance
                instance
            }
        }

        //call only when you are sure createRepo will be called first
        fun getRepo(): MessageRequestRepository? = INSTANCE
    }

    fun getAllMessageRequests(): Flow<List<MessageRequest>> {
        return database.getGroupMessageDao().getAllMessageRequests()
    }
    fun registerMessageRequestListener(){
        messageRequestReference = firebaseDatabase
            .child("groups")
            .child(basicGroupData.groupSemester)
            .child(basicGroupData.groupName)
            .child("messages-requests")

        messageRequestChildEventListener = messageRequestReference
            .addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val messageRequestNetwork = snapshot.getValue<MessageRequestNetwork>()
                val messageRequest = messageRequestNetwork?.let {
                    MessageRequest(
                        it.senderName,
                        it.textMessage,
                        it.docURL,
                        it.senderUid,
                        it.senderImageURL,
                        it.messageID,
                        it.mimeType,
                        it.messageTimeStamp
                    )
                }
                if (messageRequest!=null){
                    ioScope.launch {
                        database.getGroupMessageDao().insertMessageRequest(messageRequest)
                    }
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                Log.d(TAG, "onChildChanged: Message request data changed")
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val messageRequestNetwork = snapshot.getValue<MessageRequestNetwork>()
                /*val messageRequest = messageRequestNetwork?.let {
                    MessageRequest(
                        it.senderName,
                        it.textMessage,
                        it.docURL,
                        it.senderUid,
                        it.senderImageURL,
                        it.messageID,
                        it.mimeType,
                        it.messageTimeStamp
                    )
                }*/
                if (messageRequestNetwork!=null){
                    ioScope.launch {
                        database.getGroupMessageDao().removeMessageRequest(messageRequestNetwork.messageID)
                    }
                }
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                Log.i(TAG, "onChildMoved: Message request moved(Priority)")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "onCancelled: ${error.message}", error.toException())
            }
        })
    }

    fun messageRequestApproved(messageRequest: MessageRequest){
        val timestamp = System.currentTimeMillis()
        firebaseDatabase
            .child("groups")
            .child(basicGroupData.groupSemester)
            .child(basicGroupData.groupName)
            .child("messages-requests")
            .child(messageRequest.messageTimeStamp.toString())
            .removeValue()
        
        val uid = user?.uid!!
        val groupMessageInfo = SendMessageObject(
            messageRequest.senderName,
            messageRequest.textMessage,
            messageRequest.docURL,
            messageRequest.senderUid,
            messageRequest.senderImageURL,
            uid,
            messageRequest.messageID,
            messageRequest.mimeType,
            timestamp
        )
        Log.d(TAG, "messageRequestApproved: Object Created ${groupMessageInfo.senderUid}")

        firebaseDatabase
            .child("groups")
            .child(basicGroupData.groupSemester)
            .child(basicGroupData.groupName)
            .child("messages")
            .child(timestamp.toString())
            .setValue(groupMessageInfo)

        Log.d(TAG, "messageRequestApproved: message approved")
    }

    fun messageRequestRejected(messageRequest: MessageRequest){
        firebaseDatabase
            .child("groups")
            .child(basicGroupData.groupSemester)
            .child(basicGroupData.groupName)
            .child("messages-requests")
            .child(messageRequest.messageTimeStamp.toString())
            .removeValue()
    }

    fun deregisterMessageRequestListener(){
        messageRequestReference.removeEventListener(messageRequestChildEventListener)
    }
}