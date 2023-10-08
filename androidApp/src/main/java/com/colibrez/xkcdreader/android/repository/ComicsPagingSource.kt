package com.colibrez.xkcdreader.android.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.colibrez.xkcdreader.model.Comic
import com.colibrez.xkcdreader.network.ApiClient

class ComicsPagingSource(private val apiClient: ApiClient) : PagingSource<Long, Comic>() {
    override fun getRefreshKey(state: PagingState<Long, Comic>): Long? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Long>): LoadResult<Long, Comic> {
        // Start refresh at page 1 if undefined.
        val nextPageNumber: Long = params.key ?: 1
        val response = apiClient.getPaginatedComics(next = nextPageNumber, limit = params.loadSize.toLong())
        return response.fold(
            onSuccess = {
                LoadResult.Page(
                    data = it,
                    prevKey = null, // Only paging forward.
                    nextKey = it.lastOrNull()?.num?.plus(1)
                )
            },
            onFailure = {
                LoadResult.Error(it)
            }
        )
    }

}