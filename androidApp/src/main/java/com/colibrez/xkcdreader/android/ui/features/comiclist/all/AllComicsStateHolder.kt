package com.colibrez.xkcdreader.android.ui.features.comiclist.all

import com.colibrez.xkcdreader.android.ui.core.mvvm.StateHolder
import com.colibrez.xkcdreader.android.ui.features.comiclist.ComicListStateHolder
import com.colibrez.xkcdreader.android.ui.features.comiclist.all.filters.FilterUserAction
import com.colibrez.xkcdreader.android.ui.features.comiclist.all.filters.FiltersState
import com.colibrez.xkcdreader.android.ui.features.comiclist.all.filters.ReadFilter
import com.colibrez.xkcdreader.data.repository.ComicRepository
import com.colibrez.xkcdreader.model.Comic
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest

class AllComicsStateHolder(
    viewModelScope: CoroutineScope,
    filterStateHolder: StateHolder<FiltersState, FilterUserAction>,
    comicRepository: ComicRepository,
) : ComicListStateHolder(viewModelScope, comicRepository) {

    @OptIn(ExperimentalCoroutinesApi::class)
    override val comicsFlow: Flow<List<Comic>> =
        filterStateHolder.state.flatMapLatest { filterState ->
            comicRepository.getAllComics(
                isRead = when (filterState.isReadFilter.selection) {
                    ReadFilter.All -> null
                    ReadFilter.Unread -> false
                    ReadFilter.Read -> true
                },
            )
        }
}
