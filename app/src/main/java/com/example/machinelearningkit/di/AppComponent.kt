package com.example.machinelearningkit.di

import android.content.Context
import com.example.machinelearningkit.ui.screens.smartReplyScreen.viewModel.SmartReplyViewModel
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@[Singleton Component(modules = [DatabaseModule::class])]
interface AppComponent {

    fun smartReplyViewModel(): SmartReplyViewModel

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun context(context: Context):Builder

        fun build():AppComponent
    }
}