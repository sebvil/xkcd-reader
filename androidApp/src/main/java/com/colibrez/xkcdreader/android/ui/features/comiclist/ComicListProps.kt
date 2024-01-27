package com.colibrez.xkcdreader.android.ui.features.comiclist

import com.colibrez.xkcdreader.android.ui.core.mvvm.ComponentProps

data class ComicListProps(
    val isUnreadFilterApplied: Boolean,
    val isFavoriteFilterApplied: Boolean,
    val searchQuery: String
) : ComponentProps
