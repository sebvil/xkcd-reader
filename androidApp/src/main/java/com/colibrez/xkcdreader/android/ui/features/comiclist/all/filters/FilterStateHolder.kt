package com.colibrez.xkcdreader.android.ui.features.comiclist.all.filters

import com.colibrez.xkcdreader.android.ui.core.mvvm.StateHolder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class FilterStateHolder : StateHolder<FiltersState, FilterUserAction> {
    private val _state =
        MutableStateFlow(FiltersState(isReadFilter = EnumFilterState(selection = ReadFilter.All)))

    override val state: StateFlow<FiltersState>
        get() = _state.asStateFlow()

    override fun handle(action: FilterUserAction) {
        when (action) {
            is FilterUserAction.IsReadFilterSelected -> {
                _state.update {
                    it.copy(isReadFilter = EnumFilterState(selection = action.newFilterValue))
                }
            }
        }
    }
}
