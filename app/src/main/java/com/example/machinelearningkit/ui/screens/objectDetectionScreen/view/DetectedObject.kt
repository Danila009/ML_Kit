package com.example.machinelearningkit.ui.screens.objectDetectionScreen.view

import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import com.example.machinelearningkit.ui.view.camera.model.SourceInfo
import com.google.mlkit.vision.objects.DetectedObject

@Composable
fun DetectedObject(
    detectedObjects:List<DetectedObject>?,
    sourceInfo: SourceInfo
) {
    val context = LocalContext.current

    detectedObjects?.let {

        val needToMirror = sourceInfo.isImageFlipped

        Canvas(
            modifier = Modifier.fillMaxSize()
        ){
            for (detectedObject in detectedObjects){

                val left =
                    if (needToMirror) size.width - detectedObject.boundingBox.right.toFloat() else
                        detectedObject.boundingBox.left.toFloat()

                drawRect(
                    Color.Red, style = Stroke(1f),
                    topLeft = Offset(left, detectedObject.boundingBox.top.toFloat()),
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