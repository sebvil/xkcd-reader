package com.colibrez.xkcdreader.android.ui.features.latest

import com.colibrez.xkcdreader.android.ui.core.mvvm.BaseStateHolder
import com.colibrez.xkcdreader.data.repository.ComicRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LatestComicStateHolder(
    private val viewModelScope: CoroutineScope,
    private val comicRepository: ComicRepository,
) : BaseStateHolder<LatestComicState, LatestComicUserAction>() {

    private val showDialog: MutableStateFlow<Boolean> = MutableStateFlow(false)
    override val state =
        combine(comicRepository.getLatest(), showDialog) { comic, showDialog ->
            LatestComicState.Data(
                comicNumber = comic.number,
                comicTitle = comic.title,
                imageUrl = comic.imageUrl,
                altText = comic.altText,
                imageDescription = comic.transcript,
                permalink = comic.permalink,
                isFavorite = comic.isFavorite,
                showDialog = showDialog,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            initialValue = LatestComicState.Loading,
        )

    init {
        viewModelScope.launch {
            val comic = state.filterIsInstance<LatestComicState.Data>().first()
            comicRepository.markAsSeen(comicNum = comic.comicNumber)
        }
    }

    override fun handle(action: LatestComicUserAction) {
        when (action) {
            is LatestComicUserAction.ToggleFavorite -> {
                viewModelScope.launch {
                    comicRepository.toggleFavorite(action.comicNum, action.isFavorite)
                }
            }

            is LatestComicUserAction.ImageClicked -> {
                showDialog.update { true }
            }

            is LatestComicUserAction.OverlayClicked -> {
                showDialog.update { false }
            }
        }
    }
}
