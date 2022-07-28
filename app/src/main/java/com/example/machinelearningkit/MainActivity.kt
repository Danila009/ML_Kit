package com.example.machinelearningkit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.machinelearningkit.navigation.navHost.BaseNavHost
import com.example.machinelearningkit.ui.theme.MachineLearningKitTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MachineLearningKitTheme { BaseNavHost() } }
    }
}