package com.colibrez.xkcdreader.android

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.colibrez.xkcdreader.android.repository.ComicsPagingSource
import com.colibrez.xkcdreader.model.Comic
import kotlinx.coroutines.flow.Flow

class MainViewModel(comicsPagingSource: ComicsPagingSource) : ViewModel() {


    val pagedComics: Flow<PagingData<Comic>> = Pager(
        // Configure how data is loaded by passing additional properties to
        // PagingConfig, such as prefetchDistance.
        PagingConfig(pageSize = 20)
    ) {
        comicsPagingSource
    }.flow
        .cachedIn(viewModelScope)



    class Factory(private val comicsPagingSource: ComicsPagingSource) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MainViewModel(comicsPagingSource) as T
        }
    }
}