package com.example.machinelearningkit.navigation.navHost

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.machinelearningkit.common.viewModel.daggerViewModel
import com.example.machinelearningkit.di.AppComponent
import com.example.machinelearningkit.navigation.Arguments
import com.example.machinelearningkit.navigation.Screen
import com.example.machinelearningkit.ui.screens.barcodeGeneratorScreen.BarcodeGenerateScreen
import com.example.machinelearningkit.ui.screens.barcodeScanningScreen.BarcodeScanningScreen
import com.example.machinelearningkit.ui.screens.faceDetectionScreen.FaceDetectionScreen
import com.example.machinelearningkit.ui.screens.mainScreen.MainScreen
import com.example.machinelearningkit.ui.screens.objectDetectionScreen.ObjectDetectionScreen
import com.example.machinelearningkit.ui.screens.objectStaticDetectionScreen.ObjectStaticDetectionScreen
import com.example.machinelearningkit.ui.screens.poseAndFaceDetectionScreen.PostAndFaceDetectionScreen
import com.example.machinelearningkit.ui.screens.poseDetectionScreen.PoseDetectionScreen
import com.example.machinelearningkit.ui.screens.selfieSegmentationScreen.SelfieSegmentationScreen
import com.example.machinelearningkit.ui.screens.smartReplyScreen.SmartReplyScreen
import com.example.machinelearningkit.ui.screens.textRecognitionScreen.TextRecognitionScreen
import com.example.machinelearningkit.ui.screens.translationScreen.TranslationScreen
import com.google.accompanist.permissions.ExperimentalPermissionsApi

@ExperimentalMaterialApi
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

        composable(Screen.BarcodeGenerateScreen.route){
            BarcodeGenerateScreen()
        }

        composable(
            route = Screen.ObjectDetectionScreen.route,
            arguments = listOf(
                navArgument(
                    name = Arguments.LiveDetection.name
                ){
                    type = NavType.BoolType
                }
            )
        ){
            ObjectDetectionScreen(
                navController = navController,
                liveDetection = it.arguments?.getBoolean(Arguments.LiveDetection.name) ?: false
            )
        }

        composable(
            route = Screen.ObjectStaticDetectionScreen.route,
            arguments = listOf(
                navArgument(Arguments.ImageId.name){
                    type = NavType.StringType
                }
            )
        ) {
            ObjectStaticDetectionScreen(
                navController = navController,
                imageId = it.arguments?.getString(Arguments.ImageId.name).toString()
            )
        }

        composable(
            route = Screen.SelfieSegmentationScreen.route,
            arguments = listOf(
                navArgument(Arguments.LiveDetection.name){
                    type = NavType.BoolType
                }
            )
        ){
            SelfieSegmentationScreen(
                navController = navController,
                liveDetection = it.arguments?.getBoolean(Arguments.LiveDetection.name) ?: false
            )
        }

        composable(
            route = Screen.TextRecognitionScreen.route,
            arguments = listOf(
                navArgument(Arguments.LiveDetection.name){
                    type = NavType.BoolType
                }
            )

        ){
            TextRecognitionScreen(
                navController = navController,
                liveDetection = it.arguments?.getBoolean(Arguments.LiveDetection.name) ?: false
            )
        }
    }
}