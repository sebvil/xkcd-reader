package com.colibrez.xkcdreader.android.ui.features.comiclist

import com.colibrez.xkcdreader.android.ui.components.paging.PagingState
import com.colibrez.xkcdreader.android.ui.components.paging.PagingStateHolder
import com.colibrez.xkcdreader.android.ui.core.mvvm.BaseStateHolder
import com.colibrez.xkcdreader.android.ui.core.navigation.NavigationState
import com.colibrez.xkcdreader.android.ui.features.comic.ComicScreenArguments
import com.colibrez.xkcdreader.data.repository.ComicRepository
import com.colibrez.xkcdreader.model.Comic
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class ComicListStateHolder(
    private val viewModelScope: CoroutineScope,
    val pagingStateHolder: PagingStateHolder<ListComic, Comic>,
    private val comicRepository: ComicRepository,
) : BaseStateHolder<PagingState<ListComic>, ComicListUserAction>() {



    override val state: StateFlow<PagingState<ListComic>> = pagingStateHolder.state


    override fun handle(action: ComicListUserAction) {
        when (action) {
            is ComicListUserAction.ToggleFavorite -> {
                viewModelScope.launch {
                    comicRepository.toggleFavorite(action.comicNum, action.isFavorite)
                }
            }

            is ComicListUserAction.ComicClicked -> {
                setNavigationState(
                    NavigationState.ShowScreen(
                        ComicScreenArguments(
                            comicNumber = action.comicNum,
                            comicTitle = action.comicTitle
                        )
                    )
                )
            }
        }
    }
}