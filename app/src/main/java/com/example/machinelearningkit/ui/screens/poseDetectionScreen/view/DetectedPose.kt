package com.example.machinelearningkit.ui.screens.poseDetectionScreen.view

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp
import com.example.machinelearningkit.ui.view.camera.model.SourceInfo
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseLandmark

@Composable
fun DetectedPose(
    pose: Pose?,
    sourceInfo: SourceInfo
) {
    if (pose != null) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val whitePaint = SolidColor(Color.White)
            val leftPaint = SolidColor(Color.Green)
            val rightPaint = SolidColor(Color.Yellow)

            val leftShoulder = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)
            val rightShoulder = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)
            val leftElbow = pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW)
            val rightElbow = pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW)
            val leftWrist = pose.getPoseLandmark(PoseLandmark.LEFT_WRIST)
            val rightWrist = pose.getPoseLandmark(PoseLandmark.RIGHT_WRIST)
            val leftHip = pose.getPoseLandmark(PoseLandmark.LEFT_HIP)
            val rightHip = pose.getPoseLandmark(PoseLandmark.RIGHT_HIP)
            val leftKnee = pose.getPoseLandmark(PoseLandmark.LEFT_KNEE)
            val rightKnee = pose.getPoseLandmark(PoseLandmark.RIGHT_KNEE)
            val leftAnkle = pose.getPoseLandmark(PoseLandmark.LEFT_ANKLE)
            val rightAnkle = pose.getPoseLandmark(PoseLandmark.RIGHT_ANKLE)

            val leftPinky = pose.getPoseLandmark(PoseLandmark.LEFT_PINKY)
            val rightPinky = pose.getPoseLandmark(PoseLandmark.RIGHT_PINKY)
            val leftIndex = pose.getPoseLandmark(PoseLandmark.LEFT_INDEX)
            val rightIndex = pose.getPoseLandmark(PoseLandmark.RIGHT_INDEX)
            val leftThumb = pose.getPoseLandmark(PoseLandmark.LEFT_THUMB)
            val rightThumb = pose.getPoseLandmark(PoseLandmark.RIGHT_THUMB)
            val leftHeel = pose.getPoseLandmark(PoseLandmark.LEFT_HEEL)
            val rightHeel = pose.getPoseLandmark(PoseLandmark.RIGHT_HEEL)
            val leftFootIndex = pose.getPoseLandmark(PoseLandmark.LEFT_FOOT_INDEX)
            val rightFootIndex = pose.getPoseLandmark(PoseLandmark.RIGHT_FOOT_INDEX)


            drawLine(leftShoulder, rightShoulder,sourceInfo, whitePaint)
            drawLine(leftHip, rightHip,sourceInfo, whitePaint)
            // Left body
            drawLine(leftShoulder, leftElbow,sourceInfo, leftPaint)
            drawLine(leftElbow, leftWrist,sourceInfo, leftPaint)
            drawLine(leftShoulder, leftHip,sourceInfo, leftPaint)
            drawLine(leftHip, leftKnee,sourceInfo, leftPaint)
            drawLine(leftKnee, leftAnkle,sourceInfo, leftPaint)
            drawLine(leftWrist, leftThumb,sourceInfo, leftPaint)
            drawLine(leftWrist, leftPinky,sourceInfo, leftPaint)
            drawLine(leftWrist, leftIndex,sourceInfo, leftPaint)
            drawLine(leftIndex, leftPinky,sourceInfo, leftPaint)
            drawLine(leftAnkle, leftHeel,sourceInfo, leftPaint)
            drawLine(leftHeel, leftFootIndex,sourceInfo, leftPaint)
            // Right body
            drawLine(rightShoulder, rightElbow,sourceInfo, rightPaint)
            drawLine(rightElbow, rightWrist,sourceInfo, rightPaint)
            drawLine(rightShoulder, rightHip,sourceInfo, rightPaint)
            drawLine(rightHip, rightKnee,sourceInfo, rightPaint)
            drawLine(rightKnee, rightAnkle,sourceInfo, rightPaint)
            drawLine(rightWrist, rightThumb,sourceInfo, rightPaint)
            drawLine(rightWrist, rightPinky,sourceInfo, rightPaint)
            drawLine(rightWrist, rightIndex,sourceInfo, rightPaint)
            drawLine(rightIndex, rightPinky,sourceInfo, rightPaint)
            drawLine(rightAnkle, rightHeel,sourceInfo, rightPaint)
            drawLine(rightHeel, rightFootIndex,sourceInfo, rightPaint)
        }
    }
}

private fun DrawScope.drawLine(
    startLandmark: PoseLandmark?,
    endLandmark: PoseLandmark?,
    sourceInfo:SourceInfo,
    paint: Brush
) {
    if (startLandmark != null && endLandmark != null) {
        val strokeWidth = 1.dp.toPx()

        val needToMirror = sourceInfo.isImageFlipped

        val startX =
            if (needToMirror) size.width - startLandmark.position.x else startLandmark.position.x
        val startY = startLandmark.position.y
        val endX =
            if (needToMirror) size.width - endLandmark.position.x else endLandmark.position.x
        val endY = endLandmark.position.y
        drawLine(
            brush = paint,
            start = Offset(startX, startY),
            end = Offset(endX, endY),
            strokeWidth = strokeWidth,
        )
    }
}