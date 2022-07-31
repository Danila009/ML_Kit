package com.example.machinelearningkit.ui.view

import android.content.Context
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.example.machinelearningkit.ui.screens.faceDetectionScreen.model.PreviewScaleType
import com.example.machinelearningkit.ui.screens.faceDetectionScreen.model.SourceInfo
import com.example.machinelearningkit.ui.screens.faceDetectionScreen.useCase.bindAnalysisUseCase
import com.google.common.util.concurrent.ListenableFuture
import com.google.mlkit.vision.face.Face

@Composable
fun CameraView(
    modifier: Modifier = Modifier,
    cameraLens:Int,
    scaleType: PreviewView.ScaleType = PreviewView.ScaleType.FILL_CENTER
) {

    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current

    var detectedFaces by remember { mutableStateOf<List<Face>>(emptyList()) }
    var sourceInfo by remember { mutableStateOf(SourceInfo(10, 10, false)) }
    
    val previewView = remember { PreviewView(context) }
    val cameraProvider = remember(sourceInfo, cameraLens) {
        ProcessCameraProvider.getInstance(context)
            .configureCamera(
                previewView, lifecycleOwner, cameraLens, context,
                setSourceInfo = { sourceInfo = it },
                onFacesDetected = { detectedFaces = it },
            )
    }

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        with(LocalDensity.current){
            Box(
                modifier = Modifier
                    .size(
                        height = sourceInfo.height.toDp(),
                        width = sourceInfo.width.toDp()
                    )
                    .scale(
                        calculateScale(
                            constraints,
                            sourceInfo,
                            PreviewScaleType.CENTER_CROP
                        )
                    )
            ){
                CameraPreview(modifier,previewView, scaleType)
                DetectedFaces(faces = detectedFaces, sourceInfo = sourceInfo)
            }
        }

    }
}

@Composable
fun DetectedFaces(faces: List<Face>, sourceInfo: SourceInfo) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val needToMirror = sourceInfo.isImageFlipped
        for (face in faces) {
            val left =
                if (needToMirror) size.width - face.boundingBox.right.toFloat() else face.boundingBox.left.toFloat()

            drawRect(
                Color.Gray, style = Stroke(2.dp.toPx()),
                topLeft = Offset(left, face.boundingBox.top.toFloat()),
                size = Size(face.boundingBox.width().toFloat(), face.boundingBox.height().toFloat())
            )
        }
    }
}

@Composable
private fun CameraPreview(
    modifier: Modifier,
    previewView: PreviewView,
    scaleType: PreviewView.ScaleType
) {
    AndroidView(
        modifier = modifier,
        factory = {
            previewView.apply {
                this.scaleType = scaleType

                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )

                implementationMode = PreviewView.ImplementationMode.COMPATIBLE


            }

            previewView
        }
    )
}

private fun ListenableFuture<ProcessCameraProvider>.configureCamera(
    previewView: PreviewView,
    lifecycleOwner: LifecycleOwner,
    cameraLens: Int,
    context: Context,
    setSourceInfo: (SourceInfo) -> Unit,
    onFacesDetected: (List<Face>) -> Unit
): ListenableFuture<ProcessCameraProvider> {
    addListener({
        val cameraSelector = CameraSelector.Builder().requireLensFacing(cameraLens).build()

        val preview = androidx.camera.core.Preview.Builder()
            .build()
            .apply {
                setSurfaceProvider(previewView.surfaceProvider)
            }

        val analysis = bindAnalysisUseCase(cameraLens, setSourceInfo, onFacesDetected)

        val imageCapture = ImageCapture.Builder()
            .setTargetRotation(previewView.display.rotation)
            .build()

        try {
            get().apply {
                unbindAll()
                bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageCapture)
                bindToLifecycle(lifecycleOwner, cameraSelector, analysis, imageCapture)
            }
        } catch (exc: Exception) {
            TODO("process errors")
        }
    }, ContextCompat.getMainExecutor(context))
    return this
}

fun calculateScale(
    constraints: Constraints,
    sourceInfo: SourceInfo,
    scaleType: PreviewScaleType
): Float {
    val heightRatio = constraints.maxHeight.toFloat() / sourceInfo.height
    val widthRatio = constraints.maxWidth.toFloat() / sourceInfo.width
    return when (scaleType) {
        PreviewScaleType.FIT_CENTER -> kotlin.math.min(heightRatio, widthRatio)
        PreviewScaleType.CENTER_CROP -> kotlin.math.max(heightRatio, widthRatio)
    }
}