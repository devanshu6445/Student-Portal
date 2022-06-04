package com.college.portal.studentportal.ui.dashboard.myAssignments

import androidx.lifecycle.*
import com.college.portal.studentportal.callback.FirebaseGeneralCallback
import com.college.portal.studentportal.data.model.LoggedInUser
import com.college.portal.studentportal.roomDatabase.assignment.Assignment
import com.college.portal.studentportal.roomDatabase.assignment.AssignmentDatabase
import com.college.portal.studentportal.ui.notifications.postAssignment.AssignmentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MyAssignmentsViewModel(private val loggedInUser: LoggedInUser, assignmentDatabase: AssignmentDatabase) : ViewModel(),FirebaseGeneralCallback {
    private val _assignmentList = MutableLiveData<List<Assignment>>()
    val assignmentList:LiveData<List<Assignment>> = _assignmentList
    private val assignmentRepo = AssignmentRepository(loggedInUser,this,assignmentDatabase)

    init {
        viewModelScope.launch(Dispatchers.IO) {
            assignmentRepo.getAssignments()?.collect {
                withContext(Dispatchers.Main){
                    _assignmentList.value = it
                }
            }
        }
        assignmentRepo.updateAssignmentDatabase()
    }

    override fun onSuccessful() {

    }

    override fun onFailure(error: Exception) {

    }
}
class MyAssignmentViewModelFactory(private val loggedInUser: LoggedInUser,private val assignmentDatabase: AssignmentDatabase):ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(MyAssignmentsViewModel::class.java)){
            return MyAssignmentsViewModel(loggedInUser,assignmentDatabase) as T
        }else{
            throw IllegalStateException("Unknown class")
        }
    }

}