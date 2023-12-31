package com.colibrez.xkcdreader.android

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.savedstate.SavedStateRegistryOwner
import app.cash.sqldelight.paging3.QueryPagingSource
import com.colibrez.xkcdreader.android.repository.ComicsRemoteMediator
import com.colibrez.xkcdreader.model.Comic
import com.colibrez.xkcdreader.data.repository.ComicRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

class MainViewModel(comicsRemoteMediator: ComicsRemoteMediator, comicRepository: ComicRepository) :
    ViewModel() {


    @OptIn(ExperimentalPagingApi::class)
    val pagedComics: Flow<PagingData<Comic>> = Pager(
        config = PagingConfig(pageSize = 20),
        remoteMediator = comicsRemoteMediator
    ) {
        QueryPagingSource(
            countQuery = comicRepository.comicQueries.count(),
            transacter = comicRepository.comicQueries,
            context = Dispatchers.IO,
            queryProvider = { limit, offset ->
                comicRepository.comicQueries.selectPaged(
                    limit,
                    offset,
                    ComicRepository::mapComicSelecting
                )
            }
        ).also {
            comicsRemoteMediator.invalidate = {
                it.invalidate()
            }
        }
    }.flow


    class Factory(
        owner: SavedStateRegistryOwner,
        private val comicsRemoteMediator: ComicsRemoteMediator,
        private val comicRepository: ComicRepository
    ) : AbstractSavedStateViewModelFactory(owner, null) {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(
            key: String,
            modelClass: Class<T>,
            handle: SavedStateHandle
        ): T {
            return MainViewModel(comicsRemoteMediator, comicRepository) as T
        }
    }
}