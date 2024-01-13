package com.colibrez.xkcdreader.android.ui.features.comiclist.all

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSavedStateRegistryOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.colibrez.xkcdreader.android.XkcdReaderApplication
import com.colibrez.xkcdreader.android.data.repository.paging.AllComicsPagingDataSource
import com.colibrez.xkcdreader.android.ui.components.comic.ComicListItem
import com.colibrez.xkcdreader.android.ui.components.comic.ListComic
import com.colibrez.xkcdreader.android.ui.components.paging.PagingLazyColumn
import com.colibrez.xkcdreader.android.ui.components.paging.PagingStateHolder
import com.colibrez.xkcdreader.android.ui.core.navigation.Screen
import com.colibrez.xkcdreader.android.ui.features.comiclist.ComicListUserAction
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination
@RootNavGraph(start = true)
@Composable
fun AllComicsScreen(
    navigator: DestinationsNavigator,
    viewModel: AllComicsViewModel = allComicsViewModel()
) {
    Screen(viewModel = viewModel, navigator = navigator) { _, handleUserAction ->
        AllComicsLayout(
            pagingStateHolder = viewModel.pagingStateHolder,
            handleUserAction = handleUserAction,
        )
    }
}

@Composable
fun AllComicsLayout(
    pagingStateHolder: PagingStateHolder<ListComic, *>,
    handleUserAction: (ComicListUserAction) -> Unit,
    modifier: Modifier = Modifier
) {
    PagingLazyColumn(modifier = modifier, stateHolder = pagingStateHolder) { item ->
        ComicListItem(
            state = item,
            onClick = {
                handleUserAction(
                    ComicListUserAction.ComicClicked(
                        comicNum = item.comicNumber,
                        comicTitle = item.title,
                    ),
                )
            },
            onToggleFavorite = {
                handleUserAction(
                    ComicListUserAction.ToggleFavorite(
                        comicNum = item.comicNumber,
                        isFavorite = it
                    )
                )
            },
            modifier = Modifier.padding(vertical = 8.dp),
        )
    }
}

@Composable
fun allComicsViewModel(
    savedStateRegistryOwner: SavedStateRegistryOwner = LocalSavedStateRegistryOwner.current
): AllComicsViewModel {
    val dependencyContainer =
        (LocalContext.current.applicationContext as XkcdReaderApplication).dependencyContainer

    val factory = AllComicsViewModel.Factory(
        owner = savedStateRegistryOwner,
        allComicsPagingDataSource = AllComicsPagingDataSource(
            dependencyContainer.apiClient,
            dependencyContainer.comicRepository,
        ),
        comicRepository = dependencyContainer.comicRepository,
    )
    return viewModel(factory = factory)
}
