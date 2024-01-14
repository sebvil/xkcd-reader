package com.colibrez.xkcdreader.android.ui.features.comiclist.all

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.savedstate.SavedStateRegistryOwner
import com.colibrez.xkcdreader.android.ui.core.mvvm.BaseViewModel
import com.colibrez.xkcdreader.android.ui.core.mvvm.BaseViewModelFactory
import com.colibrez.xkcdreader.android.ui.features.comiclist.ComicListState
import com.colibrez.xkcdreader.android.ui.features.comiclist.ComicListUserAction
import com.colibrez.xkcdreader.android.ui.features.comiclist.all.filters.FilterStateHolder
import com.colibrez.xkcdreader.data.repository.ComicRepository

class AllComicsViewModel(
    comicRepository: ComicRepository
) : BaseViewModel<ComicListState, ComicListUserAction>() {

    val filterStateHolder: FilterStateHolder = FilterStateHolder()
    override val stateHolder: AllComicsStateHolder = AllComicsStateHolder(
        viewModelScope = viewModelScope,
        filterStateHolder = filterStateHolder,
        comicRepository = comicRepository,
    )

    class Factory(
        owner: SavedStateRegistryOwner,
        private val comicRepository: ComicRepository,
    ) : BaseViewModelFactory<AllComicsViewModel>(owner) {

        override fun create(
            key: String,
            handle: SavedStateHandle
        ): AllComicsViewModel {
            return AllComicsViewModel(
                comicRepository,
            )
        }
    }
}
