package com.colibrez.xkcdreader.android.ui.features.comiclist.all

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.savedstate.SavedStateRegistryOwner
import com.colibrez.xkcdreader.android.data.repository.paging.AllComicsPagingDataSource
import com.colibrez.xkcdreader.android.ui.components.comic.ListComic
import com.colibrez.xkcdreader.android.ui.components.paging.PagingState
import com.colibrez.xkcdreader.android.ui.components.paging.PagingStateHolder
import com.colibrez.xkcdreader.android.ui.core.mvvm.BaseViewModel
import com.colibrez.xkcdreader.android.ui.core.mvvm.BaseViewModelFactory
import com.colibrez.xkcdreader.android.ui.features.comiclist.ComicListUserAction
import com.colibrez.xkcdreader.data.repository.ComicRepository

class AllComicsViewModel(
    allComicsPagingDataSource: AllComicsPagingDataSource,
    comicRepository: ComicRepository
) : BaseViewModel<PagingState<ListComic>, ComicListUserAction>() {

    val pagingStateHolder = PagingStateHolder(
        pageSize = 20,
        viewModelScope = viewModelScope,
        pagingDataSource = allComicsPagingDataSource,
        itemTransform = { ListComic.fromExternalModel(it) },
    )

    override val stateHolder: AllComicsStateHolder = AllComicsStateHolder(
        viewModelScope = viewModelScope,
        pagingStateHolder = pagingStateHolder,
        comicRepository = comicRepository,
    )

    class Factory(
        owner: SavedStateRegistryOwner,
        private val allComicsPagingDataSource: AllComicsPagingDataSource,
        private val comicRepository: ComicRepository,
    ) : BaseViewModelFactory<AllComicsViewModel>(owner) {

        override fun create(
            key: String,
            handle: SavedStateHandle
        ): AllComicsViewModel {
            return AllComicsViewModel(
                allComicsPagingDataSource,
                comicRepository,
            )
        }
    }
}
