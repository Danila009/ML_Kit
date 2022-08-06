package com.example.machinelearningkit.navigation
sealed class Screen(val route:String){
    object MainScreen:Screen("main_screen")
    // Translate text from one language to another
    object TranslationScreen:Screen("translation_screen")
    // Generate text replies based on previous messages
    object SmartReplyScreen:Screen("smart_reply_screen")
    // Detect faces and facial landmarks, now with Face Contours
    object FaceDetectionScreen:Screen("face_detection_screen")
    //
    object PoseDetectionScreen:Screen("pose_detection_screen")
    //
    object PoseAndFaceDetectionScreen:Screen("pose_and_face_detection_screen")
    //Scan and process barcodes
    object BarcodeScanningScreen:Screen("barcode_scanning_screen")

    object BarcodeGenerateScreen:Screen("barcode_generator_screen")
    // Detect, track and classify objects in live camera and static images
    object ObjectDetectionScreen:Screen("object_detection_screen?live_detection={live_detection}"){
        fun arguments(
            liveDetection:Boolean
        ):String = "object_detection_screen?live_detection=$liveDetection"
    }
    object ObjectStaticDetectionScreen:Screen("object_static_detection_screen/{imageId}"){
        fun arguments(
            imageId:String
        ):String = "object_static_detection_screen/$imageId"
    }
}
