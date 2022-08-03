package com.example.machinelearningkit.navigation.navHost

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.machinelearningkit.common.viewModel.daggerViewModel
import com.example.machinelearningkit.di.AppComponent
import com.example.machinelearningkit.navigation.Screen
import com.example.machinelearningkit.ui.screens.barcodeScanningScreen.BarcodeScanningScreen
import com.example.machinelearningkit.ui.screens.faceDetectionScreen.FaceDetectionScreen
import com.example.machinelearningkit.ui.screens.mainScreen.MainScreen
import com.example.machinelearningkit.ui.screens.poseAndFaceDetectionScreen.PostAndFaceDetectionScreen
import com.example.machinelearningkit.ui.screens.poseDetectionScreen.PoseDetectionScreen
import com.example.machinelearningkit.ui.screens.smartReplyScreen.SmartReplyScreen
import com.example.machinelearningkit.ui.screens.translationScreen.TranslationScreen
import com.google.accompanist.permissions.ExperimentalPermissionsApi

@ExperimentalPermissionsApi
@Composable
fun BaseNavHost(
    appComponent: AppComponent
) {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.MainScreen.route
    ){
        composable(Screen.MainScreen.route){
            MainScreen(
                navController = navController
            )
        }

        composable(Screen.TranslationScreen.route){
            TranslationScreen()
        }

        composable(Screen.SmartReplyScreen.route){
            SmartReplyScreen(
                viewModel = daggerViewModel { appComponent.smartReplyViewModel() }
            )
        }

        composable(Screen.FaceDetectionScreen.route){
            FaceDetectionScreen(
                navController = navController
            )
        }

        composable(Screen.BarcodeScanningScreen.route){
            BarcodeScanningScreen(
                navController = navController
            )
        }

        composable(Screen.PoseDetectionScreen.route){
            PoseDetectionScreen(
                navController = navController
            )
        }

        composable(Screen.PoseAndFaceDetectionScreen.route){
            PostAndFaceDetectionScreen(
                navController = navController
            )
        }
    }
}