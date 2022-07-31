package com.example.machinelearningkit.navigation

sealed class Screen(val route:String){
    object MainScreen:Screen("main_screen")
    // Translate text from one language to another
    object TranslationScreen:Screen("translation_screen")
    // Generate text replies based on previous messages
    object SmartReplyScreen:Screen("smart_reply_screen")
    // Detect faces and facial landmarks, now with Face Contours
    object FaceDetectionScreen:Screen("face_detection_screen")
}
