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