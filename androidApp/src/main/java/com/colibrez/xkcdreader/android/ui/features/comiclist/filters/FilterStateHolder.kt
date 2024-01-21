package com.colibrez.xkcdreader.android.ui.features.comiclist.filters

import com.colibrez.xkcdreader.android.ui.core.mvvm.StateHolder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class FilterStateHolder : StateHolder<FiltersState, FilterUserAction> {
    private val _state =
        MutableStateFlow(
            FiltersState(
                isReadFilter = Filter.IsRead(selection = ReadFilter.All),
                favoriteFilter = Filter.Favorite(selection = FavoriteFilter.All),
            ),
        )

    override val state: StateFlow<FiltersState>
        get() = _state.asStateFlow()

    override fun handle(action: FilterUserAction) {
        when (action) {
            is FilterUserAction.IsReadFilterSelected -> {
                _state.update {
                    it.copy(isReadFilter = Filter.IsRead(selection = action.newFilterValue))
                }
            }

            is FilterUserAction.IsFavoriteFilterSelected -> {
                _state.update {
                    it.copy(favoriteFilter = Filter.Favorite(selection = action.newFilterValue))
                }
            }

            is FilterUserAction.ClearFilter -> {
                _state.update {
                    when (action.filter) {
                        is Filter.IsRead -> it.copy(isReadFilter = Filter.IsRead(selection = ReadFilter.All))
                        is Filter.Favorite -> it.copy(favoriteFilter = Filter.Favorite(selection = FavoriteFilter.All))
                    }
                }
            }
        }
    }
}
