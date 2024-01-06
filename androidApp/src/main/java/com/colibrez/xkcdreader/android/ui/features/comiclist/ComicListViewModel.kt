package com.colibrez.xkcdreader.android.ui.features.comiclist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.savedstate.SavedStateRegistryOwner
import com.colibrez.xkcdreader.android.data.repository.paging.AllComicsPagingDataSource
import com.colibrez.xkcdreader.android.ui.components.paging.PagingState
import com.colibrez.xkcdreader.android.ui.components.paging.PagingStateHolder
import com.colibrez.xkcdreader.android.ui.core.mvvm.BaseViewModel
import com.colibrez.xkcdreader.android.ui.core.mvvm.BaseViewModelFactory
import com.colibrez.xkcdreader.data.repository.ComicRepository


class ComicListViewModel(
    allComicsPagingDataSource: AllComicsPagingDataSource,
    comicRepository: ComicRepository
) : BaseViewModel<PagingState<ListComic>, ComicListUserAction>() {


    val pagingStateHolder = PagingStateHolder(
        pageSize = 20,
        viewModelScope = viewModelScope,
        pagingDataSource = allComicsPagingDataSource,
        itemTransform = {
            ListComic(
                comicNumber = it.number,
                title = it.title,
                imageUrl = it.imageUrl,
                isFavorite = it.isFavorite,
                isRead = it.isRead
            )
        }
    )

    override val stateHolder: ComicListStateHolder = ComicListStateHolder(
        viewModelScope = viewModelScope,
        pagingStateHolder = pagingStateHolder,
        comicRepository = comicRepository
    )


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