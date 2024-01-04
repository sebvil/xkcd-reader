package com.colibrez.xkcdreader.android.ui.features.comic

import com.colibrez.xkcdreader.android.ui.core.navigation.ScreenArguments
import com.colibrez.xkcdreader.android.ui.features.destinations.ComicScreenDestination
import com.ramcosta.composedestinations.spec.Direction
import kotlinx.serialization.Serializable

@Serializable
data class ComicScreenArguments(val comicNumber: Long, val comicTitle: String) :
    ScreenArguments<ComicScreenArguments> {
    override val direction: Direction
        get() = ComicScreenDestination(this)
}