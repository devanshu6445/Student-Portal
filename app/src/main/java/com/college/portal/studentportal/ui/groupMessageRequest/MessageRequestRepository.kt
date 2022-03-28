package com.college.portal.studentportal.ui.groupMessageRequest

import android.util.Log
import com.college.portal.studentportal.roomDatabase.groupEverything.GroupMessageDatabase
import com.college.portal.studentportal.roomDatabase.groupEverything.MessageRequest
import com.college.portal.studentportal.roomDatabase.groups.BasicGroupData
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

class MessageRequestRepository(private val database: GroupMessageDatabase,
                               private val basicGroupData: BasicGroupData) {

    private val firebaseDatabase = Firebase.database.reference
    private lateinit var messageRequestReference: DatabaseReference
    private lateinit var messageRequestChildEventListener: ChildEventListener
    private val ioScope = CoroutineScope(Dispatchers.IO + Job())

    companion object{
        private const val TAG = "MessageRequestRepository: "
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

        messageRequestChildEventListener = messageRequestReference.
        addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val messageRequest = snapshot.getValue<MessageRequest>()
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
                val messageRequest = snapshot.getValue<MessageRequest>()
                if (messageRequest!=null){
                    ioScope.launch {
                        database.getGroupMessageDao().removeMessageRequest(messageRequest)
                    }
                }
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                Log.i(TAG, "onChildMoved: Message request moved(Priority)")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "onCancelled: ${error.message}", error.toException() )
            }
        })
    }

    fun deregisterMessageRequestListener(){
        messageRequestReference.removeEventListener(messageRequestChildEventListener)
    }
}