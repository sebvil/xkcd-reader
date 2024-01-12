package com.colibrez.xkcdreader.android.ui.features.favorites

import com.colibrez.xkcdreader.android.ui.components.comic.ListComic
import com.colibrez.xkcdreader.android.ui.core.mvvm.BaseStateHolder
import com.colibrez.xkcdreader.android.ui.core.mvvm.StateHolder
import com.colibrez.xkcdreader.android.ui.core.navigation.NavigationState
import com.colibrez.xkcdreader.android.ui.features.comic.ComicScreenArguments
import com.colibrez.xkcdreader.data.repository.ComicRepository
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FavoritesStateHolder(
    private val viewModelScope: CoroutineScope,
    private val comicRepository: ComicRepository,
) : BaseStateHolder<FavoritesState, FavoritesUserAction>() {

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

    override fun handle(action: FavoritesUserAction) {
        when (action) {
            is FavoritesUserAction.ToggleFavorite -> {
                viewModelScope.launch {
                    comicRepository.toggleFavorite(action.comicNum, action.isFavorite)
                }
            }

            is FavoritesUserAction.ComicClicked -> {
                setNavigationState(
                    NavigationState.ShowScreen(
                        ComicScreenArguments(
                            comicNumber = action.comicNum,
                            comicTitle = action.comicTitle,
                        ),
                    ),
                )
            }
        }
    }
}
