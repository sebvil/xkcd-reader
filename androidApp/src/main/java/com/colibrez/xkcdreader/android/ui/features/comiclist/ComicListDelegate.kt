package com.colibrez.xkcdreader.android.ui.features.comiclist

import com.colibrez.xkcdreader.android.ui.core.mvvm.ComponentDelegate

interface ComicListDelegate : ComponentDelegate {
    fun onComicSelected(comicNumber: Long)
    fun onShownComicsChanged(shownComics: List<Long>)
}
