package com.example.machinelearningkit

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.material.ExperimentalMaterialApi
import androidx.fragment.app.FragmentActivity
import com.example.machinelearningkit.di.DaggerAppComponent
import com.example.machinelearningkit.navigation.navHost.BaseNavHost
import com.example.machinelearningkit.ui.view.camera.detection.FaceDetectorProcessor
import com.example.machinelearningkit.ui.view.camera.detection.PoseDetectorProcessor
import com.example.machinelearningkit.ui.theme.MachineLearningKitTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi

@ExperimentalMaterialApi
@ExperimentalPermissionsApi
class MainActivity : FragmentActivity() {
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

    override fun onDestroy() {
        super.onDestroy()
        PoseDetectorProcessor().stop()
        FaceDetectorProcessor().stop()
    }
}