package com.colibrez.xkcdreader.android.ui.features.comiclist

import com.colibrez.xkcdreader.android.ui.core.mvvm.ComponentDelegate
import com.colibrez.xkcdreader.android.ui.features.comic.ComicArguments

interface ComicListDelegate : ComponentDelegate {
    fun showComic(comicArguments: ComicArguments)
}
