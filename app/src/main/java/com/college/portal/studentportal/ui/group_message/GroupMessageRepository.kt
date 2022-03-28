package com.college.portal.studentportal.ui.group_message

import android.util.Log
import com.college.portal.studentportal.data.model.GroupMessageData
import com.college.portal.studentportal.data.model.LoggedInUser
import com.college.portal.studentportal.roomDatabase.groupEverything.GroupMessageDatabase
import com.college.portal.studentportal.roomDatabase.groupEverything.GroupMessageInfo
import com.college.portal.studentportal.roomDatabase.groupEverything.Participant
import com.college.portal.studentportal.roomDatabase.groups.BasicGroupData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import java.util.*

class GroupMessageRepository(val database: GroupMessageDatabase,
                             val basicGroupData: BasicGroupData){

    private val firebaseDatabase =  Firebase.database
    private val databaseReference = firebaseDatabase.reference

    private lateinit var reference: DatabaseReference
    private lateinit var childEventListener: ChildEventListener
    private lateinit var participantDatabaseReference: DatabaseReference
    private val ioScope = CoroutineScope(Dispatchers.IO + Job())
    private lateinit var participantChildEventListener: ChildEventListener


    companion object{
        private const val TAG = "GroupMessageRepository: "
    }

    fun retrieveMessages(){
        var exist = true

        reference = databaseReference.child("groups")
            .child(basicGroupData.groupSemester)
            .child(basicGroupData.groupName)
            .child("messages")

             childEventListener = reference.addChildEventListener(object: ChildEventListener{
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val groupMessageData = snapshot.getValue<GroupMessageInfo>()
                        if (exist){
                            if (groupMessageData!=null){
                                GlobalScope.launch(Dispatchers.IO) {
                                    val messageData: GroupMessageInfo? = database.getGroupMessageDao().getMessagesNotLiveData(groupMessageData.messageTimeStamp)
                                    if (messageData == null){
                                        Log.d("message-double",groupMessageData.textMessage)
                                        database.getGroupMessageDao().insertJava(groupMessageData)
                                        exist = false
                                    }else
                                        Log.d("message-not",groupMessageData.textMessage)
                                }
                            }
                        } else {
                            if (groupMessageData!=null) {
                                GlobalScope.launch(Dispatchers.IO) {
                                    database.getGroupMessageDao().insertJava(groupMessageData)
                                }
                            }
                        }
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    TODO("Not yet implemented")
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    val groupMessageData = snapshot.getValue<GroupMessageInfo>()
                    if(groupMessageData!=null)
                    GlobalScope.launch {
                        database.getGroupMessageDao().deleteMessage(groupMessageData)
                    }
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                    Log.i(TAG,"Message(Child) priority changed")
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG,error.message +  " "  + error.details)
                }

            })
    }
    fun deRegister(){
        reference.removeEventListener(childEventListener)
        participantDatabaseReference.removeEventListener(participantChildEventListener)
    }

    fun registerParticipantListener(){
        participantDatabaseReference = databaseReference.child("groups")
            .child(basicGroupData.groupSemester)
            .child(basicGroupData.groupName)
            .child("participants")

        participantChildEventListener = participantDatabaseReference.addChildEventListener(object : ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val participant = snapshot.getValue<Participant>()
                if(participant != null){
                    ioScope.launch {
                        val participantLocalDatabase = database.getGroupMessageDao().searchParticipant(participant.uid)
                        if (participantLocalDatabase == null){
                            database.getGroupMessageDao().insertParticipant(participant)
                        }else
                            database.getGroupMessageDao().updateParticipant(participant)
                    }
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val participant = snapshot.getValue<Participant>()
                if (participant!=null){
                    ioScope.launch {
                        database.getGroupMessageDao().updateParticipant(participant)
                    }
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val participant = snapshot.getValue<Participant>()
                if (participant!=null){
                    ioScope.launch {
                        database.getGroupMessageDao().deleteParticipant(participant)
                    }
                }
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                Log.d(TAG,"Participant child moved(Priority")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG,error.message,error.toException())
            }

        })
    }

    //Just a test message send method

    //TODO: Rewrite this method
    private var check = false
    fun sendMessage(message:String,loggedInUser: LoggedInUser): Boolean{
        val currentParticipant = database.getGroupMessageDao().searchParticipant(loggedInUser.userUid)

        if(currentParticipant!=null){
            if (currentParticipant.role == "moderator" ||
                currentParticipant.role == "teacher" ||
                currentParticipant.role == "admin"
            ){
                sendMessageForHigherLevelAccess(message,loggedInUser)
                return true
            }else{
                sendMessageForLowerLevelAccess(message,loggedInUser)
                return true
            }
        }else if(check){
            return false
        }else{
            if (check)
                return false
                databaseReference.child("groups")
                    .child(basicGroupData.groupSemester)
                    .child(basicGroupData.groupName)
                    .child("participants")
                    .get()
                    .addOnSuccessListener {
                        val participantList = mutableListOf<Participant>()
                        for (snapshot in it.children){
                            val participant = snapshot.getValue<Participant>()
                            if(participant!=null)
                                participantList.add(participant)

                                ioScope.launch {
                                    database.getGroupMessageDao().insertParticipantInBulk(participantList)
                                    check = true
                                    sendMessage(message,loggedInUser)
                                }
                        }
                    }
        }
        return false
    }

    private fun sendMessageForHigherLevelAccess(message: String,loggedInUser: LoggedInUser){
        val uuid = UUID.randomUUID().toString()
        val timestamp = System.currentTimeMillis()
        val groupMessageData = GroupMessageInfo(
            loggedInUser.userName,
            message,
            "",
            loggedInUser.userUid,
            loggedInUser.userImageUrl,
            loggedInUser.userUid,
            uuid,
            timestamp
        )
        databaseReference.child("groups")
            .child(basicGroupData.groupSemester)
            .child(basicGroupData.groupName)
            .child("messages")
            .child(timestamp.toString())
            .setValue(groupMessageData)
    }

    private fun sendMessageForLowerLevelAccess(message: String,loggedInUser: LoggedInUser){
        val uuid = UUID.randomUUID().toString()
        val timestamp = System.currentTimeMillis()
        val groupMessageData = GroupMessageInfo(
            loggedInUser.userName,
            message,
            "",
            loggedInUser.userUid,
            loggedInUser.userImageUrl,
            "",
            uuid,
            timestamp
        )
        databaseReference.child("groups")
            .child(basicGroupData.groupSemester)
            .child(basicGroupData.groupName)
            .child("messages-requests")
            .child(timestamp.toString())
            .setValue(groupMessageData)
    }
    private val user = FirebaseAuth.getInstance().currentUser
    fun getCurrentParticipant(): Flow<Participant?>{
        return database.getGroupMessageDao().searchParticipantFlow(user?.uid!!)
    }
}