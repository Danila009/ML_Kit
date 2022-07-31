package com.example.machinelearningkit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.machinelearningkit.di.DaggerAppComponent
import com.example.machinelearningkit.navigation.navHost.BaseNavHost
import com.example.machinelearningkit.ui.theme.MachineLearningKitTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi

@ExperimentalPermissionsApi
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appComponent = DaggerAppComponent
            .builder()
            .context(this)
            .build()

        setContent {
            MachineLearningKitTheme {
                BaseNavHost(
                    appComponent = appComponent
                )
            }
        }
    }
}