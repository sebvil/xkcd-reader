package com.colibrez.xkcdreader.android.ui.features.comiclist.all

import com.colibrez.xkcdreader.android.ui.features.comiclist.ComicListStateHolder
import com.colibrez.xkcdreader.android.ui.features.comiclist.all.filters.FilterStateHolder
import com.colibrez.xkcdreader.data.repository.ComicRepository
import com.colibrez.xkcdreader.model.Comic
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest

class AllComicsStateHolder(
    viewModelScope: CoroutineScope,
    filterStateHolder: FilterStateHolder,
    comicRepository: ComicRepository,
) : ComicListStateHolder(viewModelScope, comicRepository) {

    @OptIn(ExperimentalCoroutinesApi::class)
    override val comicsFlow: Flow<List<Comic>> =
        filterStateHolder.state.flatMapLatest { filterState ->
            comicRepository.getAllComics(isRead = filterState.isReadFilter)
        }

}
