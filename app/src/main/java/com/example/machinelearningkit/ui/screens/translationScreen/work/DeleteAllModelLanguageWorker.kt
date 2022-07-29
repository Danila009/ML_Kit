package com.example.machinelearningkit.ui.screens.translationScreen.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import com.google.mlkit.common.model.RemoteModelManager
import com.google.mlkit.nl.translate.TranslateRemoteModel

class DeleteAllModelLanguageWorker(
    context: Context,
    workerParameters: WorkerParameters
):CoroutineWorker(context, workerParameters) {

    companion object {
        const val ON_ERROR_KEY = "on_error_delete_model_language_worker_key"
    }

    override suspend fun doWork(): Result {
        return try {

            val data = Data.Builder()

            deleteAllModel {
                data.putString(ON_ERROR_KEY, it)

                Result.failure(data.build())
            }

            Result.success()
        }catch (e:Exception){ Result.failure() }
    }

    private fun deleteAllModel(
        onError:(String) -> Unit
    ){
        val modelManager = RemoteModelManager.getInstance()

        modelManager.getDownloadedModels(TranslateRemoteModel::class.java)
            .addOnSuccessListener { models ->
                models.forEach { model ->
                    modelManager.deleteDownloadedModel(model)
                        .addOnFailureListener { onError(it.message ?: "Error") }
                }
            }
            .addOnFailureListener { onError(it.message ?: "Error") }
    }
}