package com.college.portal.studentportal.ui.notifications.announcement

import com.college.portal.studentportal.data.model.Announcement
import com.college.portal.studentportal.extensionFunctions.announcementType
import com.college.portal.studentportal.roomDatabase.announcement.AnnouncementDatabase
import com.college.portal.studentportal.roomDatabase.announcement.AnnouncementEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*

class AnnouncementRepository() {

    private var database: AnnouncementDatabase? = null

    constructor(database: AnnouncementDatabase) : this() {
        this.database = database
    }

    private val mUser = FirebaseAuth.getInstance().currentUser
    private val mFirestore = FirebaseFirestore.getInstance()
    private val ioScope = CoroutineScope(Dispatchers.IO+ Job())

    fun updateAnnouncementDatabase(course: String, section: String) {
        mFirestore.collection("announcement")
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val announcementList = mutableListOf<AnnouncementEntity>()
                    for (document in it.result) {
                        val announcement = document.toObject(Announcement::class.java)
                        if (announcement.announcementType == "Universal") {
                            annADD(announcementList,announcement)
                        }else if(announcement.announcementType == "Course"){
                            if(announcement.announcementCourse == course){
                                annADD(announcementList,announcement)
                            }
                        } else if(announcement.announcementType == "Class"){
                            if(announcement.announcementCourse == course){
                                if(announcement.announcementClass == section){
                                    annADD(announcementList,announcement)
                                }
                            }
                        }
                    }
                    ioScope.launch {
                        database?.getAnnouncementDao()?.insertAnnouncement(announcementList)
                    }
                }
            }
    }
    private fun annADD(list:MutableList<AnnouncementEntity>,announcement: Announcement){
        list.add(
            AnnouncementEntity(
                announcement.announcementText,
                announcement.announcementID,
                announcement.announcementType,
                announcement.announcementCreator,
                announcement.announcementCourse,
                announcement.announcementClass,
                announcement.announcementTimestamp
            )
        )
    }
    fun postAnnouncement(announcement: Announcement, listener: AnnouncementResult) {
        announcement.announcementCreator = mUser?.uid!!
        val uid = UUID.randomUUID().toString()
        val timestamp = System.currentTimeMillis() / 1000
        announcement.announcementID = uid
        announcement.announcementTimestamp = timestamp
        mFirestore
            .collection("announcement")
            .document(timestamp.toString())
            .set(announcement)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    listener.onSuccessful()
                } else {
                    listener.onFailure(it.exception!!)
                }
            }

    }
}