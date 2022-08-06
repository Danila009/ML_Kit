package com.example.machinelearningkit.ui.screens.mainScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.machinelearningkit.navigation.Screen

private data class ButtonData(
    val name:String,
    val onClick:() -> Unit
)

@Composable
fun MainScreen(
    navController: NavController
) {

    val buttonData = listOf(
        ButtonData(
            name = "Translate text from one language to another",
            onClick = { navController.navigate(Screen.TranslationScreen.route) }
        ),
        ButtonData(
            name = "Generate text replies based on previous messages",
            onClick = { navController.navigate(Screen.SmartReplyScreen.route) }
        ),
        ButtonData(
            name = "Detect faces and facial landmarks, now with Face Contours",
            onClick = { navController.navigate(Screen.FaceDetectionScreen.route) }
        ),
        ButtonData(
            name = "Detect poses",
            onClick = { navController.navigate(Screen.PoseDetectionScreen.route) }
        ),
        ButtonData(
            name = "Detect poses and faces",
            onClick = { navController.navigate(Screen.PoseAndFaceDetectionScreen.route) }
        ),
        ButtonData(
            name = "Barcode scanning",
            onClick = { navController.navigate(Screen.BarcodeScanningScreen.route) }
        ),
        ButtonData(
            name = "QR Code Generate",
            onClick = { navController.navigate(Screen.BarcodeGenerateScreen.route) }
        ),
        ButtonData(
            name = "Detect, track and classify objects in live camera",
            onClick = { navController.navigate(Screen.ObjectDetectionScreen.arguments(true)) }
        ),
        ButtonData(
            name = "Detect, track and classify objects in static camera",
            onClick = { navController.navigate(Screen.ObjectDetectionScreen.arguments(false)) }
        )
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        buttonData.forEach { item ->
            OutlinedButton(
                modifier = Modifier.padding(10.dp),
                onClick = item.onClick
            ) {
                Text(
                    text = item.name,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}