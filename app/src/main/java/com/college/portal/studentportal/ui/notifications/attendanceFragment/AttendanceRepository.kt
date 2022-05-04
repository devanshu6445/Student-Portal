package com.college.portal.studentportal.ui.notifications.attendanceFragment

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.college.portal.studentportal.data.model.LoggedInUser
import com.college.portal.studentportal.data.model.Subject
import com.college.portal.studentportal.roomDatabase.user.CurrentUserDatabase
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.*

class AttendanceRepository() {

    private val mFirestore = FirebaseFirestore.getInstance()
    private val mDatabaseReference = FirebaseDatabase.getInstance().reference
    private var studentDatabase: CurrentUserDatabase? = null
    private val ioScope = CoroutineScope(Dispatchers.IO + Job())

    constructor(studentDatabase: CurrentUserDatabase): this(){
        this.studentDatabase = studentDatabase
    }
    companion object{
        private const val TAG = "AttendanceRepository"
    }
    suspend fun getStudentForTheSubject(semester:String,course:String):Flow<List<LoggedInUser>> = withContext(Dispatchers.IO){
        return@withContext studentDatabase?.getCurrentUserDao()?.getStudentListForAttendance(semester,course)!!
    }

    fun getStudentForTheSubjectNetwork(){
        mFirestore.collection("users")
            .get()
            .addOnCompleteListener {
                if(it.isSuccessful){
                    val studentList = mutableListOf<LoggedInUser>()
                    for(document in it.result){
                        val student = document.toObject(LoggedInUser::class.java)
                        studentList.add(student)
                    }
                    ioScope.launch {
                        studentDatabase?.getCurrentUserDao()?.insertStudentIntoStudentDatabase(studentList)
                    }
                }else
                    Log.e(TAG, "getStudentForTheSubjectNetwork: ", it.exception)
            }

        /*mFirestore.collection("users")
            .whereEqualTo("userSemester", semester)
            .whereEqualTo("userCourse",course)
            .get()
            .addOnCompleteListener {
                if(it.isSuccessful){
                    val studentList = mutableListOf<LoggedInUser>()
                    for(document in it.result){
                        val student = document.toObject(LoggedInUser::class.java)
                        studentList.add(student)
                    }
                    listener.onSuccessful(studentList)
                }else
                    listener.onFailure(it.exception!!)
            }*/
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun submitAttendance(absenteesList: List<LoggedInUser>, subjectCode:String){
        val uid = mutableListOf<String>()
        //making an absent student list with only their uid to send to database
        absenteesList.forEach {
            uid.add(it.userUid)
        }
        //Taking date locally to use it as node name in database
        val date =  Date()
        val day = SimpleDateFormat("dd",Locale.UK).format(date)
        val month = SimpleDateFormat("MMMM",Locale.UK).format(date)

        mDatabaseReference
            .child("attendance")
            .child(subjectCode)
            .child(month)
            .child(day)
            .setValue(uid)
    }

    fun getSubjectList(listener:FirebaseAttendanceCallback<Subject>){
        mFirestore.collection("subjects")
            .get()
            .addOnCompleteListener {
                if(it.isSuccessful){
                    val subjectList = mutableListOf<Subject>()
                    for(document in it.result){
                        val subject = document.toObject(Subject::class.java)
                        subjectList.add(subject)
                    }
                    listener.onSuccessful(subjectList)
                }
            }
    }

}