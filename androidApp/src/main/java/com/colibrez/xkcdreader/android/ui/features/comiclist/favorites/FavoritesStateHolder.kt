package com.colibrez.xkcdreader.android.ui.features.comiclist.favorites

import com.colibrez.xkcdreader.android.ui.features.comiclist.ComicListStateHolder
import com.colibrez.xkcdreader.data.repository.ComicRepository
import com.colibrez.xkcdreader.model.Comic
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

class FavoritesStateHolder(
    viewModelScope: CoroutineScope,
    comicRepository: ComicRepository,
) : ComicListStateHolder(viewModelScope, comicRepository) {
    override val comicsFlow: Flow<List<Comic>> = comicRepository.getFavorites()
}
