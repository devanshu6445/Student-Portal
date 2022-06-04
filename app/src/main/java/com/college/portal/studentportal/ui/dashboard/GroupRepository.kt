package com.college.portal.studentportal.ui.dashboard

import android.util.Log
import android.view.View
import com.college.portal.studentportal.data.model.BasicGroupDataNetwork
import com.college.portal.studentportal.data.model.LoggedInUser
import com.college.portal.studentportal.roomDatabase.groups.BasicGroupData
import com.college.portal.studentportal.roomDatabase.groups.GroupDatabase
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*

/**
 * This repository is used for fetching group data such as name, groupID from network or local database
 */
class GroupRepository(val database: GroupDatabase) {

    companion object{
        private const val TAG = "GroupRepository: "
    }
    private val firebaseDatabase = Firebase.database
    private val databaseReference = firebaseDatabase.reference
    private lateinit var reference: DatabaseReference
    private val mFirestore = FirebaseFirestore.getInstance()
    private lateinit var childEventListener:ChildEventListener
    private val ioScope = CoroutineScope(Dispatchers.IO + Job())

    suspend fun searchGroup(textQuery: String) : List<BasicGroupData> = withContext(Dispatchers.IO){
        return@withContext database.getGroupDao().searchAllAboutAGroup("%$textQuery%")
    }
    fun retrieveGroupsRDatabase(
        currentUser: LoggedInUser
    ){
        when(currentUser.userDesignation){
            "teacher","admin","HOD" -> {
                retrieveGroupsForHigherAuthority(currentUser)
            }
            "moderator","student" -> {
                retrieveGroupRDatabase(currentUser)
            }
        }
    }

    private fun retrieveGroupsForHigherAuthority(currentUser: LoggedInUser){
        Log.d("LoggedInUser",currentUser.userSemester+currentUser.userUid)
        val dao = database.getGroupDao()

        mFirestore.collection("BCA")
            .get()
            .addOnSuccessListener {
                for(document in it){
                    val groupDataNetwork = document.toObject(BasicGroupDataNetwork::class.java)
                    ioScope.launch {
                        val g = dao.searchGroup(groupDataNetwork.groupID)
                        if(g == null)
                        {
                            val groupData = BasicGroupData(
                                groupDataNetwork.groupName,
                                groupDataNetwork.groupImage,
                                groupDataNetwork.groupPurpose,
                                groupDataNetwork.groupSemester,
                                groupDataNetwork.groupCourse,
                                groupDataNetwork.groupID,
                                ""
                            )
                            dao.insertGroup(groupData)
                        }
                    }
                }
            }.addOnFailureListener {

            }
    }

    private fun retrieveGroupRDatabase(currentUser: LoggedInUser){
        Log.d("LoggedInUser",currentUser.userSemester+currentUser.userUid)
        val dao = database.getGroupDao()

        mFirestore.collection("BCA")
            .whereEqualTo("groupCourse",currentUser.userCourse)
            .whereEqualTo("groupSemester","sem${currentUser.userSemester}")
            .get()
            .addOnSuccessListener {
                for(document in it){
                    val groupDataNetwork = document.toObject(BasicGroupDataNetwork::class.java)
                    ioScope.launch {
                        val g = dao.searchGroup(groupDataNetwork.groupID)
                        if(g == null)
                        {
                            val groupData = BasicGroupData(
                                groupDataNetwork.groupName,
                                groupDataNetwork.groupImage,
                                groupDataNetwork.groupPurpose,
                                groupDataNetwork.groupSemester,
                                groupDataNetwork.groupCourse,
                                groupDataNetwork.groupID,
                                ""
                            )
                            dao.insertGroup(groupData)
                        }
                    }
                }
            }.addOnFailureListener {

            }

       /* reference = databaseReference
            .child(currentUser.userCourse)
                .child("groups")
                .child("sem${currentUser.userSemester}-details")

        childEventListener = reference.addChildEventListener(object: ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val groupDataNetwork = snapshot.getValue<BasicGroupDataNetwork>()
                if (groupDataNetwork!=null)
                    ioScope.launch {
                        val g = dao.searchGroup(groupDataNetwork.groupID)
                        if(g == null)
                        {
                            val groupData = BasicGroupData(
                                    groupDataNetwork.groupName,
                                    groupDataNetwork.groupImage,
                                    groupDataNetwork.groupPurpose,
                                    groupDataNetwork.groupSemester,
                                    groupDataNetwork.groupCourse,
                                    groupDataNetwork.groupID,
                                    ""
                                )
                            dao.insertGroup(groupData)
                        }
                    }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val groupDataNetwork = snapshot.getValue<BasicGroupData>()
                if (groupDataNetwork!=null)
                    ioScope.launch {
                        val groupData = BasicGroupData(
                            groupDataNetwork.groupName,
                            groupDataNetwork.groupImage,
                            groupDataNetwork.groupPurpose,
                            groupDataNetwork.groupSemester,
                            groupDataNetwork.groupCourse,
                            groupDataNetwork.groupID,
                            ""
                        )
                        dao.updateGroup(groupData)
                    }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val groupDataNetwork = snapshot.getValue<BasicGroupData>()
                if (groupDataNetwork!=null)
                    ioScope.launch {
                        val groupData = BasicGroupData(
                            groupDataNetwork.groupName,
                            groupDataNetwork.groupImage,
                            groupDataNetwork.groupPurpose,
                            groupDataNetwork.groupSemester,
                            groupDataNetwork.groupCourse,
                            groupDataNetwork.groupID,
                            ""
                        )
                        dao.deleteGroup(groupData)
                    }
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                Log.d(TAG,"child priority changed")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG,error.message + " " + error.details)
            }

        })*/

    }

    fun deRegister(){
        /*reference.removeEventListener(childEventListener)*/
    }
}