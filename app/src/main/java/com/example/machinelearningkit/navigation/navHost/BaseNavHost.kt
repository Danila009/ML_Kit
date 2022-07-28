package com.example.machinelearningkit.navigation.navHost

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.machinelearningkit.navigation.Screen
import com.example.machinelearningkit.ui.screens.mainScreen.MainScreen
import com.example.machinelearningkit.ui.screens.translationScreen.TranslationScreen

@Composable
fun BaseNavHost() {

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
    }
}