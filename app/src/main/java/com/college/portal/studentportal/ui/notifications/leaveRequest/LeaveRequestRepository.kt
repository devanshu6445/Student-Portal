package com.college.portal.studentportal.ui.notifications.leaveRequest

import com.college.portal.studentportal.data.model.LeaveRequest
import com.college.portal.studentportal.data.model.LoggedInUser
import com.college.portal.studentportal.roomDatabase.leaveRequest.LeaveDatabase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import java.util.*

class LeaveRequestRepository(private val listener:LeaveRequestCallback) {

    private var database:LeaveDatabase? = null

    constructor(database: LeaveDatabase,listener: LeaveRequestCallback):this(listener){
        this.database = database
    }

    private val mFirestore = FirebaseFirestore.getInstance()
    private val ioScope = CoroutineScope(Dispatchers.IO + Job())

    fun sendRequest(leaveRequest: LeaveRequest){

        mFirestore.collection("leaveRequests")
            .document(leaveRequest.timestamp.toString())
            .set(leaveRequest)
            .addOnSuccessListener {
                listener.onSuccess()
            }.addOnFailureListener {
                listener.onFailure()
            }
    }

    fun registerLeaveRequestListener(loggedInUser: LoggedInUser){
        mFirestore
            .collection("leaveRequests")
            .whereEqualTo("requesterUid",loggedInUser.userUid)
            .get()
            .addOnSuccessListener {
                val leaveRequestList = mutableListOf<LeaveRequest>()
                for (document in it){
                    val leaveRequest = document.toObject(LeaveRequest::class.java)
                    leaveRequestList.add(leaveRequest)
                }
                ioScope.launch {
                    database?.getLeaveDao()?.insertLeaveRequest(leaveRequestList)
                }
            }
            .addOnFailureListener {
                listener.onFailure()
            }
    }
    fun updateLeaveDatabaseForHOD(course:String){
        mFirestore.collection("leaveRequests")
            .whereEqualTo("requesterCourse",course)
            .whereEqualTo("status","")
            .get()
            .addOnSuccessListener {
                val leaveRequestList = mutableListOf<LeaveRequest>()
                for (document in it){
                    val leaveRequest = document.toObject(LeaveRequest::class.java)
                    leaveRequestList.add(leaveRequest)
                }
                ioScope.launch {
                    database?.getLeaveDao()?.insertLeaveRequest(leaveRequestList)
                }
            }
            .addOnFailureListener {
                listener.onFailure()
            }
    }

    suspend fun deleteLeaveRequest(leaveRequest: LeaveRequest) = withContext(Dispatchers.IO){
        database?.getLeaveDao()?.deleteLeaveRequest(leaveRequest)
    }

    fun updateLeaveRequestOnServer(leaveRequest: LeaveRequest){
        mFirestore.collection("leaveRequests")
            .document(leaveRequest.timestamp.toString())
            .update(mapOf(
                "status" to leaveRequest.status
            ))
    }
    suspend fun getAllLeaveRequests(): Flow<List<LeaveRequest>>? = withContext(Dispatchers.IO){
        return@withContext database?.getLeaveDao()?.getAllLeaves()
    }
}