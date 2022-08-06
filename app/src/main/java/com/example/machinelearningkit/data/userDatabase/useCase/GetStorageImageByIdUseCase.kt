package com.example.machinelearningkit.data.userDatabase.useCase

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import com.example.machinelearningkit.data.userDatabase.model.StorageImage
import java.io.File

class GetStorageImageByIdUseCase {

    @SuppressLint("Recycle", "Range")
    operator fun invoke(id:String, context:Context):StorageImage? {
        return try {

            val projection = arrayOf(
                MediaStore.Images.Media.TITLE,
                MediaStore.Images.Media.SIZE,
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DATE_ADDED
            )

            val cursor = context.contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                MediaStore.Images.Media._ID +" = "+ id,
                null,
                null
            )

            cursor?.let {

                cursor.moveToNext()

                val title = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.TITLE))
                val folder = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME))
                val size = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.SIZE))
                val patch = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
                val date = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED))

                val file = File(patch)
                val uri = Uri.fromFile(file)

                return StorageImage(
                    id = id,
                    title = title,
                    size = size,
                    folder = folder,
                    patch = patch,
                    date = date,
                    uri = uri
                )

            }

            cursor?.close()

            null
        }catch (e:Exception){ null }
    }
}