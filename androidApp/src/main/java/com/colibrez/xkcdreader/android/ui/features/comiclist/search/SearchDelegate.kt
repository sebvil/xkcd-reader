package com.colibrez.xkcdreader.android.ui.features.comiclist.search

import com.colibrez.xkcdreader.android.ui.core.mvvm.ComponentDelegate

interface SearchDelegate : ComponentDelegate {
    fun onSearchQueryUpdated(newQuery: String)
}
