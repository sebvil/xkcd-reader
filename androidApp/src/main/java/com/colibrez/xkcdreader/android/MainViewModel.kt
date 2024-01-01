package com.colibrez.xkcdreader.android

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.RemoteMediator
import androidx.savedstate.SavedStateRegistryOwner
import com.colibrez.xkcdreader.data.repository.ComicRepository
import com.colibrez.xkcdreader.model.Comic
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch


sealed interface MainUserAction {
    data class ToggleFavorite(val comicNum: Long, val isFavorite: Boolean) : MainUserAction
}

@OptIn(ExperimentalPagingApi::class)
class MainViewModel(
    comicsRemoteMediator: RemoteMediator<Int, Comic>,
    pagingSourceFactory: () -> PagingSource<Int, Comic>,
    private val comicRepository: ComicRepository
) : ViewModel() {


    @OptIn(ExperimentalPagingApi::class)
    val pagedComics: Flow<PagingData<Comic>> = Pager(
        config = PagingConfig(pageSize = 20),
        remoteMediator = comicsRemoteMediator,
        pagingSourceFactory = pagingSourceFactory
    ).flow


    fun handle(action: MainUserAction) {
        when (action) {
            is MainUserAction.ToggleFavorite -> {
                viewModelScope.launch {
                    comicRepository.toggleFavorite(action.comicNum, action.isFavorite)
                }
            }
        }
    }


    class Factory(
        owner: SavedStateRegistryOwner,
        private val comicsRemoteMediator: RemoteMediator<Int, Comic>,
        private val pagingSourceFactory: () -> PagingSource<Int, Comic>,
        private val comicRepository: ComicRepository,
    ) : AbstractSavedStateViewModelFactory(owner, null) {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(
            key: String,
            modelClass: Class<T>,
            handle: SavedStateHandle
        ): T {
            return MainViewModel(comicsRemoteMediator, pagingSourceFactory, comicRepository) as T
        }
    }
}