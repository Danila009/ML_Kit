package com.example.machinelearningkit.ui.screens.translationScreen.viewModel

import androidx.lifecycle.ViewModel
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

internal class TranslationViewModel:ViewModel() {

    fun translate(
        translateText:String,
        startTranslateLanguage:String = TranslateLanguage.ENGLISH,
        finishedTranslateLanguage:String = TranslateLanguage.RUSSIAN
    ):Flow<String> = callbackFlow {
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(startTranslateLanguage)
            .setTargetLanguage(finishedTranslateLanguage)
            .build()

        val englishGermanTranslator = Translation.getClient(options)

        val conditions = DownloadConditions.Builder()
            .requireWifi()
            .build()

        englishGermanTranslator.downloadModelIfNeeded(conditions)
            .addOnSuccessListener {
                englishGermanTranslator.translate(translateText)
                    .addOnSuccessListener { translatedText ->
                        trySend(translatedText)
                    }
                    .addOnFailureListener { trySend(it.message ?: "Error") }
            }
            .addOnFailureListener { trySend(it.message ?: "Error") }

        awaitClose {
            englishGermanTranslator.close()
        }
    }
}