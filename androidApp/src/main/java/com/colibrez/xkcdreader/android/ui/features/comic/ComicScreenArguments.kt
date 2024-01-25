package com.colibrez.xkcdreader.android.ui.features.comic

import kotlinx.serialization.Serializable

@Serializable
data class ComicScreenArguments(val comicNumber: Long, val comicTitle: String)
