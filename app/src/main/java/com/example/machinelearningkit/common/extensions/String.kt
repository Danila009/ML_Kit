package com.example.machinelearningkit.common.extensions

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import kotlin.String

fun String.copyText(context: Context){
    val sdk = Build.VERSION.SDK_INT
    if (sdk < Build.VERSION_CODES.HONEYCOMB) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.text = this
    } else {
        val clip = ClipData.newPlainText("TAG", this)
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(clip)
    }
}

@SuppressLint("ComposableNaming")
@Composable
fun String.copyText(){
    val context = LocalContext.current

    val sdk = Build.VERSION.SDK_INT
    if (sdk < Build.VERSION_CODES.HONEYCOMB) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.text = this
    } else {
        val clip = ClipData.newPlainText("TAG", this)
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(clip)
    }
}