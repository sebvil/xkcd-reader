package com.colibrez.xkcdreader.android.ui.features.comiclist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.savedstate.SavedStateRegistryOwner
import com.colibrez.xkcdreader.android.ui.core.mvvm.BaseViewModel
import com.colibrez.xkcdreader.android.ui.core.mvvm.BaseViewModelFactory
import com.colibrez.xkcdreader.android.ui.features.comiclist.filters.FilterStateHolder
import com.colibrez.xkcdreader.android.ui.features.comiclist.search.SearchStateHolder
import com.colibrez.xkcdreader.data.repository.ComicRepository
import com.colibrez.xkcdreader.data.repository.SearchRepository

class ComicListViewModel(
    comicRepository: ComicRepository,
    searchRepository: SearchRepository,
) : BaseViewModel<ComicListState, ComicListUserAction>() {

    val filterStateHolder: FilterStateHolder = FilterStateHolder()

    val searchStateHolder = SearchStateHolder(viewModelScope, searchRepository)

    override val stateHolder: ComicListStateHolder = ComicListStateHolder(
        viewModelScope = viewModelScope,
        filterStateHolder = filterStateHolder,
        comicRepository = comicRepository,
    )

    class Factory(
        owner: SavedStateRegistryOwner,
        private val comicRepository: ComicRepository,
        private val searchRepository: SearchRepository,
    ) : BaseViewModelFactory<ComicListViewModel>(owner) {

        override fun create(
            key: String,
            handle: SavedStateHandle
        ): ComicListViewModel {
            return ComicListViewModel(
                comicRepository,
                searchRepository,
            )
        }
    }
}
