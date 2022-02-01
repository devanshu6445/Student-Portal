package com.college.portal.studentportal.ui.group_message

import android.content.Context
import android.util.Log
import androidx.room.Room
import com.college.portal.studentportal.callback.FirebaseGMDFCompleteListener
import com.college.portal.studentportal.data.model.GroupMessageData
import com.college.portal.studentportal.roomDatabase.UsersDatabase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

class GroupMessageRepository private constructor(){

    private val firebaseDatabase =  Firebase.database
    private val databaseReference = firebaseDatabase.reference
    private val firebaseUser = FirebaseAuth.getInstance().currentUser
    companion object{

        private val TAG = "GroupMessageRepository"

        @Volatile
        private var INSTANCE: GroupMessageRepository? = null

        fun getInstance() : GroupMessageRepository{
            return INSTANCE ?: synchronized(this){
                val instance = GroupMessageRepository()
                INSTANCE = instance
                instance
            }
        }
    }

    fun retrieveMessages(listener: FirebaseGMDFCompleteListener){
        retrieveMessage(listener)
    }

    private fun retrieveMessage(listener: FirebaseGMDFCompleteListener){
        val groupMessageList = mutableListOf<GroupMessageData>()
        databaseReference.child("groups")
            .child("sem1")//TODO: change according to user
            .child("bc") // TODO: change according to the group selected by user on Dashboard fragment
            .child("messages")
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    groupMessageList.clear()
                    lateinit var groupMessageData: GroupMessageData
                    for(dataSnapshot in snapshot.children){
                        groupMessageData = dataSnapshot.getValue<GroupMessageData>()!!
                        groupMessageList.add(groupMessageData)
                    }
                    groupMessageList.forEach {
                        Log.d(TAG,"MessageRepo: ${it.senderName}")
                    }
                    listener.onComplete(groupMessageList)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "onCancelled: Error", error.toException())
                    listener.onFailure()
                }

            })
    }

    fun sendMessage(message:String){
        val groupMessageData = GroupMessageData("Devanshu",message,"Today",firebaseUser!!.uid,"URL")
        databaseReference.child("groups")
            .child("sem1")//TODO: change according to user
            .child("bc") // TODO: change according to the group selected by user on Dashboard fragment
            .child("messages")
            .push()
            .setValue(groupMessageData)
    }
}