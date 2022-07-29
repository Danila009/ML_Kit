package com.example.machinelearningkit.ui.screens.translationScreen.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.common.model.RemoteModelManager
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.TranslateRemoteModel

class DownloadAllModelLanguageWorker(
    context: Context,
    workerParameter: WorkerParameters
): CoroutineWorker(context, workerParameter) {

    companion object {
        const val ON_ERROR_KEY = "on_error_downloaded_model_language_worker_key"
    }

    override suspend fun doWork(): Result {
        return try {

            val builder = Data.Builder()

            downloadAllTranslateLanguage {
                builder.putString(ON_ERROR_KEY, it)
                Result.failure(builder.build())
            }

            Result.success()
        }catch (e:Exception){
            Result.failure()
        }
    }

    private fun downloadAllTranslateLanguage(
        onError:(String) -> Unit
    ){
        val modelManager = RemoteModelManager.getInstance()

        val conditions = DownloadConditions.Builder()
            .requireWifi()
            .build()


        modelManager.getDownloadedModels(TranslateRemoteModel::class.java)
            .addOnSuccessListener { models ->
                TranslateLanguage.getAllLanguages().forEach { item ->
                    if (!models.any { it.language == item }){
                        val model = TranslateRemoteModel.Builder(item).build()
                        modelManager.download(model, conditions)
                            .addOnFailureListener { onError(it.message ?: "Error") }
                    }
                }

            }
            .addOnFailureListener { onError(it.message ?: "Error") }
    }
}