package com.colibrez.xkcdreader.android.ui.features.comiclist.filters

import com.colibrez.xkcdreader.android.ui.core.mvvm.StateHolder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class FilterStateHolder(private val delegate: FiltersDelegate) :
    StateHolder<FiltersState, FilterUserAction> {
    private val _state =
        MutableStateFlow(
            FiltersState(
                unread = Filter.Unread(selected = false),
                favorites = Filter.Favorites(selected = false),
            ),
        )

    override val state: StateFlow<FiltersState>
        get() = _state.asStateFlow()

    override fun handle(action: FilterUserAction) {
        when (action) {
            is FilterUserAction.UnreadFilterSelected -> {
                _state.update {
                    it.copy(unread = Filter.Unread(action.newValue))
                }
                delegate.onUnreadFilterChanged(newValue = action.newValue)
            }

            is FilterUserAction.FavoriteFilterSelected -> {
                _state.update {
                    it.copy(favorites = Filter.Favorites(action.newValue))
                }
                delegate.onFavoriteFilterChanges(newValue = action.newValue)
            }
        }
    }
}
