package com.colibrez.xkcdreader.android.ui.features.comiclist.filters

import com.colibrez.xkcdreader.android.ui.core.mvvm.ComponentDelegate

interface FiltersDelegate : ComponentDelegate {
    fun onUnreadFilterChanged(newValue: Boolean)
    fun onFavoriteFilterChanges(newValue: Boolean)
}
