package com.colibrez.xkcdreader.android.ui.features.comiclist

import androidx.compose.runtime.Stable
import androidx.paging.PagingData
import com.colibrez.xkcdreader.android.ui.components.paging.PagingState
import com.colibrez.xkcdreader.android.ui.core.mvvm.UiState
import com.colibrez.xkcdreader.model.Comic
import kotlinx.coroutines.flow.Flow

@Stable
data class ComicListState(
    val comics: PagingState<ListComic>
) : UiState