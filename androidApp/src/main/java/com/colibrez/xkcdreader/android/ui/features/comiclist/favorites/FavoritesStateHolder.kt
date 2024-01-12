package com.colibrez.xkcdreader.android.ui.features.comiclist.favorites

import com.colibrez.xkcdreader.android.ui.components.comic.ListComic
import com.colibrez.xkcdreader.android.ui.core.mvvm.StateHolder
import com.colibrez.xkcdreader.android.ui.features.comiclist.ComicListStateHolder
import com.colibrez.xkcdreader.data.repository.ComicRepository
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class FavoritesStateHolder(
    viewModelScope: CoroutineScope,
    comicRepository: ComicRepository,
) : ComicListStateHolder<FavoritesState>(viewModelScope, comicRepository) {

    override val state: StateFlow<FavoritesState> = comicRepository.getFavorites().map { comics ->
        FavoritesState.Data(
            comics = comics.map { ListComic.fromExternalModel(it) }
                .toImmutableList(),
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(StateHolder.DEFAULT_SUBSCRIPTION_TIME),
        FavoritesState.Loading,
    )
}
