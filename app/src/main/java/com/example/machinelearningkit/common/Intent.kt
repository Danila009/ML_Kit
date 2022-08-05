package com.example.machinelearningkit.common

import android.content.Intent
import android.net.Uri
import kotlin.String

fun intentEmail(
    address: String?,
    subject: String?,
    body: String?
):Intent = Intent(Intent.ACTION_SENDTO)
        .apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, address)
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
        }

fun intentUrl(url: String?):Intent = Intent(Intent.ACTION_VIEW).setData(Uri.parse(url))

fun intentPhone(phone: String?):Intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$phone"))