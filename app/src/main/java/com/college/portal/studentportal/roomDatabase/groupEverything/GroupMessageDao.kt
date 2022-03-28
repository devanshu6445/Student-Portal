package com.college.portal.studentportal.roomDatabase.groupEverything

import androidx.lifecycle.LiveData
import androidx.room.*
import com.college.portal.studentportal.data.model.GroupMessageData
import kotlinx.coroutines.flow.Flow


@Dao
interface GroupMessageDao {

    @Insert
    fun insertJava(user: GroupMessageInfo)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertParticipant(participant: Participant)

    @Insert
    fun insertParticipantInBulk(participantList: List<Participant>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMessageRequest(messageRequest: MessageRequest)

    @Update
    fun updateParticipant(participant: Participant)

    @Delete
    fun deleteMessage(groupMessageData: GroupMessageInfo)

    @Delete
    fun deleteParticipant(participant: Participant)

    @Delete
    fun removeMessageRequest(messageRequest: MessageRequest)

    @Query("SELECT * FROM groupParticipants")
    fun getAllParticipants(): LiveData<MutableList<Participant>>

    @Query("SELECT * FROM groupParticipants WHERE uid = :participantUid")
    fun searchParticipant(participantUid: String): Participant?

    @Query("SELECT * FROM groupParticipants WHERE uid = :participantUid")
    fun searchParticipantFlow(participantUid: String): Flow<Participant?>

    @Query("SELECT * FROM messages")
    fun getMessages(): Flow<MutableList<GroupMessageData>>

    @Query("SELECT * FROM messages")
    fun getAllMessagesNotLiveData() : List<GroupMessageData>

    @Query("SELECT * FROM messages where messageTimeStamp like :messageTimeStamp")
    fun getMessagesNotLiveData(messageTimeStamp: Long): GroupMessageInfo?

    @Query("SELECT * FROM messages LIMIT 1")
    fun getLastMessageLiveData(): LiveData<GroupMessageData>

    @Query("SELECT * FROM message_requests")
    fun getAllMessageRequests(): Flow<List<MessageRequest>>

}