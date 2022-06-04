package com.college.portal.studentportal.ui.notifications.announcement

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.college.portal.studentportal.data.model.Announcement
import com.college.portal.studentportal.data.model.MediaFile
import com.college.portal.studentportal.roomDatabase.announcement.AnnouncementDatabase
import java.util.*

class PostAnnouncementViewModel(database: AnnouncementDatabase) : ViewModel(), AnnouncementResult {

    private val announcementRepository = AnnouncementRepository(database)
    lateinit var exception: java.lang.Exception
    private val _uploadStatus = MutableLiveData(0)
    val uploadStatus:LiveData<Int> = _uploadStatus

    init {
        announcementRepository.updateAnnouncementDatabase("BCA","A")
    }
    fun postAnnouncement(announcement: Announcement,fileList:Stack<MediaFile>) {
        announcementRepository.uploadFiles(fileList,announcement,this)
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
