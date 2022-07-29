package com.example.machinelearningkit.ui.screens.translationScreen

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.work.*
import com.example.machinelearningkit.ui.screens.translationScreen.viewModel.TranslationViewModel
import com.example.machinelearningkit.ui.screens.translationScreen.work.DeleteAllModelLanguageWorker
import com.example.machinelearningkit.ui.screens.translationScreen.work.DownloadAllModelLanguageWorker
import com.google.mlkit.nl.translate.TranslateLanguage
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach

@Composable
fun TranslationScreen(

) {
    val context = LocalContext.current
    val owner = LocalLifecycleOwner.current

    val viewModel = viewModel<TranslationViewModel>()

    var startTranslateText by rememberSaveable{ mutableStateOf("Hello") }
    var finishedTranslateText by rememberSaveable{ mutableStateOf("") }

    var startTranslateLanguage by rememberSaveable{ mutableStateOf(TranslateLanguage.ENGLISH) }
    var finishedTranslateLanguage by rememberSaveable{ mutableStateOf(TranslateLanguage.RUSSIAN) }

    var startTranslateLanguageDropdownMenu by rememberSaveable{ mutableStateOf(false) }
    var finishedTranslateLanguageDropdownMenu by rememberSaveable{ mutableStateOf(false) }

    val workerDownloadConstraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

    val downloadedModelLanguageWorker = OneTimeWorkRequestBuilder<DownloadAllModelLanguageWorker>()
        .setConstraints(workerDownloadConstraints)
        .build()

    val deleteModelLanguageWorker = OneTimeWorkRequestBuilder<DeleteAllModelLanguageWorker>()
        .build()

    val workManager = WorkManager.getInstance(context)

    val outputWorkDownloadInfo = workManager.getWorkInfoByIdLiveData(downloadedModelLanguageWorker.id)
    val outputWorkDeleteInfo = workManager.getWorkInfoByIdLiveData(deleteModelLanguageWorker.id)

    outputWorkDownloadInfo.observe(owner) { workerInfo ->
        if (workerInfo.state == WorkInfo.State.FAILED) {
            workerInfo.outputData.getString(DownloadAllModelLanguageWorker.ON_ERROR_KEY)?.let { message ->
                finishedTranslateLanguage = message
            }
        } else if(workerInfo.state == WorkInfo.State.SUCCEEDED){
            Toast.makeText(context, "Succeeded", Toast.LENGTH_SHORT).show()
        }
    }

    outputWorkDeleteInfo.observe(owner) {workerInfo ->
        if (workerInfo.state == WorkInfo.State.FAILED) {
            workerInfo.outputData.getString(DeleteAllModelLanguageWorker.ON_ERROR_KEY)?.let { message ->
                finishedTranslateLanguage = message
            }
        } else if(workerInfo.state == WorkInfo.State.SUCCEEDED){
            Toast.makeText(context, "Succeeded", Toast.LENGTH_SHORT).show()
        }
    }

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

            Column {

                Column {
                    OutlinedButton(onClick = { workManager.enqueue(downloadedModelLanguageWorker) }) {
                        Text(text = "Download all translate language")
                    }

                    OutlinedButton(onClick = { workManager.enqueue(deleteModelLanguageWorker) }) {
                        Text(text = "Delete all translate language")
                    }
                }

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
        onDismissRequest = onDismissRequest
    ) {
        TranslateLanguage.getAllLanguages().forEach { item ->
            DropdownMenuItem(onClick = { onClick(item)} ) {
                Text(text = item)
            }
        }
    }
}