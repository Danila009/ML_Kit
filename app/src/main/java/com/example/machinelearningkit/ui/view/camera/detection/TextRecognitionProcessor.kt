package com.example.machinelearningkit.ui.view.camera.detection

import android.annotation.SuppressLint
import androidx.camera.core.ImageProxy
import com.google.android.gms.tasks.TaskExecutors
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

class TextRecognitionProcessor {

    private val recognizer: TextRecognizer

    private val executor = TaskExecutors.MAIN_THREAD

    init {
        val options = TextRecognizerOptions.Builder()
            .build()

        recognizer = TextRecognition.getClient(options)
    }

    fun stop(){
        recognizer.close()
    }

    @SuppressLint("UnsafeOptInUsageError")
    fun processImageProxy(image: ImageProxy, onDetectionFinished:(Text) -> Unit){
        recognizer.process(InputImage.fromMediaImage(image.image!!, image.imageInfo.rotationDegrees))
            .addOnSuccessListener(executor) { onDetectionFinished(it) }
            .addOnCompleteListener { image.close() }
    }

    @SuppressLint("UnsafeOptInUsageError")
    fun processImageProxy(image: InputImage, onDetectionFinished:(Text) -> Unit){
        recognizer.process(image)
            .addOnSuccessListener(executor) { onDetectionFinished(it) }
    }
}