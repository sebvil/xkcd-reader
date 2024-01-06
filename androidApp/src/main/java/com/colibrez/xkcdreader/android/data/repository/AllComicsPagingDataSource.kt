package com.colibrez.xkcdreader.android.data.repository

import com.colibrez.xkcdreader.android.ui.components.paging.PagingState
import com.colibrez.xkcdreader.data.model.asEntity
import com.colibrez.xkcdreader.data.repository.ComicRepository
import com.colibrez.xkcdreader.extensions.withDefault
import com.colibrez.xkcdreader.model.Comic
import com.colibrez.xkcdreader.network.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class AllComicsPagingDataSource(
    private val apiClient: ApiClient,
    private val comicRepository: ComicRepository
) : PagingDataSource<Comic> {

    private val _items = comicRepository.getAllComics().withDefault(listOf())
    private val _endOfPaginationReached = MutableStateFlow(false)
    private val _isLoading = MutableStateFlow(false)

    override val state = combine(
        _items,
        _isLoading,
        _endOfPaginationReached
    ) { items, isLoading, endOfPaginationReached ->
        PagingState(items, isLoading, endOfPaginationReached)
    }.withDefault(PagingState(items = listOf(), isLoading = false, endOfPaginationReached = false))


    override suspend fun fetch(pageSize: Long) {
        val key = comicRepository.getComicCount().first()
        _isLoading.update { true }
        val response = apiClient.getPaginatedComics(key, pageSize)

        val result = response.fold(
            onSuccess = {
                it
            },
            onFailure = {
                throw it
            }
        )
        comicRepository.insertComics(result.map { it.asEntity() })
        _isLoading.update { false }
        _endOfPaginationReached.update { result.isEmpty() }


    }
}