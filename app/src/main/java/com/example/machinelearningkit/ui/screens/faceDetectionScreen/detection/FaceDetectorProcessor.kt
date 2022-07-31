package com.example.machinelearningkit.ui.screens.faceDetectionScreen.detection

import android.annotation.SuppressLint
import androidx.camera.core.ImageProxy
import com.google.android.gms.tasks.TaskExecutors
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions

class FaceDetectorProcessor {

    private val detector: FaceDetector

    private val executor = TaskExecutors.MAIN_THREAD

    init {
        val faceDetectorOptions = FaceDetectorOptions.Builder()

            .build()

        detector = FaceDetection.getClient(faceDetectorOptions)
    }

    fun stop(){
        detector.close()
    }

    @SuppressLint("UnsafeOptInUsageError")
    fun processImageProxy(
        image:ImageProxy,
        onDetectionFinished:(List<Face>) -> Unit
    ){
        detector.process(InputImage.fromMediaImage(image.image!!, image.imageInfo.rotationDegrees))
            .addOnSuccessListener { onDetectionFinished(it) }
            .addOnCompleteListener { image.close() }
    }
}