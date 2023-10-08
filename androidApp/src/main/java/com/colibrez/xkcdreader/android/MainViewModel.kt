package com.colibrez.xkcdreader.android

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.cachedIn
import app.cash.sqldelight.paging3.QueryPagingSource
import com.colibrez.xkcdreader.android.repository.ComicsRemoteMediator
import com.colibrez.xkcdreader.data.ComicQueries
import com.colibrez.xkcdreader.model.Comic
import com.colibrez.xkcdreader.repository.ComicRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class MainViewModel(comicsRemoteMediator: ComicsRemoteMediator, comicQueries: ComicQueries) :
    ViewModel() {


    @OptIn(ExperimentalPagingApi::class)
    val pagedComics: Flow<PagingData<Comic>> = Pager(
        config = PagingConfig(pageSize = 20),
        remoteMediator = comicsRemoteMediator
    ) {
        QueryPagingSource(
            countQuery = comicQueries.count(),
            transacter = comicQueries,
            context = Dispatchers.IO,
            queryProvider = { limit, offset ->
                comicQueries.selectPaged(
                    limit,
                    offset,
                    ComicRepository::mapComicSelecting
                )
            }
        )
    }.flow.cachedIn(viewModelScope)


    class Factory(private val comicsRemoteMediator: ComicsRemoteMediator, private val comicQueries: ComicQueries) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MainViewModel(comicsRemoteMediator, comicQueries) as T
        }
    }
}