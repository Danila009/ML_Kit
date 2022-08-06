package com.example.machinelearningkit.ui.view.camera.detection

import android.annotation.SuppressLint
import androidx.camera.core.ImageProxy
import com.google.android.gms.tasks.TaskExecutors
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.DetectedObject
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.ObjectDetector
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions

class ObjectDetectionProcessor {

    private val objectLiveDetector:ObjectDetector
    private val objectStaticDetector:ObjectDetector

    private val executor = TaskExecutors.MAIN_THREAD

    init {

        // Live detection and tracking
        val liveDetectionOptions = ObjectDetectorOptions.Builder()
            .setDetectorMode(ObjectDetectorOptions.STREAM_MODE)
            .enableClassification()
            .build()

        // Multiple object detection in static images
        val staticDetectionOptions = ObjectDetectorOptions.Builder()
            .setDetectorMode(ObjectDetectorOptions.SINGLE_IMAGE_MODE)
            .enableMultipleObjects()
            .enableClassification()
            .build()

        objectLiveDetector = ObjectDetection.getClient(liveDetectionOptions)
        objectStaticDetector = ObjectDetection.getClient(staticDetectionOptions)

    }

    fun stop(){
        objectLiveDetector.close()
        objectStaticDetector.close()
    }

    @SuppressLint("UnsafeOptInUsageError")
    fun liveProcessImageProxy(
        image:ImageProxy,
        onDetectionFinished:(List<DetectedObject>) -> Unit
    ){
        objectLiveDetector.process(InputImage.fromMediaImage(image.image!!, image.imageInfo.rotationDegrees))
            .addOnSuccessListener(executor) { onDetectionFinished(it) }
            .addOnCompleteListener { image.close() }
    }

    @SuppressLint("UnsafeOptInUsageError")
    fun staticProcessImageProxy(
        image:InputImage,
        onDetectionFinished:(List<DetectedObject>) -> Unit
    ){
        objectStaticDetector.process(image)
            .addOnSuccessListener(executor) { onDetectionFinished(it) }
    }
}