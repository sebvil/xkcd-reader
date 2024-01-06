package com.colibrez.xkcdreader.android.ui.features.comiclist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingSource
import androidx.paging.RemoteMediator
import androidx.savedstate.SavedStateRegistryOwner
import com.colibrez.xkcdreader.android.ui.core.mvvm.BaseViewModel
import com.colibrez.xkcdreader.android.ui.core.mvvm.BaseViewModelFactory
import com.colibrez.xkcdreader.data.repository.ComicRepository
import com.colibrez.xkcdreader.model.Comic


@OptIn(ExperimentalPagingApi::class)
class ComicListViewModel(
    comicsRemoteMediator: RemoteMediator<Int, Comic>,
    pagingSourceFactory: () -> PagingSource<Int, Comic>,
    comicRepository: ComicRepository
) : BaseViewModel<ComicListState, ComicListUserAction>() {

    override val stateHolder: ComicListStateHolder = ComicListStateHolder(
        viewModelScope = viewModelScope,
        comicsRemoteMediator = comicsRemoteMediator,
        pagingSourceFactory = pagingSourceFactory,
        comicRepository = comicRepository
    )

    class Factory(
        owner: SavedStateRegistryOwner,
        private val comicsRemoteMediator: RemoteMediator<Int, Comic>,
        private val pagingSourceFactory: () -> PagingSource<Int, Comic>,
        private val comicRepository: ComicRepository,
    ) : BaseViewModelFactory<ComicListViewModel>(owner) {

        override fun create(
            key: String,
            handle: SavedStateHandle
        ): ComicListViewModel {
            return ComicListViewModel(
                comicsRemoteMediator,
                pagingSourceFactory,
                comicRepository
            )
        }
    }
}