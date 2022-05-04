package com.college.portal.studentportal.ui.notifications.editStudentDetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.college.portal.studentportal.callback.FirebaseGeneralCallback
import com.college.portal.studentportal.data.model.EditedProfile
import com.college.portal.studentportal.ui.notifications.changePassword.ChangePasswordRepository
import kotlinx.coroutines.launch
import java.lang.Exception

class EditStudentDetailsViewModel : ViewModel(), FirebaseGeneralCallback,
    FirebaseProfileChangeCallback {
    // TODO: Implement the ViewModel
    private val _profileUpdateStatus = MutableLiveData(0)
    val profileUpdateStatus:LiveData<Int> = _profileUpdateStatus
    private val editStudentDetailsRepository = EditStudentDetailsRepository(this)
    private val changePasswordRepository = ChangePasswordRepository(this)
    private lateinit var editedProfile:EditedProfile
    lateinit var error: Exception

    fun updateProfile(editedProfile:EditedProfile,password:String){
        this.editedProfile = editedProfile
        _profileUpdateStatus.value = 1
        viewModelScope.launch {
            changePasswordRepository.reAuthenticateUser(password)
        }
    }

    override fun onSuccessful() {
        editStudentDetailsRepository.updateProfile(editedProfile)
    }

    override fun onFailure(error: Exception) {
        this.error = error
        _profileUpdateStatus.value = 3 //failure code
    }

    override fun onProfileChanged() {
        _profileUpdateStatus.value = 2 //success code
    }

    override fun onProfileChangeFailure(error: Exception) {
        this.error = error
        _profileUpdateStatus.value = 3
    }

}
interface FirebaseProfileChangeCallback{
    fun onProfileChanged()
    fun onProfileChangeFailure(error:Exception)
}
