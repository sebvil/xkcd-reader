package com.colibrez.xkcdreader.android.ui.features.comiclist.all

import com.colibrez.xkcdreader.android.ui.features.comiclist.ComicListStateHolder
import com.colibrez.xkcdreader.data.repository.ComicRepository
import com.colibrez.xkcdreader.model.Comic
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

class AllComicsStateHolder(
    viewModelScope: CoroutineScope,
    comicRepository: ComicRepository,
) : ComicListStateHolder(viewModelScope, comicRepository) {
    override val comicsFlow: Flow<List<Comic>> = comicRepository.getAllComics()
}
