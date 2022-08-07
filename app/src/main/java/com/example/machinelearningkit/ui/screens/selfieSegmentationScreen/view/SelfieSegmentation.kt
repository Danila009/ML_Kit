package com.example.machinelearningkit.ui.screens.selfieSegmentationScreen.view

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.example.machinelearningkit.ui.view.camera.model.SourceInfo
import com.google.mlkit.vision.segmentation.SegmentationMask

@Composable
fun SelfieSegmentation(
    segmentationMask : SegmentationMask?,
    sourceInfo: SourceInfo
) {
    segmentationMask?.let {
        val mask = segmentationMask.buffer
        val maskWidth = segmentationMask.width
        val maskHeight = segmentationMask.height

        Canvas(modifier = Modifier.fillMaxSize()){
            for (x in 0..maskWidth){
                Log.e("segmentationMask", x.toString())
            }

            for (y in 0..maskHeight){
                Log.e("segmentationMask", y.toString())
            }
        }
    }
}