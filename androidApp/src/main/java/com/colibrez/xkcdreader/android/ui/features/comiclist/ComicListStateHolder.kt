package com.colibrez.xkcdreader.android.ui.features.comiclist

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.RemoteMediator
import androidx.paging.cachedIn
import androidx.paging.map
import com.colibrez.xkcdreader.android.ui.core.mvvm.BaseStateHolder
import com.colibrez.xkcdreader.android.ui.core.navigation.NavigationState
import com.colibrez.xkcdreader.android.ui.features.comic.ComicScreenArguments
import com.colibrez.xkcdreader.data.repository.ComicRepository
import com.colibrez.xkcdreader.model.Comic
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch


@OptIn(ExperimentalPagingApi::class)
class ComicListStateHolder(
    private val viewModelScope: CoroutineScope,
    comicsRemoteMediator: RemoteMediator<Int, Comic>,
    pagingSourceFactory: () -> PagingSource<Int, Comic>,
    private val comicRepository: ComicRepository
) : BaseStateHolder<ComicListState, ComicListUserAction>() {

    override val state: StateFlow<ComicListState> = MutableStateFlow(
        ComicListState(
            comics = Pager(
                config = PagingConfig(pageSize = 20),
                remoteMediator = comicsRemoteMediator,
                pagingSourceFactory = pagingSourceFactory
            ).flow.map { data ->
                data.map {
                    ListComic(
                        comicNumber = it.number,
                        title = it.title,
                        imageUrl = it.imageUrl,
                        isFavorite = it.isFavorite,
                        isRead = it.isRead
                    )
                }
            }.cachedIn(viewModelScope)
        )
    ).asStateFlow()


    override fun handle(action: ComicListUserAction) {
        when (action) {
            is ComicListUserAction.ToggleFavorite -> {
                viewModelScope.launch {
                    comicRepository.toggleFavorite(action.comicNum, action.isFavorite)
                }
            }

            is ComicListUserAction.ComicClicked -> {
                setNavigationState(
                    NavigationState.ShowScreen(
                        ComicScreenArguments(
                            comicNumber = action.comicNum,
                            comicTitle = action.comicTitle
                        )
                    )
                )
            }
        }
    }
}