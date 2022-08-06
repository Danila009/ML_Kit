package com.example.machinelearningkit.data.userDatabase.source

import android.content.Context
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.machinelearningkit.data.userDatabase.model.StorageImage
import com.example.machinelearningkit.data.userDatabase.useCase.GetStorageImagesUseCase

class StorageImagesPageSource(
    private val context: Context
):PagingSource<Int, StorageImage>() {

    private val getStorageImagesUseCase = GetStorageImagesUseCase()

    override fun getRefreshKey(state: PagingState<Int, StorageImage>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, StorageImage> {
        return try {

            val page = params.key ?: 1

            val result = getStorageImagesUseCase.invoke(
                context = context,
                page = page
            )

            LoadResult.Page(
                data = result,
                prevKey = if (page == 1) null else page -1,
                nextKey = page.plus(1)
            )

        }catch (e:Exception){
            LoadResult.Error(e)
        }
    }
}