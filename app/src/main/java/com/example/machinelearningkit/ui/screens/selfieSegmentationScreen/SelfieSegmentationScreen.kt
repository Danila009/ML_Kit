package com.example.machinelearningkit.ui.screens.selfieSegmentationScreen

import android.Manifest
import android.annotation.SuppressLint
import androidx.camera.core.CameraSelector
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.example.machinelearningkit.data.userDatabase.source.StorageImagesPageSource
import com.example.machinelearningkit.navigation.Screen
import com.example.machinelearningkit.ui.view.camera.CameraControlsView
import com.example.machinelearningkit.ui.view.camera.CameraView
import com.example.machinelearningkit.ui.view.camera.switchLens
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

@ExperimentalPermissionsApi
@Composable
fun SelfieSegmentationScreen(
    navController: NavController,
    liveDetection:Boolean
) {
    when(liveDetection){
        true -> LiveDetection(navController)
        false -> StaticDetection(navController)
    }
}

@ExperimentalPermissionsApi
@Composable
fun LiveDetection(navController: NavController) {
    val cameraPermission = rememberPermissionState(permission = Manifest.permission.CAMERA)

    LaunchedEffect(key1 = Unit, block = {
        cameraPermission.launchPermissionRequest()
    })

    if (cameraPermission.hasPermission){
        Box(modifier = Modifier.fillMaxSize()) {
            var lens by remember { mutableStateOf(CameraSelector.LENS_FACING_BACK) }
            CameraView(
                cameraLens = lens,
                selfieSegmentation = true
            )
            CameraControlsView(
                onLensChange = { lens = switchLens(lens) }
            )
        }
    }else{
        navController.navigate(Screen.MainScreen.route)
    }
}

@SuppressLint("FlowOperatorInvokedInComposition")
@ExperimentalPermissionsApi
@Composable
fun StaticDetection(navController: NavController) {
    val context = LocalContext.current

    val storagePermission = rememberPermissionState(permission = Manifest.permission.WRITE_EXTERNAL_STORAGE)

    LaunchedEffect(key1 = Unit, block = {
        storagePermission.launchPermissionRequest()
    })

    if (storagePermission.hasPermission){

        val responseImages = Pager(PagingConfig(pageSize = 10)){
            StorageImagesPageSource(
                context = context
            )
        }.flow.cachedIn(CoroutineScope(Dispatchers.IO))

        val images = responseImages.collectAsLazyPagingItems()

        LazyColumn {
            items(images){ image ->

                Column(
                    modifier = Modifier.clickable {

                    }
                ) {

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        SubcomposeAsyncImage(
                            model = image?.uri,
                            contentDescription = null,
                            modifier = Modifier
                                .size(150.dp)
                                .padding(5.dp)
                        ) {
                            val state = painter.state
                            if (
                                state is AsyncImagePainter.State.Loading ||
                                state is AsyncImagePainter.State.Error
                            ) {
                                CircularProgressIndicator()
                            } else {
                                SubcomposeAsyncImageContent()
                            }
                        }

                        Text(
                            text = image?.title ?: "",
                            modifier = Modifier.padding(5.dp),
                            fontWeight = FontWeight.W900
                        )
                    }

                    Divider()
                }

            }
        }

    }else{
        navController.navigate(Screen.MainScreen.route)
    }
}