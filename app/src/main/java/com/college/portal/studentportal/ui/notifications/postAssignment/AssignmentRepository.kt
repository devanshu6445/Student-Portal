package com.college.portal.studentportal.ui.notifications.postAssignment

import android.net.Uri
import android.util.Log
import com.college.portal.studentportal.callback.FirebaseGeneralCallback
import com.college.portal.studentportal.data.model.LoggedInUser
import com.college.portal.studentportal.data.model.MediaFile
import com.college.portal.studentportal.data.model.Subject
import com.college.portal.studentportal.roomDatabase.assignment.Assignment
import com.college.portal.studentportal.roomDatabase.assignment.AssignmentDatabase
import com.college.portal.studentportal.roomDatabase.subjectDatabase.SubjectDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.util.*

class AssignmentRepository(
    private val loggedInUser: LoggedInUser,
    private val listener: FirebaseGeneralCallback
) {

    private val mFirestore = FirebaseFirestore.getInstance()
    private val mStorageReference = FirebaseStorage.getInstance().reference
    private var assignmentDatabase:AssignmentDatabase? = null
    private val ioScope = CoroutineScope(Dispatchers.IO + Job())

    constructor(
        loggedInUser: LoggedInUser,
        listener: FirebaseGeneralCallback,
        assignmentDatabase: AssignmentDatabase
    ) : this(loggedInUser, listener) {
        this.assignmentDatabase = assignmentDatabase
    }

    fun uploadFiles(fileList: Stack<MediaFile>, assignment: Assignment) {
        val urlList = mutableListOf<String>()
        var flag = true
        fun uploadFile() {
            if (fileList.isNotEmpty()) {
                val file = fileList.pop()
                val ref = mStorageReference.child("assignments")
                    .child(assignment.assignmentID)
                    .child(file.fileName)

                val task = ref.putFile(file.uri)
                    .addOnSuccessListener {
                        ref.downloadUrl.addOnSuccessListener {
                            urlList.add(it.toString())
                            uploadFile()
                        }
                    }
            } else {
                if (flag) {
                    if (urlList.isNotEmpty()) {
                        val jsonArray = JSONArray(urlList)
                        val urlJsonString = jsonArray.toString()
                        assignment.assignmentURL = urlJsonString
                        sendAssignment(assignment)
                    } else {
                        assignment.assignmentURL = null
                        sendAssignment(assignment)
                    }
                    flag = false
                }
            }
        }
        uploadFile()
    }

    fun getSubjectList(database: SubjectDatabase, course: String): List<String> {
        return database.getSubjectDao().getSubjectsBySemesterAndCourse(course)
    }

    fun updateAssignmentDatabase(){
        //
        mFirestore.collection("assignment")
            .whereEqualTo("assignmentSection",loggedInUser.userSection)
            .whereEqualTo("assignmentCourse",loggedInUser.userCourse)
            .get()
            .addOnSuccessListener {
                val assignmentList = mutableListOf<Assignment>()
                for(document in it){
                    val assignment = document.toObject(Assignment::class.java)
                    assignmentList.add(assignment)
                }
                ioScope.launch {
                    assignmentDatabase?.getAssignmentDao()?.insertAssignment(assignmentList)
                }
            }.addOnFailureListener {

            }
    }

    suspend fun getAssignments():Flow<List<Assignment>>?{
        return assignmentDatabase?.getAssignmentDao()?.getAllAssignments()
    }

    private fun sendAssignment(assignment: Assignment) {
        mFirestore
            .collection("assignment")
            .document(assignment.assignmentID)
            .set(assignment)
        listener.onSuccessful()
    }
}