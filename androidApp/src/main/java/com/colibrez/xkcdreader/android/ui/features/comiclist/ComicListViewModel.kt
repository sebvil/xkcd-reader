package com.colibrez.xkcdreader.android.ui.features.comiclist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingSource
import androidx.paging.RemoteMediator
import androidx.savedstate.SavedStateRegistryOwner
import com.colibrez.xkcdreader.android.data.repository.AllComicsPagingDataSource
import com.colibrez.xkcdreader.android.ui.components.paging.PagingState
import com.colibrez.xkcdreader.android.ui.core.mvvm.BaseViewModel
import com.colibrez.xkcdreader.android.ui.core.mvvm.BaseViewModelFactory
import com.colibrez.xkcdreader.data.repository.ComicRepository
import com.colibrez.xkcdreader.model.Comic


class ComicListViewModel(
    allComicsPagingDataSource: AllComicsPagingDataSource,
    comicRepository: ComicRepository
) : BaseViewModel<PagingState<ListComic>, ComicListUserAction>() {


    override val stateHolder: ComicListStateHolder = ComicListStateHolder(
        viewModelScope = viewModelScope,
        pagingDataSource = allComicsPagingDataSource,
        comicRepository = comicRepository
    )

    val pagingStateHolder
        get() = stateHolder.pagingStateHolder


    class Factory(
        owner: SavedStateRegistryOwner,
        private val allComicsPagingDataSource: AllComicsPagingDataSource,
        private val comicRepository: ComicRepository,
    ) : BaseViewModelFactory<ComicListViewModel>(owner) {

        override fun create(
            key: String,
            handle: SavedStateHandle
        ): ComicListViewModel {
            return ComicListViewModel(
                allComicsPagingDataSource,
                comicRepository
            )
        }
    }
}