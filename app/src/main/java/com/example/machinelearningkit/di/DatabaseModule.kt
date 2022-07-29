package com.example.machinelearningkit.di

import android.content.Context
import androidx.room.Room
import com.example.machinelearningkit.data.userDatabase.dao.ChatMessageDAO
import com.example.machinelearningkit.data.userDatabase.database.UserDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule {

    @[Provides Singleton]
    fun providerChatMessageDAO(
        userDatabase: UserDatabase
    ):ChatMessageDAO = userDatabase.chatMessageDAO()

    @[Provides Singleton]
    fun providerUserDatabase(
        context: Context
    ):UserDatabase = Room.databaseBuilder(
        context.applicationContext,
        UserDatabase::class.java,
        "user_database"
    ).build()
}