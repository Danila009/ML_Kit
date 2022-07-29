package com.example.machinelearningkit.data.userDatabase.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.machinelearningkit.data.userDatabase.dao.ChatMessageDAO
import com.example.machinelearningkit.data.userDatabase.model.ChatMessage

@Database(
    entities = [ChatMessage::class],
    version = 1
)
abstract class UserDatabase:RoomDatabase() {

    abstract fun chatMessageDAO():ChatMessageDAO
}