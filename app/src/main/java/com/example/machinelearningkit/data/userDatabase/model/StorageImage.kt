package com.example.machinelearningkit.data.userDatabase.model

import android.net.Uri

data class StorageImage(
    val id:String,
    val title:String,
    val size:String,
    val folder:String,
    val patch:String,
    val date:String,
    val uri: Uri
)