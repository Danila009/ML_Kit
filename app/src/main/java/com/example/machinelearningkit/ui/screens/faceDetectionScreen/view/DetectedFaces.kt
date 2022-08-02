package com.example.machinelearningkit.ui.screens.faceDetectionScreen.view

import android.graphics.PointF
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.example.machinelearningkit.ui.view.camera.model.SourceInfo
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceContour

@Composable
fun DetectedFaces(faces: List<Face>, sourceInfo: SourceInfo) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val needToMirror = sourceInfo.isImageFlipped
        for (face in faces) {
            val left =
                if (needToMirror) size.width - face.boundingBox.right.toFloat() else face.boundingBox.left.toFloat()

            val leftEyeContour = face.getContour(FaceContour.LEFT_EYE)?.points
            val leftRightContour = face.getContour(FaceContour.RIGHT_EYE)?.points

            val upperLipTopContour = face.getContour(FaceContour.UPPER_LIP_TOP)?.points
            val upperLipBottomContour = face.getContour(FaceContour.UPPER_LIP_BOTTOM)?.points

            val lowerLipTopContour = face.getContour(FaceContour.LOWER_LIP_TOP)?.points
            val lowerLipBottomContour = face.getContour(FaceContour.LOWER_LIP_BOTTOM)?.points

            val noseBridgeContour = face.getContour(FaceContour.NOSE_BRIDGE)?.points
            val noseBottomContour = face.getContour(FaceContour.NOSE_BOTTOM)?.points

            val leftCheekContour = face.getContour(FaceContour.LEFT_CHEEK)?.points
            val rightCheekContour = face.getContour(FaceContour.RIGHT_CHEEK)?.points

            val faceContour = face.getContour(FaceContour.FACE)?.points

            val leftEyebrowTopContour = face.getContour(FaceContour.LEFT_EYEBROW_TOP)?.points
            val rightEyebrowTopContour = face.getContour(FaceContour.RIGHT_EYEBROW_TOP)?.points

            val leftEyebrowBottomContour = face.getContour(FaceContour.LEFT_EYEBROW_BOTTOM)?.points
            val rightEyebrowBottomContour = face.getContour(FaceContour.RIGHT_EYEBROW_BOTTOM)?.points

            contour(leftEyeContour,needToMirror, Color.Yellow)
            contour(leftRightContour,needToMirror, Color.Yellow)

            contour(lowerLipTopContour,needToMirror, Color(0xFF00E1FF))
            contour(lowerLipBottomContour,needToMirror, Color(0xFF00E1FF))

            contour(upperLipTopContour,needToMirror, Color(0xFFFF9900))
            contour(upperLipBottomContour,needToMirror, Color(0xFFFF9900))

            contour(noseBridgeContour,needToMirror, Color.Green)
            contour(noseBottomContour,needToMirror, Color.Green)

            contour(leftCheekContour,needToMirror, Color(0xFFBB00FF))
            contour(rightCheekContour,needToMirror, Color(0xFFBB00FF))

            contour(faceContour,needToMirror, Color.Blue)

            contour(leftEyebrowTopContour,needToMirror, Color(0xFFFF006E))
            contour(rightEyebrowTopContour,needToMirror, Color(0xFFFF006E))

            contour(leftEyebrowBottomContour,needToMirror, Color.Magenta)
            contour(rightEyebrowBottomContour,needToMirror, Color.Magenta)

            drawRect(
                Color.Red, style = Stroke(1f),
                topLeft = Offset(left, face.boundingBox.top.toFloat()),
                size = Size(face.boundingBox.width().toFloat(), face.boundingBox.height().toFloat())
            )
        }
    }
}

private fun DrawScope.contour(
    contour: List<PointF>?,
    needToMirror:Boolean,
    color: Color = Color.Red
) {
    contour?.let {
        drawPath(
            path = Path().apply {
                contour.forEachIndexed { index, point ->
                    if (index == 0) {
                        if (needToMirror){
                            moveTo(size.width - point.x, point.y)
                        }else {
                            moveTo(point.x, point.y)
                        }
                    } else {
                        if (needToMirror){
                            lineTo(size.width - point.x, point.y)
                        }else{
                            lineTo(point.x, point.y)
                        }
                    }
                }
            },
            color = color,
            style = Stroke(1f)
        )
    }
}