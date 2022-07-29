package com.example.machinelearningkit.data.userDatabase.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.machinelearningkit.data.userDatabase.model.ChatMessage
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

@Dao
interface ChatMessageDAO {

    @Query("SELECT * FROM chat_messages ORDER BY id")
    fun getChatMessages():Single<List<ChatMessage>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertChatMessage(item: ChatMessage): Completable

    @Query("DELETE FROM chat_messages WHERE id=:id")
    fun deleteChatMessageById(id:Int): Completable

    @Query("DELETE FROM chat_messages WHERE timestampMillis=:timestampMillis")
    fun deleteChatMessageByTimestampMillis(timestampMillis:Long): Completable
}