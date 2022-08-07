package com.example.machinelearningkit.ui.view.camera.detection

import android.annotation.SuppressLint
import androidx.camera.core.ImageProxy
import com.google.android.gms.tasks.TaskExecutors
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.segmentation.Segmentation
import com.google.mlkit.vision.segmentation.SegmentationMask
import com.google.mlkit.vision.segmentation.Segmenter
import com.google.mlkit.vision.segmentation.selfie.SelfieSegmenterOptions

class SelfieSegmentationProcessor {

    private val segmenter:Segmenter

    private val executor = TaskExecutors.MAIN_THREAD

    init {
        val options = SelfieSegmenterOptions.Builder()
            .setDetectorMode(SelfieSegmenterOptions.STREAM_MODE)
            .enableRawSizeMask()
            .build()

        segmenter = Segmentation.getClient(options)
    }

    fun stop(){
        segmenter.close()
    }

    @SuppressLint("UnsafeOptInUsageError")
    fun processImageProxy(image: ImageProxy, onDetectionFinished:(SegmentationMask) -> Unit){
        segmenter.process(InputImage.fromMediaImage(image.image!!, image.imageInfo.rotationDegrees))
            .addOnSuccessListener(executor) { onDetectionFinished(it) }
            .addOnCompleteListener { image.close() }
    }

    @SuppressLint("UnsafeOptInUsageError")
    fun processImageProxy(image: InputImage, onDetectionFinished:(SegmentationMask) -> Unit){
        segmenter.process(image)
            .addOnSuccessListener(executor) { onDetectionFinished(it) }
    }
}