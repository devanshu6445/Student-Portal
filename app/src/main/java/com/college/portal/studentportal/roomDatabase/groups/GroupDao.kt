package com.college.portal.studentportal.roomDatabase.groups

import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface GroupDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertGroup(groupData: BasicGroupData)

    @Query("SELECT * FROM groupData")
    fun getAllGroup(): Flow<MutableList<BasicGroupData>>

    @Query("SELECT * FROM groupData WHERE groupID like :searchText OR groupName like :searchText")
    fun searchAllAboutAGroup(searchText: String) : List<BasicGroupData>

    @Update
    fun updateGroup(groupData: BasicGroupData)

    @Delete
    fun deleteGroup(groupData: BasicGroupData)

    @Query("SELECT * FROM groupData WHERE groupID = :groupID")
    fun searchGroup(groupID: String): BasicGroupData?
}