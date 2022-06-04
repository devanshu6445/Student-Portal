package com.college.portal.studentportal.ui.notifications.myAttendance

import com.college.portal.studentportal.data.model.LoggedInUser
import com.college.portal.studentportal.data.model.Subject
import com.college.portal.studentportal.roomDatabase.attendance.Attendance
import com.college.portal.studentportal.roomDatabase.attendance.AttendanceDatabase
import com.college.portal.studentportal.ui.notifications.attendanceFragment.FirebaseAttendanceCallback
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow

class MyAttendanceRepository(private val database: AttendanceDatabase) {

    private val mDatabaseReference = FirebaseDatabase.getInstance().reference

    //private val mUser = FirebaseAuth.getInstance().currentUser
    private val ioScope = CoroutineScope(Dispatchers.IO + Job())
    suspend fun getPresents(month: String): Flow<List<Attendance>> = withContext(Dispatchers.IO) {
        return@withContext database.getAttendanceDao().getAll(month)
    }

    fun registerListener(subjectCode: String, studentUid: String) {
        mDatabaseReference
            .child("attendance")
            .child(subjectCode)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val myAttendanceList = mutableListOf<Attendance>()
                    val nList = mutableListOf<Attendance>()
                    myAttendanceList.clear()
                    for (snapMonth in snapshot.children) {
                        val attendance = Attendance()
                        attendance.month = snapMonth.key!!
                        for (snapDay in snapMonth.children) {
                            attendance.day = snapDay.key!!.toInt()
                            var found = false

                            for (att in snapDay.children) {
                                if (studentUid.trim() == att.value.toString().trim()) {
                                    attendance.isPresent = true
                                    val attendance1 = Attendance().apply {
                                        year = attendance.year
                                        month = attendance.month
                                        day = attendance.day
                                        isPresent = attendance.isPresent
                                    }
                                    found = true
                                    myAttendanceList.add(attendance1)
                                    break
                                }
                            }
                            if (!found) {
                                val attendance1 = Attendance().apply {
                                    year = attendance.year
                                    month = attendance.month
                                    day = attendance.day
                                    isPresent = false
                                }
                                myAttendanceList.add(attendance1)
                            }
                        }
                    }
                    ioScope.launch {
                        database.getAttendanceDao().insertAttendance(myAttendanceList)
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }

    suspend fun getMonthPercentage(month: String): Flow<String> = withContext(Dispatchers.IO) {
        return@withContext database.getAttendanceDao().getMonthAttendancePercentage(month)
    }

    suspend fun getOverAllPercentage(): Flow<String> = withContext(Dispatchers.IO) {
        return@withContext database.getAttendanceDao().getOverAllAttendancePercentage()
    }
}

class SelectSubjectRepository(private val loggedInUser: LoggedInUser) {

    private val mFirestore = FirebaseFirestore.getInstance()

    fun getSubjectListForStudent(listener: FirebaseAttendanceCallback<Subject>) {
        mFirestore
            .collection("subjects")
            .whereEqualTo("subCourse", loggedInUser.userCourse)
            .whereEqualTo("subSem", loggedInUser.userSemester)
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val subjectList = mutableListOf<Subject>()
                    for (document in it.result) {
                        val subject = document.toObject(Subject::class.java)
                        subjectList.add(subject)
                    }
                    listener.onSuccessful(subjectList)
                } else
                    listener.onFailure(it.exception ?: NullPointerException("Document is null"))
            }
    }
}