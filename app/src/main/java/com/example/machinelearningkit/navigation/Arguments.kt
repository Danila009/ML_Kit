package com.example.machinelearningkit.navigation

sealed class Arguments(val name:String) {
    object LiveDetection:Arguments("live_detection")
    object ImageId:Arguments("imageId")
}