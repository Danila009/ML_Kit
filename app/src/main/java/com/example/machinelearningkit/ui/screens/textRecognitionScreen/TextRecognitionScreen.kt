package com.example.machinelearningkit.ui.screens.textRecognitionScreen

import android.Manifest
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.camera.core.CameraSelector
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.example.machinelearningkit.common.extensions.copyText
import com.example.machinelearningkit.navigation.Screen
import com.example.machinelearningkit.ui.view.camera.CameraControlsView
import com.example.machinelearningkit.ui.view.camera.CameraView
import com.example.machinelearningkit.ui.view.camera.detection.TextRecognitionProcessor
import com.example.machinelearningkit.ui.view.camera.switchLens
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text

@ExperimentalPermissionsApi
@Composable
fun TextRecognitionScreen(
    navController: NavController,
    liveDetection:Boolean
) {
   when(liveDetection){
       true -> LiveDetection(navController)
       false -> StaticDetection()
   }
}

@ExperimentalPermissionsApi
@Composable
private fun LiveDetection(
    navController: NavController
) {
    val cameraPermission = rememberPermissionState(permission = Manifest.permission.CAMERA)

    LaunchedEffect(key1 = Unit, block = {
        cameraPermission.launchPermissionRequest()
    })

    if (cameraPermission.hasPermission){
        Box(modifier = Modifier.fillMaxSize()) {
            var lens by remember { mutableStateOf(CameraSelector.LENS_FACING_BACK) }
            CameraView(
                cameraLens = lens,
                textRecognition = true
            )
            CameraControlsView(
                onLensChange = { lens = switchLens(lens) }
            )
        }
    }else{
        navController.navigate(Screen.MainScreen.route)
    }
}

@Composable
private fun StaticDetection() {
    val context = LocalContext.current

    val textRecognitionProcessor = TextRecognitionProcessor()

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val bitmap =  remember { mutableStateOf<Bitmap?>(null) }

    var detectionTextRecognition by remember { mutableStateOf<Text?>(null) }

    val imageCropLauncher = rememberLauncherForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            imageUri = result.uriContent
        }
    }

    if (imageUri != null) {
        if (Build.VERSION.SDK_INT < 28) {
            bitmap.value = MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri)
        } else {
            val source = ImageDecoder.createSource(context.contentResolver, imageUri!!)
            bitmap.value = ImageDecoder.decodeBitmap(source)
        }
    }

    Button(onClick = {
        imageCropLauncher.launch(CropImageContractOptions(null, CropImageOptions()))
    }) {
        Text("Pick image to crop")
    }

    bitmap.value?.let {

        try {
            val imageInput = InputImage.fromBitmap(bitmap.value!!, 0)

            textRecognitionProcessor.processImageProxy(
                image = imageInput,
                onDetectionFinished = { detectionTextRecognition = it }
            )

        } catch (e: Exception) {}

        LazyColumn {
            item {

                detectionTextRecognition?.let { text ->

                    Image(
                        bitmap = bitmap.value!!.asImageBitmap(),
                        contentDescription = null
                    )

                    TextButton(onClick = {
                        text.text.copyText(context)
                        Toast
                            .makeText(context, "Текст скопирован", Toast.LENGTH_SHORT)
                            .show()
                    }) {
                        Text(
                            text = text.text
                        )
                    }
                }
            }
        }
    }
}