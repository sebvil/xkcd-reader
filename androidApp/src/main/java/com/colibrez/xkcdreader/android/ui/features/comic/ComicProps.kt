package com.colibrez.xkcdreader.android.ui.features.comic

import com.colibrez.xkcdreader.android.ui.core.mvvm.ComponentProps

data class ComicProps(
    val comicNumber: Long?,
    val shownComics: List<Long>,
    val isShowingComic: Boolean,
) : ComponentProps
