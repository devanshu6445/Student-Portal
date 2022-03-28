package com.college.portal.studentportal.ui.dashboard

import android.util.Log
import com.college.portal.studentportal.data.model.LoggedInUser
import com.college.portal.studentportal.roomDatabase.groups.BasicGroupData
import com.college.portal.studentportal.roomDatabase.groups.GroupDatabase
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class GroupRepository {

    companion object{
        private const val TAG = "GroupRepository: "
    }
    private val firebaseDatabase = Firebase.database
    private val databaseReference = firebaseDatabase.reference
    private lateinit var reference: DatabaseReference
    private lateinit var childEventListener:ChildEventListener

    fun retrieveGroupsRDatabase(
        database: GroupDatabase,
        currentUser: LoggedInUser
    ){
        retrieveGroupRDatabase(database,currentUser)
    }

    private fun retrieveGroupRDatabase(database:GroupDatabase,currentUSer: LoggedInUser){
        Log.d("LoggedInUser",currentUSer.userSemester+currentUSer.userUid)
        val dao = database.getGroupDao()

        reference = databaseReference
                .child("groups")
                .child("sem${currentUSer.userSemester}-details")

        childEventListener = reference.addChildEventListener(object: ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val groupData = snapshot.getValue<BasicGroupData>()
                if (groupData!=null)
                    GlobalScope.launch(Dispatchers.IO) {
                        val g = dao.searchGroup(groupData.groupID)
                        if(g == null)
                            dao.insertGroup(groupData)
                    }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val groupData = snapshot.getValue<BasicGroupData>()
                if (groupData!=null)
                    GlobalScope.launch(Dispatchers.IO) {
                        dao.updateGroup(groupData)
                    }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val groupData = snapshot.getValue<BasicGroupData>()
                if (groupData!=null)
                    GlobalScope.launch(Dispatchers.IO) {
                        dao.deleteGroup(groupData)
                    }
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                Log.d(TAG,"child priority changed")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG,error.message + " " + error.details)
            }

        })
    }

    fun deRegister(){
        reference.removeEventListener(childEventListener)
    }
}