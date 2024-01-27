package com.colibrez.xkcdreader.android.ui.features.comiclist

import com.colibrez.xkcdreader.android.ui.core.mvvm.StateHolder
import com.colibrez.xkcdreader.android.ui.core.mvvm.UiState
import com.colibrez.xkcdreader.android.ui.core.mvvm.UserAction
import com.colibrez.xkcdreader.android.ui.features.comic.ComicArguments
import com.colibrez.xkcdreader.android.ui.features.comic.ComicComponent
import com.colibrez.xkcdreader.android.ui.features.comic.ComicDelegate
import com.colibrez.xkcdreader.android.ui.features.comiclist.filters.FiltersComponent
import com.colibrez.xkcdreader.android.ui.features.comiclist.filters.FiltersDelegate
import com.colibrez.xkcdreader.android.ui.features.comiclist.search.SearchComponent
import com.colibrez.xkcdreader.android.ui.features.comiclist.search.SearchDelegate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class AllComicsState(
    val listScreen: ComicListComponent,
    val comicScreen: ComicComponent?,
    val filtersComponent: FiltersComponent,
    val searchComponent: SearchComponent,
) : UiState

sealed interface AllComicsAction : UserAction {
    data class ShowComic(val arguments: ComicArguments) : AllComicsAction
    data object HideComic : AllComicsAction
}

class AllComicsStateHolder : StateHolder<AllComicsState, AllComicsAction> {

    private val comicListProps = MutableStateFlow(
        ComicListProps(
            isUnreadFilterApplied = false,
            isFavoriteFilterApplied = false,
            searchQuery = "",
        ),
    )

    private val _state: MutableStateFlow<AllComicsState> = MutableStateFlow(
        AllComicsState(
            listScreen = ComicListComponent(
                props = comicListProps,
                delegate = object : ComicListDelegate {
                    override fun showComic(comicArguments: ComicArguments) {
                        handle(AllComicsAction.ShowComic(comicArguments))
                    }
                },
            ),
            comicScreen = null,
            filtersComponent = FiltersComponent(
                delegate = object : FiltersDelegate {
                    override fun onUnreadFilterChanged(newValue: Boolean) {
                        comicListProps.update { it.copy(isUnreadFilterApplied = newValue) }
                    }

                    override fun onFavoriteFilterChanges(newValue: Boolean) {
                        comicListProps.update { it.copy(isFavoriteFilterApplied = newValue) }
                    }
                },
            ),
            searchComponent = SearchComponent(
                delegate = object : SearchDelegate {
                    override fun onSearchQueryUpdated(newQuery: String) {
                        comicListProps.update { it.copy(searchQuery = newQuery) }
                    }
                },
            ),
        ),
    )

    override val state: StateFlow<AllComicsState>
        get() = _state.asStateFlow()

    override fun handle(action: AllComicsAction) {
        when (action) {
            is AllComicsAction.ShowComic -> {
                _state.update {
                    it.copy(
                        comicScreen = ComicComponent(
                            arguments = action.arguments,
                            delegate = object : ComicDelegate {
                                override fun popScreen() {
                                    handle(AllComicsAction.HideComic)
                                }
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
