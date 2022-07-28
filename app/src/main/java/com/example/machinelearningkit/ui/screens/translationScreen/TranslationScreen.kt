package com.example.machinelearningkit.ui.screens.translationScreen

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.machinelearningkit.ui.screens.translationScreen.viewModel.TranslationViewModel
import com.google.mlkit.nl.translate.TranslateLanguage
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach

@Composable
fun TranslationScreen(

) {
    val viewModel = viewModel<TranslationViewModel>()

    var startTranslateText by rememberSaveable{ mutableStateOf("Hello") }
    var finishedTranslateText by rememberSaveable{ mutableStateOf("") }

    var startTranslateLanguage by rememberSaveable{ mutableStateOf(TranslateLanguage.ENGLISH) }
    var finishedTranslateLanguage by rememberSaveable{ mutableStateOf(TranslateLanguage.RUSSIAN) }

    var startTranslateLanguageDropdownMenu by rememberSaveable{ mutableStateOf(false) }
    var finishedTranslateLanguageDropdownMenu by rememberSaveable{ mutableStateOf(false) }

    LaunchedEffect(
        startTranslateText,
        startTranslateLanguage,
        finishedTranslateLanguage
    ){
        try {
            viewModel.translate(
                translateText = startTranslateText,
                startTranslateLanguage = startTranslateLanguage,
                finishedTranslateLanguage = finishedTranslateLanguage
            ).onEach { finishedTranslateText = it }.collect()   
        }catch (e:Exception){ finishedTranslateText = e.message.toString() }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    Column {
                        TextButton(onClick = { startTranslateLanguageDropdownMenu = true }) {
                            Text(text = startTranslateLanguage)
                        }
                        TranslateLanguageDropdownMenu(
                            expanded = startTranslateLanguageDropdownMenu,
                            onDismissRequest = { startTranslateLanguageDropdownMenu = false }
                        ){ translateLanguage ->
                            startTranslateLanguage = translateLanguage
                            startTranslateLanguageDropdownMenu = false
                        }
                    }
                },
                actions = {
                    Column {
                        TextButton(onClick = { finishedTranslateLanguageDropdownMenu = true }) {
                            Text(text = finishedTranslateLanguage)
                        }
                        TranslateLanguageDropdownMenu(
                            expanded = finishedTranslateLanguageDropdownMenu,
                            onDismissRequest = { finishedTranslateLanguageDropdownMenu = false }
                        ){ translateLanguage ->
                            finishedTranslateLanguage = translateLanguage
                            finishedTranslateLanguageDropdownMenu = false
                        }
                    }
                }
            )
            
        }, content = {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth(),
                    value = startTranslateText,
                    onValueChange = { startTranslateText = it }
                )

                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth(),
                    value = finishedTranslateText,
                    readOnly = true,
                    enabled = false,
                    onValueChange = {}
                )
            }
        }
    )
}

@Composable
fun TranslateLanguageDropdownMenu(
    expanded:Boolean,
    onDismissRequest:() -> Unit,
    onClick:(String) -> Unit
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest) {
        TranslateLanguage.getAllLanguages().forEach { item ->
            DropdownMenuItem(onClick = { onClick(item)} ) {
                Text(text = item)
            }
        }
    }
}