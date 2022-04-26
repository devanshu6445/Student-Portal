package com.college.portal.studentportal.ui.group_message

import android.database.sqlite.SQLiteConstraintException
import android.net.Uri
import android.util.Log
import com.college.portal.studentportal.data.model.LoggedInUser
import com.college.portal.studentportal.data.model.SendMessageObject
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
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.io.File
import java.util.*

class GroupMessageRepository(
    private val database: GroupMessageDatabase,
    private val basicGroupData: BasicGroupData
) {


    private val firebaseDatabase = Firebase.database
    private val databaseReference = firebaseDatabase.reference
    private val storage = FirebaseStorage.getInstance().reference
    private val firebaseStorage = FirebaseStorage.getInstance()
    private lateinit var messageImageReference: StorageReference

    private lateinit var reference: DatabaseReference
    private lateinit var childEventListener: ChildEventListener
    private lateinit var participantDatabaseReference: DatabaseReference
    private val ioScope = CoroutineScope(Dispatchers.IO + Job())
    private lateinit var participantChildEventListener: ChildEventListener

    init {
        messageRepo = this
    }
    companion object {
        private const val TAG = "GroupMessageRepository: "
        private var messageRepo: GroupMessageRepository? = null
    /**
     * This function should only be used when you are certain that the object has been created
     */
    fun getInstance(): GroupMessageRepository?{
        return messageRepo
    }

    /**
     * This function is to create Repo
     */
    fun createRepo(database: GroupMessageDatabase,basicGroupData: BasicGroupData): GroupMessageRepository{
        return messageRepo ?: synchronized(this){
            val instance = GroupMessageRepository(database,basicGroupData)
            messageRepo = instance
            instance
        }
    }
    }



    fun updateMessageDatabase() {
        databaseReference.child("groups")
            .child(basicGroupData.groupSemester)
            .child(basicGroupData.groupName)
            .child("messages")
            .get()
            .addOnSuccessListener {
                ioScope.launch {
                    val listOfMessageFromDatabase =
                        database.getGroupMessageDao().getAllMessagesNotLiveData()
                    val listOfMessageFromNetwork = mutableListOf<GroupMessageInfo>()
                    for (snapshot in it.children) {
                        val message = snapshot.getValue<GroupMessageInfo>()
                        if (message != null)
                            listOfMessageFromNetwork.add(message)
                    }
                    val sum = listOfMessageFromDatabase + listOfMessageFromNetwork
                    val map = sum.groupBy { it.messageID }
                        .filter { it.value.size == 1 }
                        .flatMap { it.value }

                    if (map.isNotEmpty())
                        database.getGroupMessageDao().deleteMessageInBulk(map)
                    retrieveMessages()
                }
            }
    }

    private fun retrieveMessages() {
        reference = databaseReference.child("groups")
            .child(basicGroupData.groupSemester)
            .child(basicGroupData.groupName)
            .child("messages")
        var exist = true //Message existence flag for local database
        childEventListener = reference.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val groupMessageData = snapshot.getValue<GroupMessageInfo>()
                if (exist) {
                    if (groupMessageData != null) {
                        ioScope.launch(Dispatchers.IO) {
                            //check if the message exist in local database
                            val messageData: GroupMessageInfo? = database.getGroupMessageDao()
                                .getMessagesNotLiveData(groupMessageData.messageTimeStamp)
                            //messageData will be null if message doesn't already exist in local database
                            if (messageData == null) {
                                Log.d("message-double", groupMessageData.textMessage)
                                if (groupMessageData.docURL != "") {
                                    firebaseStorage.getReferenceFromUrl(groupMessageData.docURL).metadata.addOnSuccessListener {
                                        groupMessageData.apply {
                                            docSize = (it.sizeBytes / 1048576).toString() + "MB"
                                            docType = it.contentType ?: ""
                                            ioScope.launch {
                                                database.getGroupMessageDao()
                                                    .insertJava(groupMessageData)
                                            }
                                        }
                                    }
                                } else {
                                    ioScope.launch {
                                        database.getGroupMessageDao().insertJava(groupMessageData)
                                    }
                                }
                                //Now the exist flag will be set to false
                                // so from this point forward the message will directly be inserted in local database
                                exist = false
                            } else {
                                if (messageData.docURL.startsWith("/")) {
                                    val file = File(messageData.docURL)
                                    if (!file.exists()) {
                                        //TODO: Logic changed needed to be test
                                        messageData.apply {
                                            docURL = groupMessageData.docURL
                                        }
                                        database.getGroupMessageDao()
                                            .updateMessage(messageData)
                                    }
                                }
                                Log.d("message-not", groupMessageData.textMessage)
                            }
                        }
                    }
                } else {
                    if (groupMessageData != null) {
                        if (groupMessageData.docURL != "") {
                            firebaseStorage.getReferenceFromUrl(groupMessageData.docURL).metadata.addOnSuccessListener {
                                groupMessageData.apply {
                                    docSize = (it.sizeBytes / 1048576).toString() + "MB"
                                    docType = it.contentType ?: ""
                                    ioScope.launch {
                                        database.getGroupMessageDao().insertJava(groupMessageData)
                                    }
                                }
                            }
                        } else {
                            ioScope.launch {
                                database.getGroupMessageDao().insertJava(groupMessageData)
                            }
                        }
                    }
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val groupMessageData = snapshot.getValue<GroupMessageInfo>()
                if (groupMessageData != null) {
                    ioScope.launch {
                        try {
                            database.getGroupMessageDao().insertMessage(groupMessageData)
                        } catch (e: SQLiteConstraintException) {
                            Log.e(TAG, e.message, e)
                        }
                    }
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val groupMessageData = snapshot.getValue<GroupMessageInfo>()
                if (groupMessageData != null)
                    ioScope.launch {
                        database.getGroupMessageDao().deleteMessage(groupMessageData)
                    }
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                Log.i(TAG, "Message(Child) priority changed")
                val groupMessageData = snapshot.getValue<GroupMessageInfo>()
                if (groupMessageData != null) {
                    ioScope.launch {
                        database.getGroupMessageDao().insertJava(groupMessageData)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, error.message + " " + error.details)
            }

        })
    }

    fun deRegister() {
        reference.removeEventListener(childEventListener)
        participantDatabaseReference.removeEventListener(participantChildEventListener)
    }

    fun registerParticipantListener() {
        participantDatabaseReference = databaseReference.child("groups")
            .child(basicGroupData.groupSemester)
            .child(basicGroupData.groupName)
            .child("participants")

        participantChildEventListener =
            participantDatabaseReference.addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val participant = snapshot.getValue<Participant>()
                    if (participant != null) {
                        ioScope.launch {
                            val participantLocalDatabase =
                                database.getGroupMessageDao().searchParticipant(participant.uid)
                            if (participantLocalDatabase == null) {
                                database.getGroupMessageDao().insertParticipant(participant)
                            } else
                                database.getGroupMessageDao().updateParticipant(participant)
                        }
                    }
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    val participant = snapshot.getValue<Participant>()
                    if (participant != null) {
                        ioScope.launch {
                            database.getGroupMessageDao().updateParticipant(participant)
                        }
                    }
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    val participant = snapshot.getValue<Participant>()
                    if (participant != null) {
                        ioScope.launch {
                            database.getGroupMessageDao().deleteParticipant(participant)
                        }
                    }
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                    Log.d(TAG, "Participant child moved(Priority")
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, error.message, error.toException())
                }

            })
    }

    //Just a test message send method


    private var check = false
    fun sendMessage(
        message: String?,
        loggedInUser: LoggedInUser,
        uri: Uri?,
        mimeType: String
    ): Boolean {
        val currentParticipant =
            database.getGroupMessageDao().searchParticipant(loggedInUser.userUid)

        if (currentParticipant != null) {
            val uuid = UUID.randomUUID().toString()
            if (currentParticipant.role == "moderator" ||
                currentParticipant.role == "teacher" ||
                currentParticipant.role == "admin"
            ) {
                return if (uri == null) {
                    sendMessageForHigherLevelAccess(uuid, message, loggedInUser, null, mimeType)
                    true
                } else {
                    sendMediaMessageForHigherLevelAccess(uuid, uri, loggedInUser, message, mimeType)
                    true
                }

            } else {
                return if (uri == null) {
                    sendMessageForLowerLevelAccess(uuid, message, loggedInUser, null, mimeType)
                    true
                } else {
                    sendMediaMessageForLowerLevelAccess(uuid, message, loggedInUser, uri, mimeType)
                    return true
                }
            }
        } else if (check) {
            return false
        } else {
            if (check)
                return false
            databaseReference.child("groups")
                .child(basicGroupData.groupSemester)
                .child(basicGroupData.groupName)
                .child("participants")
                .get()
                .addOnSuccessListener {
                    val participantList = mutableListOf<Participant>()
                    for (snapshot in it.children) {
                        val participant = snapshot.getValue<Participant>()
                        if (participant != null)
                            participantList.add(participant)

                        ioScope.launch {
                            database.getGroupMessageDao().insertParticipantInBulk(participantList)
                            check = true
                            sendMessage(message, loggedInUser, uri, mimeType)
                        }
                    }
                }
        }
        return false
    }

    private fun sendMediaMessageForLowerLevelAccess(
        uuid: String,
        message: String?,
        loggedInUser: LoggedInUser,
        uri: Uri,
        mimeType: String
    ) {
        messageImageReference = storage
            .child("groups")
            .child(basicGroupData.groupSemester)
            .child(basicGroupData.groupName)
            .child("message-requests")
            .child(uuid)

        val imageUploadTask = messageImageReference.putFile(uri)

        imageUploadTask
            .addOnSuccessListener {
                messageImageReference.downloadUrl.addOnSuccessListener {
                    sendMessageForLowerLevelAccess(
                        uuid,
                        message,
                        loggedInUser,
                        it.toString(),
                        mimeType
                    )
                }
            }
            .addOnFailureListener {
                throw it
            }
    }

    private fun sendMediaMessageForHigherLevelAccess(
        uuid: String,
        uri: Uri,
        loggedInUser: LoggedInUser,
        message: String?,
        mimeType: String
    ) {
        messageImageReference = storage
            .child("groups")
            .child(basicGroupData.groupSemester)
            .child(basicGroupData.groupName)
            .child("messages")
            .child(uuid)

        val imageUploadTask = messageImageReference.putFile(uri)

        imageUploadTask
            .addOnSuccessListener {
                messageImageReference.downloadUrl.addOnSuccessListener {
                    sendMessageForHigherLevelAccess(
                        uuid,
                        message,
                        loggedInUser,
                        it.toString(),
                        mimeType
                    )
                }
            }
            .addOnFailureListener {
                throw it
            }
    }

    private fun sendMessageForHigherLevelAccess(
        uuid: String,
        message: String?,
        loggedInUser: LoggedInUser,
        docURL: String?,
        mimeType: String
    ) {
        val timestamp = System.currentTimeMillis()
        val groupMessageData = SendMessageObject(
            loggedInUser.userName,
            message ?: "",
            docURL ?: "",
            loggedInUser.userUid,
            loggedInUser.userImageUrl,
            loggedInUser.userUid,
            uuid,
            mimeType,
            timestamp
        )

        databaseReference.child("groups")
            .child(basicGroupData.groupSemester)
            .child(basicGroupData.groupName)
            .child("messages")
            .child(timestamp.toString())
            .setValue(groupMessageData)
    }

    private fun sendMessageForLowerLevelAccess(
        uuid: String,
        message: String?,
        loggedInUser: LoggedInUser,
        docURL: String?,
        mimeType: String
    ) {
        val timestamp = System.currentTimeMillis()
        val groupMessageData = SendMessageObject(
            loggedInUser.userName,
            message ?: "",
            docURL ?: "",
            loggedInUser.userUid,
            loggedInUser.userImageUrl,
            loggedInUser.userUid,
            uuid,
            mimeType,
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
    fun getCurrentParticipant(): Flow<Participant?> {
        return database.getGroupMessageDao().searchParticipantFlow(user?.uid!!)
    }

    fun deleteMessage(groupMessageData:GroupMessageInfo) {
        databaseReference.child("groups")
            .child(basicGroupData.groupSemester)
            .child(basicGroupData.groupName)
            .child("messages")
            .child(groupMessageData.messageTimeStamp.toString())
            .removeValue()
    }
}