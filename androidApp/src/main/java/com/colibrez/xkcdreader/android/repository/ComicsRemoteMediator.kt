package com.colibrez.xkcdreader.android.repository

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.colibrez.xkcdreader.model.Comic
import com.colibrez.xkcdreader.network.ApiClient
import com.colibrez.xkcdreader.repository.ComicRepository
import kotlinx.coroutines.flow.first
import okio.IOException

@OptIn(ExperimentalPagingApi::class)
class ComicsRemoteMediator(
    private val comicRepository: ComicRepository,
    private val apiClient: ApiClient
) : RemoteMediator<Int, Comic>() {


    var invalidate: (() -> Unit)? = null

    override suspend fun load(loadType: LoadType, state: PagingState<Int, Comic>): MediatorResult {
        return try {

            // The network load method takes an optional after=<user.id>
            // parameter. For every page after the first, pass the last user
            // ID to let it continue from where it left off. For REFRESH,
            // pass null to load the first page.
            val loadKey = when (loadType) {
                LoadType.REFRESH -> 0
                // In this example, you never need to prepend, since REFRESH
                // will always load the first page in the list. Immediately
                // return, reporting end of pagination.
                LoadType.PREPEND ->
                    return MediatorResult.Success(endOfPaginationReached = true)

                LoadType.APPEND -> {
                    // You must explicitly check if the last item is null when
                    // appending, since passing null to networkService is only
                    // valid for initial load. If lastItem is null it means no
                    // items were loaded after the initial REFRESH and there are
                    // no more items to load.
                    val lastItem = state.lastItemOrNull()
                        ?: return MediatorResult.Success(
                            endOfPaginationReached = true
                        )

                    lastItem.num
                }
            }


            val response =
                apiClient.getPaginatedComics(next = loadKey, limit = state.config.pageSize.toLong())
            invalidate?.invoke()

            val comics = response.fold(
                onSuccess = {
                    it
                },
                onFailure = {
                    return MediatorResult.Error(it)
                }
            )
            comicRepository.insertComics(comics)

            return MediatorResult.Success(
                endOfPaginationReached = comics.isEmpty()
            )
        } catch (e: IOException) {
            MediatorResult.Error(e)
        }

    }

    override suspend fun initialize(): InitializeAction {
        val hasComics = comicRepository.getCount().first() > 0
        return  if (hasComics) {
            InitializeAction.SKIP_INITIAL_REFRESH
        } else {
            InitializeAction.LAUNCH_INITIAL_REFRESH
        }
    }

}