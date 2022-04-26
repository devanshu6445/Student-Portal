package com.college.portal.studentportal.roomDatabase.groupEverything

import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow


@Dao
interface GroupMessageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertJava(user: GroupMessageInfo)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMessage(groupMessageData: GroupMessageInfo)

    @Insert(onConflict = OnConflictStrategy.REPLACE)

    fun insertParticipant(participant: Participant)

    @Insert
    fun insertParticipantInBulk(participantList: List<Participant>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMessageRequest(messageRequest: MessageRequest)

    @Update
    fun updateParticipant(participant: Participant)

    @Update
    fun updateMessageRequest(messageRequest: MessageRequest)

    @Update
    fun updateMessage(groupMessageData: GroupMessageInfo)


    @Delete
    fun deleteMessage(groupMessageData: GroupMessageInfo)

    @Delete
    fun deleteParticipant(participant: Participant)

    @Query("DELETE FROM message_requests WHERE messageID = :messageId")
    fun removeMessageRequest(messageId: String)

    @Delete
    fun deleteMessageInBulk(list: List<GroupMessageInfo>)

    @Query("SELECT * FROM groupParticipants")
    fun getAllParticipants(): Flow<MutableList<Participant>>

    @Query("SELECT * FROM groupParticipants WHERE uid = :participantUid")
    fun searchParticipant(participantUid: String): Participant?

    @Query("SELECT * FROM groupParticipants WHERE uid = :participantUid")
    fun searchParticipantFlow(participantUid: String): Flow<Participant?>

    @Query("SELECT * FROM messages ORDER BY messageTimeStamp DESC")
    fun getMessages(): Flow<MutableList<GroupMessageInfo>>

    @Query("SELECT * FROM messages")
    fun getAllMessagesNotLiveData() : List<GroupMessageInfo>

    @Query("SELECT * FROM messages where messageTimeStamp like :messageTimeStamp")
    fun getMessagesNotLiveData(messageTimeStamp: Long): GroupMessageInfo?

    @Query("SELECT * FROM messages LIMIT 1")
    fun getLastMessageLiveData(): LiveData<GroupMessageInfo>

    @Query("SELECT * FROM message_requests")
    fun getAllMessageRequests(): Flow<List<MessageRequest>>


}