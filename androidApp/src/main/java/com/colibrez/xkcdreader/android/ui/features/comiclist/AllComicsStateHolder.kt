package com.colibrez.xkcdreader.android.ui.features.comiclist

import com.colibrez.xkcdreader.android.ui.core.mvvm.StateHolder
import com.colibrez.xkcdreader.android.ui.core.mvvm.UiState
import com.colibrez.xkcdreader.android.ui.core.mvvm.UserAction
import com.colibrez.xkcdreader.android.ui.features.comic.ComicScreen
import com.colibrez.xkcdreader.android.ui.features.comic.ComicScreenArguments
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class AllComicsState(
    val listScreen: ComicListScreen,
    val comicScreen: ComicScreen?
) : UiState

sealed interface AllComicsAction : UserAction {
    data class ShowComic(val arguments: ComicScreenArguments) : AllComicsAction
    data object HideComic : AllComicsAction
}

class AllComicsStateHolder : StateHolder<AllComicsState, AllComicsAction> {

    private val _state: MutableStateFlow<AllComicsState> = MutableStateFlow(
        AllComicsState(
            listScreen = ComicListScreen(showComic = {
                handle(AllComicsAction.ShowComic(it))
            }),
            comicScreen = null,
        ),
    )

    override val state: StateFlow<AllComicsState>
        get() = _state.asStateFlow()

    override fun handle(action: AllComicsAction) {
        when (action) {
            is AllComicsAction.ShowComic -> {
                _state.update {
                    it.copy(
                        comicScreen = ComicScreen(
                            arguments = action.arguments,
                            popScreen = {
                                handle(AllComicsAction.HideComic)
                            },
                        ),
                    )
                }
            }

            is AllComicsAction.HideComic -> {
                _state.update {
                    it.comicScreen?.onClear()
                    it.copy(comicScreen = null)
                }
            }
        }
    }
}
