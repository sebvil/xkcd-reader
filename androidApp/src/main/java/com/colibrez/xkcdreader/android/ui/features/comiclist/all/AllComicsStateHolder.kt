package com.colibrez.xkcdreader.android.ui.features.comiclist.all

import com.colibrez.xkcdreader.android.ui.components.comic.ListComic
import com.colibrez.xkcdreader.android.ui.components.paging.PagingState
import com.colibrez.xkcdreader.android.ui.components.paging.PagingStateHolder
import com.colibrez.xkcdreader.android.ui.features.comiclist.ComicListStateHolder
import com.colibrez.xkcdreader.data.repository.ComicRepository
import com.colibrez.xkcdreader.model.Comic
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow

class AllComicsStateHolder(
    viewModelScope: CoroutineScope,
    comicRepository: ComicRepository,
    val pagingStateHolder: PagingStateHolder<ListComic, Comic>,
) : ComicListStateHolder<PagingState<ListComic>>(viewModelScope, comicRepository) {

    override val state: StateFlow<PagingState<ListComic>> = pagingStateHolder.state
}
