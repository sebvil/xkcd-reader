package com.colibrez.xkcdreader.android.ui.features.comiclist

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.RemoteMediator
import androidx.paging.cachedIn
import androidx.paging.map
import androidx.savedstate.SavedStateRegistryOwner
import com.colibrez.xkcdreader.android.ui.core.mvvm.BaseViewModel
import com.colibrez.xkcdreader.android.ui.core.mvvm.UiState
import com.colibrez.xkcdreader.android.ui.core.mvvm.UserAction
import com.colibrez.xkcdreader.data.repository.ComicRepository
import com.colibrez.xkcdreader.model.Comic
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch


sealed interface ComicListUserAction : UserAction {
    data class ToggleFavorite(val comicNum: Long, val isFavorite: Boolean) : ComicListUserAction
}

data class ListComic(
    val comicNumber: Long,
    val title: String,
    val imageUrl: String,
    val isFavorite: Boolean,
    val isRead: Boolean
)

data class ComicListState(val comics: Flow<PagingData<ListComic>>) : UiState

@OptIn(ExperimentalPagingApi::class)
class ComicListViewModel(
    comicsRemoteMediator: RemoteMediator<Int, Comic>,
    pagingSourceFactory: () -> PagingSource<Int, Comic>,
    private val comicRepository: ComicRepository
) : BaseViewModel<ComicListState, ComicListUserAction>() {

    override val state: StateFlow<ComicListState> = MutableStateFlow(
        ComicListState(
            comics = Pager(
                config = PagingConfig(pageSize = 20),
                remoteMediator = comicsRemoteMediator,
                pagingSourceFactory = pagingSourceFactory
            ).flow.map { data ->
                data.map {
                    ListComic(
                        comicNumber = it.num,
                        title = it.title,
                        imageUrl = it.img,
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
        }
    }


    class Factory(
        owner: SavedStateRegistryOwner,
        private val comicsRemoteMediator: RemoteMediator<Int, Comic>,
        private val pagingSourceFactory: () -> PagingSource<Int, Comic>,
        private val comicRepository: ComicRepository,
    ) : AbstractSavedStateViewModelFactory(owner, null) {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(
            key: String,
            modelClass: Class<T>,
            handle: SavedStateHandle
        ): T {
            return ComicListViewModel(
                comicsRemoteMediator,
                pagingSourceFactory,
                comicRepository
            ) as T
        }
    }
}