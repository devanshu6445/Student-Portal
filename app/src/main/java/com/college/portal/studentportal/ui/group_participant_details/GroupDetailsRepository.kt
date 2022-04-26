package com.college.portal.studentportal.ui.group_participant_details

import android.util.Log
import com.college.portal.studentportal.roomDatabase.groupEverything.GroupMessageDatabase
import com.college.portal.studentportal.roomDatabase.groupEverything.Participant
import com.college.portal.studentportal.roomDatabase.groups.BasicGroupData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import java.lang.Exception

class GroupDetailsRepository private constructor() {

    private val databaseReference = Firebase.database.reference
    private val database = GroupMessageDatabase.getDatabase()
    private val user = FirebaseAuth.getInstance().currentUser
    private lateinit var participantReference: DatabaseReference
    private lateinit var childEventListener: ChildEventListener
    private val ioScope = CoroutineScope(Dispatchers.IO + Job())

    companion object {
        @Volatile
        private var INSTANCE: GroupDetailsRepository? = null
        private lateinit var basicGroupData: BasicGroupData
        private const val TAG = "GroupDetailsRepository: "
        fun createRepository(basicGroupData: BasicGroupData): GroupDetailsRepository {
            this.basicGroupData = basicGroupData
            return synchronized(this) {
                val instance = GroupDetailsRepository()
                INSTANCE = instance
                instance
            }
        }

        fun getRepository() = INSTANCE
    }

    fun getLoggedInParticipant(): Flow<Participant?> {
        return database.getGroupMessageDao().searchParticipantFlow(user?.uid!!)
    }

    suspend fun demoteUser(participant: Participant): String = withContext(Dispatchers.IO) {
        lateinit var roleToChange: String
        if (participant.role == "moderator")
            roleToChange = "student"
        else
            return@withContext "Can't change the role from here please contact he administrator"
        databaseReference.child("groups")
            .child(basicGroupData.groupSemester)
            .child(basicGroupData.groupName)
            .child("participants")
            .child(participant.uid)
            .child("role")
            .setValue(roleToChange)
        return@withContext "Role has been changed to $roleToChange"
    }

    suspend fun promoteUser(participant: Participant): String = withContext(Dispatchers.IO) {
        lateinit var roleToChange: String
        if (participant.role == "student")
            roleToChange = "moderator"
        else
            return@withContext "Can't change the role from here please contact he administrator"
        databaseReference.child("groups")
            .child(basicGroupData.groupSemester)
            .child(basicGroupData.groupName)
            .child("participants")
            .child(participant.uid)
            .child("role")
            .setValue(roleToChange)
        return@withContext "Role has been changed to $roleToChange"
    }

    fun loadList(
        basicGroupData: BasicGroupData
    ) {

        participantReference = databaseReference.child("groups")
            .child(basicGroupData.groupSemester)
            .child(basicGroupData.groupName)
            .child("participants")

        childEventListener = participantReference.addChildEventListener(object :
            ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val participant = snapshot.getValue<Participant>()
                if(participant!=null){
                    ioScope.launch {
                        val part = database.getGroupMessageDao().searchParticipant(participant.uid)
                        if(part==null){
                            database.getGroupMessageDao().insertParticipant(participant)
                        }
                    }
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val participant = snapshot.getValue<Participant>()
                if(participant!=null){
                    ioScope.launch {
                        database.getGroupMessageDao().updateParticipant(participant)
                    }
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val participant = snapshot.getValue<Participant>()
                if(participant!=null){
                    ioScope.launch {
                        database.getGroupMessageDao().deleteParticipant(participant)
                    }
                }
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "onCancelled: ${error.message}",error.toException() )
            }

        })
    }
    fun getParticipants(): Flow<List<Participant>> {
        return database.getGroupMessageDao().getAllParticipants()
    }
    fun removeListeners(){
        participantReference.removeEventListener(childEventListener)
    }

    suspend fun banStudent(rawBanTime:Long,bannedUID:String,calculate:Boolean):String = withContext(Dispatchers.Main){
        val banTime = if(calculate) (System.currentTimeMillis()/1000)+rawBanTime else rawBanTime
        try {
            databaseReference.child("groups")
                .child(basicGroupData.groupSemester)
                .child(basicGroupData.groupName)
                .child("participants")
                .child(bannedUID)
                .child("banTime")
                .setValue(banTime)
        } catch (e: Exception) {
            return@withContext "Some error occurred ${e.message}"
        }
        return@withContext "Student has been banned"
    }
}