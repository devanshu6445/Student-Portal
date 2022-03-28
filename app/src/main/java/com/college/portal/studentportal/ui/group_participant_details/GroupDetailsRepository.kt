package com.college.portal.studentportal.ui.group_participant_details

import android.util.Log
import com.college.portal.studentportal.callback.FirebasePFCompleteListener
import com.college.portal.studentportal.roomDatabase.groups.BasicGroupData
import com.college.portal.studentportal.roomDatabase.groupEverything.Participant
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

class GroupDetailsRepository private constructor() {

    private val databaseReference = Firebase.database.reference


    companion object{
        @Volatile
        private var INSTANCE: GroupDetailsRepository? = null

        fun getRepository(): GroupDetailsRepository{

            return INSTANCE?:synchronized(this){
                val instance = GroupDetailsRepository()
                INSTANCE = instance
                instance
            }
        }
    }

    fun loadList(firebasePFCompleteListener: FirebasePFCompleteListener,basicGroupData: BasicGroupData){

        databaseReference.child("groups")
            .child(basicGroupData.groupSemester)
            .child(basicGroupData.groupName)
            .child("participants")
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val participantList = mutableListOf<Participant>()
                    for (dataSnapshot in snapshot.children){
                        val participant = dataSnapshot.getValue<Participant>()
                        if (participant != null) {
                            participantList.add(participant)
                        }
                    }
                    firebasePFCompleteListener.onComplete(participantList)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("database-error",error.message,error.toException())
                    firebasePFCompleteListener.onFailure()
                }

            })

    }

}