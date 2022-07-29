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
            name = "Translation text",
            onClick = { navController.navigate(Screen.TranslationScreen.route) }
        ),
        ButtonData(
            name = "Smart Reply",
            onClick = { navController.navigate(Screen.SmartReplyScreen.route) }
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
                Text(text = item.name)
            }
        }
    }
}