package com.college.portal.studentportal.ui.notifications.announcement

import com.college.portal.studentportal.data.model.Announcement
import com.college.portal.studentportal.data.model.MediaFile
import com.college.portal.studentportal.roomDatabase.announcement.AnnouncementDatabase
import com.college.portal.studentportal.roomDatabase.announcement.AnnouncementEntity
import com.college.portal.studentportal.roomDatabase.assignment.Assignment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import org.json.JSONArray
import java.util.*

class AnnouncementRepository() {

    private var database: AnnouncementDatabase? = null

    constructor(database: AnnouncementDatabase) : this() {
        this.database = database
    }

    private val mUser = FirebaseAuth.getInstance().currentUser
    private val mFirestore = FirebaseFirestore.getInstance()
    private val ioScope = CoroutineScope(Dispatchers.IO+ Job())
    private val mStorageReference = FirebaseStorage.getInstance().reference

    suspend fun getLatestAnnouncement():Flow<AnnouncementEntity>? = withContext(Dispatchers.IO) {
        return@withContext database?.getAnnouncementDao()?.getLatestAnnouncement(System.currentTimeMillis()/1000)
    }

    suspend fun getAllAnnouncements():Flow<List<AnnouncementEntity>>? = withContext(Dispatchers.IO){
        return@withContext database?.getAnnouncementDao()?.getAllAnnouncements()
    }

    fun updateAnnouncementDatabase(course: String = "-1", section: String = "-1") {
        mFirestore.collection("announcement")
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val announcementList = mutableListOf<AnnouncementEntity>()
                    if(course == "-1" && section == "-1"){
                        for (document in it.result){
                            val announcement = document.toObject(Announcement::class.java)
                            annADD(announcementList,announcement)
                        }
                    }
                    else{
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
                announcement.announcementUrl,
                announcement.announcementTimestamp
            )
        )
    }

    fun uploadFiles(fileList: Stack<MediaFile>, announcement: Announcement,listener: AnnouncementResult) {
        val urlList = mutableListOf<String>()
        var flag = true
        announcement.announcementID = UUID.randomUUID().toString()
        fun uploadFile() {
            if (fileList.isNotEmpty()) {
                val file = fileList.pop()
                val ref = mStorageReference.child("announcements")
                    .child(announcement.announcementID)
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
                        announcement.announcementUrl = urlJsonString
                        postAnnouncement(announcement,listener)
                    } else {
                        announcement.announcementUrl = null
                        postAnnouncement(announcement,listener)
                    }
                    flag = false
                }
            }
        }
        uploadFile()
    }

    private fun postAnnouncement(announcement: Announcement, listener: AnnouncementResult) {
        announcement.announcementCreator = mUser?.uid!!
        val timestamp = System.currentTimeMillis() / 1000
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