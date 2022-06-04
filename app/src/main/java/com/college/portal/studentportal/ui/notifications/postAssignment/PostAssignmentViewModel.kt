package com.college.portal.studentportal.ui.notifications.postAssignment

import android.net.Uri
import android.util.Log
import androidx.lifecycle.*
import com.college.portal.studentportal.callback.FirebaseGeneralCallback
import com.college.portal.studentportal.data.model.LoggedInUser
import com.college.portal.studentportal.data.model.MediaFile
import com.college.portal.studentportal.data.model.Subject
import com.college.portal.studentportal.roomDatabase.assignment.Assignment
import com.college.portal.studentportal.roomDatabase.subjectDatabase.SubjectDatabase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray

import java.lang.Exception
import java.util.*

class PostAssignmentViewModel(private val loggedInUser: LoggedInUser) : ViewModel(),FirebaseGeneralCallback{
    
    private val assignmentRepository = AssignmentRepository(loggedInUser,this)
    private val _status = MutableLiveData(0)
    private val _subjectList = MutableLiveData<List<String>>()
    val subjectList:LiveData<List<String>> = _subjectList
    val status:LiveData<Int> = _status


    fun uploadAssignment(fileList:Stack<MediaFile>,assignment: Assignment){
        _status.value = 1
        assignmentRepository.uploadFiles(fileList,assignment)
    }
    suspend fun getSubjectList(database:SubjectDatabase,course:String){
        val list = assignmentRepository.getSubjectList(database,course)
        withContext(Dispatchers.Main){
            _subjectList.value = list
        }
    }
    override fun onSuccessful() {
        _status.value = 2
    }

    override fun onFailure(error: Exception) {
        _status.value = 3
    }

    fun getSubjectList(){


    }
}

class PostAssignmentViewModelFactory(private val loggedInUser: LoggedInUser): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(PostAssignmentViewModel::class.java)){
            return PostAssignmentViewModel(loggedInUser) as T
        }else{
            throw IllegalStateException("Unknown class")
        }
    }

}
