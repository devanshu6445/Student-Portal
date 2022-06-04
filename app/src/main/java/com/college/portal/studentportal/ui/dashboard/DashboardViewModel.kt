package com.college.portal.studentportal.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.college.portal.studentportal.data.model.LoggedInUser
import com.college.portal.studentportal.roomDatabase.announcement.AnnouncementDatabase
import com.college.portal.studentportal.roomDatabase.announcement.AnnouncementEntity
import com.college.portal.studentportal.roomDatabase.groups.BasicGroupData
import com.college.portal.studentportal.roomDatabase.groups.GroupDatabase
import com.college.portal.studentportal.roomDatabase.user.CurrentUserDatabase
import com.college.portal.studentportal.ui.notifications.announcement.AnnouncementRepository
import com.college.portal.studentportal.ui.notifications.attendanceFragment.AttendanceFragment
import com.college.portal.studentportal.ui.notifications.attendanceFragment.AttendanceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.log

class DashboardViewModel(

    private val currentUserDatabase: CurrentUserDatabase,
    private val groupDatabase: GroupDatabase,
    private val announcementDatabase: AnnouncementDatabase,
    private val studentDatabase: CurrentUserDatabase
) : ViewModel() {

    private val groupRepository = GroupRepository(groupDatabase)
    private val announcementRepository = AnnouncementRepository(announcementDatabase)
    private val _latestAnnouncement = MutableLiveData<AnnouncementEntity>()
    private val attendanceRepository = AttendanceRepository(studentDatabase)
    val latestAnnouncement:LiveData<AnnouncementEntity> = _latestAnnouncement
    val groupList: MutableLiveData<MutableList<BasicGroupData>> = MutableLiveData()

    lateinit var loggedInUser:LoggedInUser

    //Will observe the group database asynchronously for any change
    //if any change has occurred  live data will be updated on Main Thread
    val v = viewModelScope.launch {
        groupDatabase.getGroupDao().getAllGroup().collect {
            withContext(Dispatchers.Main) {
                groupList.value = it
            }
        }
    }

    suspend fun searchGroupData(queryText: String): List<BasicGroupData> {
        return groupRepository.searchGroup(queryText)
    }

    init {

        retrieveGroup()
        viewModelScope.launch {
            announcementRepository.getLatestAnnouncement()?.collect {
                withContext(Dispatchers.Main){
                    _latestAnnouncement.value = it
                }
            }
        }
        var retry = 0
        fun updateSubjectData(){
            viewModelScope.launch {
                try {
                    when(loggedInUser.userDesignation){
                        "teacher","admin","HOD" -> {
                            announcementRepository.updateAnnouncementDatabase()
                        }
                        else -> {
                            announcementRepository.updateAnnouncementDatabase(loggedInUser.userCourse,loggedInUser.userSection)
                        }
                    }
                } catch (e: UninitializedPropertyAccessException) {
                    delay(1000)
                    retry++
                    if(retry<=10){
                        updateSubjectData()
                    }
                }
            }
        }
        updateSubjectData()
        attendanceRepository.getStudentForTheSubjectNetwork()
        attendanceRepository.updateSubjectDatabase()

    }

    private fun retrieveGroup() {
        viewModelScope.launch(Dispatchers.IO) {
            //will fetch the current user from local database and then retrieve groups accordingly
            val currentUserEntity = currentUserDatabase.getCurrentUserDao().getCurrentUser()
            loggedInUser = currentUserEntity
            groupRepository.retrieveGroupsRDatabase(currentUserEntity)
        }
    }

    override fun onCleared() {
        super.onCleared()
        //remove the network database listener
        groupRepository.deRegister()
        System.gc()
    }

}