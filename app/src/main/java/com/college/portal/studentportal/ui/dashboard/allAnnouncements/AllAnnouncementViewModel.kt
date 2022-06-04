package com.college.portal.studentportal.ui.dashboard.allAnnouncements

import androidx.lifecycle.*
import com.college.portal.studentportal.data.model.LoggedInUser
import com.college.portal.studentportal.roomDatabase.announcement.AnnouncementDatabase
import com.college.portal.studentportal.roomDatabase.announcement.AnnouncementEntity
import com.college.portal.studentportal.ui.notifications.announcement.AnnouncementRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AllAnnouncementViewModel(database: AnnouncementDatabase, loggedInUser: LoggedInUser) : ViewModel() {

    private val announcementRepository = AnnouncementRepository(database)
    private val _announcementList = MutableLiveData<List<AnnouncementEntity>>()
    val announcementList:LiveData<List<AnnouncementEntity>> = _announcementList
    init {
        announcementRepository.updateAnnouncementDatabase(loggedInUser.userCourse,loggedInUser.userSection)
        viewModelScope.launch {
            announcementRepository.getAllAnnouncements()?.collect {
                withContext(Dispatchers.Main){
                    _announcementList.value = it
                }
            }
        }
    }

    class AllAnnouncementViewModelFactory(private val database: AnnouncementDatabase,private val loggedInUser: LoggedInUser): ViewModelProvider.Factory{

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if(modelClass.isAssignableFrom(AllAnnouncementViewModel::class.java)){
                return AllAnnouncementViewModel(database,loggedInUser) as T
            }else{
                throw IllegalStateException("Unknown class")
            }
        }

    }
}