package com.college.portal.studentportal.ui.notifications.announcement

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.college.portal.studentportal.data.model.Announcement

class PostAnnouncementViewModel : ViewModel(), AnnouncementResult {

    private val announcementRepository = AnnouncementRepository()
    lateinit var exception: java.lang.Exception
    private val _uploadStatus = MutableLiveData(0)
    val uploadStatus:LiveData<Int> = _uploadStatus

    fun postAnnouncement(announcement: Announcement) {
        announcementRepository.postAnnouncement(announcement,this)
    }

    override fun onSuccessful() {
        _uploadStatus.value = 1
    }

    override fun onFailure(e : Exception) {
        _uploadStatus.value = 2
        exception = e
    }

}

interface AnnouncementResult {
    fun onSuccessful()
    fun onFailure(e : Exception)
}
