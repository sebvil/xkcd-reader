package com.colibrez.xkcdreader.android.ui.features.comiclist

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
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination
@RootNavGraph(start = true)
@Composable
fun ComicListScreen(
    navigator: DestinationsNavigator,
    viewModel: ComicListViewModel = comicListViewModel()
) {
    Screen(viewModel = viewModel, navigator = navigator) { _, handleUserAction ->
        ComicListLayout(
            pagingStateHolder = viewModel.pagingStateHolder,
            handleUserAction = handleUserAction,
        )
    }
}

@Composable
fun ComicListLayout(
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
            modifier = Modifier.padding(vertical = 8.dp),
        )
    }
}

@Composable
fun comicListViewModel(
    savedStateRegistryOwner: SavedStateRegistryOwner = LocalSavedStateRegistryOwner.current
): ComicListViewModel {
    val dependencyContainer =
        (LocalContext.current.applicationContext as XkcdReaderApplication).dependencyContainer

    val factory = ComicListViewModel.Factory(
        owner = savedStateRegistryOwner,
        allComicsPagingDataSource = AllComicsPagingDataSource(
            dependencyContainer.apiClient,
            dependencyContainer.comicRepository,
        ),
        comicRepository = dependencyContainer.comicRepository,
    )
    return viewModel(factory = factory)
}
