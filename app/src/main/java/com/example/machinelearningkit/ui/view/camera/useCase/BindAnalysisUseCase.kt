package com.example.machinelearningkit.ui.view.camera.useCase

import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.example.machinelearningkit.ui.view.camera.detection.*
import com.example.machinelearningkit.ui.view.camera.model.SourceInfo
import com.google.android.gms.tasks.TaskExecutors
import com.google.mlkit.common.MlKitException
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.objects.DetectedObject
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.segmentation.SegmentationMask
import com.google.mlkit.vision.text.Text

fun bindAnalysisUseCase(
    lens: Int,
    poseDetection:Boolean,
    faceDetection:Boolean,
    barcodeScanner:Boolean,
    objectDetection:Boolean,
    selfieSegmentation:Boolean,
    textRecognition:Boolean,
    setSourceInfo: (SourceInfo) -> Unit,
    onFacesDetected: (List<Face>) -> Unit,
    onPoseDetected: (Pose) -> Unit,
    onBarcodeDetected:(List<Barcode>) -> Unit,
    onObjectDetected:(List<DetectedObject>) -> Unit,
    onSelfieSegmentation:(SegmentationMask) -> Unit,
    onTextRecognition:(Text) -> Unit
): ImageAnalysis? {

    val textRecognitionProcessor = try {
        if (textRecognition){
            TextRecognitionProcessor()
        }else{
            null
        }
    }catch (e:Exception){
        Log.e("CAMERA", "Can not create image processor", e)
        return null
    }

    val selfieSegmentationProcessor = try {
        if (selfieSegmentation){
            SelfieSegmentationProcessor()
        }else{
            null
        }
    }catch (e:Exception){
        Log.e("CAMERA", "Can not create image processor", e)
        return null
    }

    val objectProcessor = try {
        if (objectDetection){
            ObjectDetectionProcessor()
        }else{
            null
        }
    }catch (e:Exception){
        Log.e("CAMERA", "Can not create image processor", e)
        return null
    }

    val barcodeProcessor = try {
        if (barcodeScanner){
            BarcodeScannerProcessor()
        }else{
            null
        }
    }catch (e:Exception){
        Log.e("CAMERA", "Can not create image processor", e)
        return null
    }

    val imageProcessor = try {
        if (faceDetection){
            FaceDetectorProcessor()
        }else{
            null
        }
    } catch (e: Exception) {
        Log.e("CAMERA", "Can not create image processor", e)
        return null
    }

    val poseProcessor = try {
        if (poseDetection){
            PoseDetectorProcessor()
        }else{
            null
        }
    } catch (e: Exception) {
        Log.e("CAMERA", "Can not create pose processor", e)
        return null
    }

    val builder = ImageAnalysis.Builder()
    val analysisUseCase = builder.build()

    var sourceInfoUpdated = false

    analysisUseCase.setAnalyzer(
        TaskExecutors.MAIN_THREAD
    ) { imageProxy: ImageProxy ->
        if (!sourceInfoUpdated) {
            setSourceInfo(obtainSourceInfo(lens, imageProxy))
            sourceInfoUpdated = true
        }
        try {
            imageProcessor?.let {
                imageProcessor.processImageProxy(imageProxy, onFacesDetected)
            }
            poseProcessor?.let {
                poseProcessor.processImageProxy(imageProxy, onPoseDetected)
            }
            barcodeProcessor?.let {
                barcodeProcessor.processImageProxy(imageProxy, onBarcodeDetected)
            }
            objectProcessor?.let {
                objectProcessor.liveProcessImageProxy(imageProxy,onObjectDetected)
            }
            selfieSegmentationProcessor?.let {
                selfieSegmentationProcessor.processImageProxy(imageProxy, onSelfieSegmentation)
            }
            textRecognitionProcessor?.let {
                textRecognitionProcessor.processImageProxy(imageProxy, onTextRecognition)
            }
        } catch (e: MlKitException) {
            Log.e(
                "CAMERA", "Failed to process image. Error: " + e.localizedMessage
            )
        }
    }
    return analysisUseCase
}


private fun obtainSourceInfo(lens: Int, imageProxy: ImageProxy): SourceInfo {
    val isImageFlipped = lens == CameraSelector.LENS_FACING_FRONT
    val rotationDegrees = imageProxy.imageInfo.rotationDegrees
    return if (rotationDegrees == 0 || rotationDegrees == 180) {
        SourceInfo(
            height = imageProxy.height, width = imageProxy.width, isImageFlipped = isImageFlipped
        )
    } else {
        SourceInfo(
            height = imageProxy.width, width = imageProxy.height, isImageFlipped = isImageFlipped
        )
    }
}