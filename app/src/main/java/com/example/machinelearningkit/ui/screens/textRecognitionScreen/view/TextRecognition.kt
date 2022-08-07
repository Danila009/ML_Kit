package com.example.machinelearningkit.ui.screens.textRecognitionScreen.view

import androidx.compose.foundation.layout.offset
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.IntOffset
import com.example.machinelearningkit.ui.view.camera.model.SourceInfo
import com.google.mlkit.vision.text.Text

@Composable
fun TextRecognition(
    text:Text?,
    sourceInfo: SourceInfo
) {
    text?.let {
        val needToMirror = sourceInfo.isImageFlipped

        val screenWidth = LocalConfiguration.current.screenWidthDp

        for (block in text.textBlocks){

            for (line in block.lines) {
                val lineText = line.text
                val lineFrame = line.boundingBox

                lineFrame?.let {

                    val left =
                        if (needToMirror) screenWidth - lineFrame.right else
                            lineFrame.left

                    Text(
                        text = lineText,
                        modifier = Modifier
                            .offset {
                                IntOffset(left, lineFrame.top)
                            }
//                            .size(
//                                lineFrame.width().dp,
//                                lineFrame.height().dp
//                            )

                    )

                }

            }
        }
    }
}