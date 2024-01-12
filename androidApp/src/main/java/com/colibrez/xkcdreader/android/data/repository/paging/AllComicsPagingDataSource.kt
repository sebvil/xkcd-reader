package com.colibrez.xkcdreader.android.data.repository.paging

import com.colibrez.xkcdreader.android.ui.components.paging.PagingState
import com.colibrez.xkcdreader.data.model.asEntity
import com.colibrez.xkcdreader.data.repository.ComicRepository
import com.colibrez.xkcdreader.extensions.withDefault
import com.colibrez.xkcdreader.model.Comic
import com.colibrez.xkcdreader.network.ApiClient
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update

class AllComicsPagingDataSource(
    private val apiClient: ApiClient,
    private val comicRepository: ComicRepository
) : PagingDataSource<Comic> {

    private val _items = comicRepository.getAllComics().withDefault(listOf())
    private val _pagingStatus = MutableStateFlow<PagingStatus>(PagingStatus.Idle(endOfPaginationReached = false))

    override val state = combine(
        _items,
        _pagingStatus,
    ) { items, pagingStatus ->
        PagingState(items = items.toImmutableList(), status = pagingStatus)
    }.withDefault(PagingState(items = persistentListOf(), status = PagingStatus.Loading))

    override suspend fun fetch(pageSize: Long, isInitialFetch: Boolean) {
        val key = comicRepository.getComicCount().first()
        if (isInitialFetch && key != 0L) {
            return
        }
        _pagingStatus.update { PagingStatus.Loading }
        val response = apiClient.getPaginatedComics(key, pageSize)

        val result = response.fold(
            onSuccess = {
                it
            },
            onFailure = {
                _pagingStatus.update {
                    PagingStatus.NetworkError("There was an issue fetching comics.")
                }
                return
            },
        )
        comicRepository.insertComics(result.map { it.asEntity() })
        _pagingStatus.update {
            PagingStatus.Idle(endOfPaginationReached = result.isEmpty())
        }
    }
}
