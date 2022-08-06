package com.example.machinelearningkit.data.userDatabase.useCase

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import com.example.machinelearningkit.data.userDatabase.model.StorageImage
import java.io.File

class GetStorageImagesUseCase {

    @SuppressLint("Range")
    operator fun invoke(
        context: Context,
        page:Int = 1,
        pageSize:Int = 10
    ): ArrayList<StorageImage> {
        val images = ArrayList<StorageImage>()

        val projection = arrayOf(
            MediaStore.Images.Media.TITLE,
            MediaStore.Images.Media.SIZE,
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.DATE_ADDED
        )

        val cursor = context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection,
            null,
            null,
            MediaStore.Images.Media.DATE_ADDED + " DESC"
        )

        try {

            if (cursor != null){
                //pageSize = 10
                //page = 2
                // (2-1) * 10 = 10
                // 2 * 10 = 20
                // 10 until 20
                ((page - 1) * pageSize until (page * pageSize)).forEach {

                    cursor.moveToPosition(it)

                    val title = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.TITLE))
                    val id = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media._ID))
                    val folder = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME))
                    val size = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.SIZE))
                    val patch = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
                    val date = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED))

                    try {
                        val file = File(patch)
                        val uri = Uri.fromFile(file)

                        val image = StorageImage(
                            id = id,
                            title = title,
                            size = size,
                            folder = folder,
                            patch = patch,
                            date = date,
                            uri = uri
                        )

                        if (file.exists()) images.add(image)

                    }catch (e:Exception){ Log.e("getStorageImages",e.message.toString()) }

                }

                cursor.close()
            }

        }catch (e:Exception){ }

        return images
    }

}