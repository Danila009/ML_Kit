package com.example.machinelearningkit.ui.screens.poseAndFaceDetectionScreen

import android.Manifest
import androidx.camera.core.CameraSelector
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.machinelearningkit.navigation.Screen
import com.example.machinelearningkit.ui.view.camera.CameraControlsView
import com.example.machinelearningkit.ui.view.camera.CameraView
import com.example.machinelearningkit.ui.view.camera.switchLens
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState

@ExperimentalPermissionsApi
@Composable
fun PostAndFaceDetectionScreen(
    navController:NavController
) {

    val cameraPermission = rememberPermissionState(permission = Manifest.permission.CAMERA)

    LaunchedEffect(key1 = Unit, block = {
        cameraPermission.launchPermissionRequest()
    })

    if (cameraPermission.hasPermission){
        Box(modifier = Modifier.fillMaxSize()) {
            var lens by remember { mutableStateOf(CameraSelector.LENS_FACING_BACK) }
            CameraView(
                cameraLens = lens,
                poseDetection = true,
                faceDetection = true
            )
            CameraControlsView(
                onLensChange = { lens = switchLens(lens) }
            )
        }
    }else{
        navController.navigate(Screen.MainScreen.route)
    }
}