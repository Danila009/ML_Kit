package com.example.machinelearningkit.data.userDatabase.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_messages")
data class ChatMessage(
    @PrimaryKey(autoGenerate = true) val id:Int = 0,
    val messageText:String,
    val timestampMillis:Long = System.currentTimeMillis()
)