package com.example.machinelearningkit.ui.screens.objectStaticDetectionScreen

import android.Manifest
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.machinelearningkit.data.userDatabase.useCase.GetStorageImageByIdUseCase
import com.example.machinelearningkit.navigation.Screen
import com.example.machinelearningkit.ui.view.camera.detection.ObjectDetectionProcessor
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.DetectedObject
import java.io.IOException

@ExperimentalPermissionsApi
@Composable
fun ObjectStaticDetectionScreen(
    navController: NavController,
    imageId:String
) {
    val context = LocalContext.current

    val storagePermission = rememberPermissionState(permission = Manifest.permission.WRITE_EXTERNAL_STORAGE)

    LaunchedEffect(key1 = Unit, block = {
        storagePermission.launchPermissionRequest()
    })

    if (storagePermission.hasPermission){

        val objectDetectionProcessor = ObjectDetectionProcessor()
        val getStorageImageByIdUseCase = GetStorageImageByIdUseCase()

        val image = getStorageImageByIdUseCase.invoke(imageId, context)

        var detectionObjects by remember { mutableStateOf<List<DetectedObject>?>(null) }

        var imageInput:InputImage? = null

        image?.let {
            try {
                imageInput = InputImage.fromFilePath(context, image.uri)

                imageInput?.let {
                    objectDetectionProcessor.staticProcessImageProxy(
                        image = imageInput!!,
                        onDetectionFinished = { detectionObjects = it }
                    )
                }
            } catch (e: IOException) {}

            detectionObjects?.let { detectedObjects ->
                Canvas(modifier = Modifier.fillMaxSize()){
                    for (detectedObject in detectedObjects) {

                        imageInput?.let {
                            it.bitmapInternal?.let { bitmap ->
                                drawImage(bitmap.asImageBitmap())
                            }
                        }

                        drawRect(
                            Color.Red, style = Stroke(1f),
                            topLeft = Offset(
                                detectedObject.boundingBox.left.toFloat(),
                                detectedObject.boundingBox.top.toFloat()
                            ),
                            size = Size(
                                detectedObject.boundingBox.width().toFloat(),
                                detectedObject.boundingBox.height().toFloat()
                            )
                        )

                        for (label in detectedObject.labels) {
                            Toast.makeText(context, label.text, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

    }else{
        navController.navigate(Screen.MainScreen.route)
    }
}