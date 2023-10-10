package com.colibrez.xkcdreader.android.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.colibrez.xkcdreader.model.Comic
import com.colibrez.xkcdreader.repository.ComicRepository
import kotlinx.coroutines.flow.first
import okio.IOException

class ComicsPagingSource(private val comicRepository: ComicRepository) :
    PagingSource<Int, Comic>() {
    override fun getRefreshKey(state: PagingState<Int, Comic>): Int? {
        return when (val anchorPosition = state.anchorPosition) {
            null -> null
            /**
             *  It is unknown whether anchorPosition represents the item at the top of the screen or item at
             *  the bottom of the screen. To ensure the number of items loaded is enough to fill up the
             *  screen, half of loadSize is loaded before the anchorPosition and the other half is
             *  loaded after the anchorPosition -- anchorPosition becomes the middle item.
             */
            else -> maxOf(0, anchorPosition - (state.config.initialLoadSize / 2))
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Comic> {
        return try {
            // Start refresh at page 1 if undefined.
            val nextPageNumber = params.key ?: 1
            val results =
                comicRepository.getComicsPaged(
                    next = nextPageNumber.toLong(),
                    limit = params.loadSize.toLong()
                ).first()
            return if (invalid) LoadResult.Invalid() else LoadResult.Page(results, prevKey = null, results.lastOrNull()?.num?.plus(1)?.toInt())
        } catch (e: IOException) {
            LoadResult.Error(e)
        }
    }

}