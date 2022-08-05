package com.example.machinelearningkit.ui.screens.barcodeGeneratorScreen.generateBarCode

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter

fun generateBarCode(
    text:String,
    format: BarcodeFormat = BarcodeFormat.QR_CODE
): Bitmap? {
    return try {

        val width = 500
        val height = 150
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val codeWriter = MultiFormatWriter()

        val bitMatrix = codeWriter.encode(
            text,
            format,
            width,
            height
        )

        for (x in 0 until width) {
            for (y in 0 until height) {
                val color = if (bitMatrix[x, y]) Color.BLACK else Color.WHITE
                bitmap.setPixel(x, y, color)
            }
        }

        bitmap
    }catch (e:Exception){ null }
}