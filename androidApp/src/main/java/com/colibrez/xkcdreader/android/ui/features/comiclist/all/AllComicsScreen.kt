package com.colibrez.xkcdreader.android.ui.features.comiclist.all

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSavedStateRegistryOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.colibrez.xkcdreader.android.XkcdReaderApplication
import com.colibrez.xkcdreader.android.ui.core.navigation.Screen
import com.colibrez.xkcdreader.android.ui.features.comiclist.ComicListLayout
import com.colibrez.xkcdreader.android.ui.features.comiclist.all.filters.FilterBar
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@RootNavGraph(start = true)
@Destination
@Composable
fun AllComicsScreen(
    navigator: DestinationsNavigator,
    viewModel: AllComicsViewModel = allComicsViewModel(),
) {
    Screen(viewModel = viewModel, navigator = navigator) { state, handleUserAction ->
        Column {
            FilterBar(stateHolder = viewModel.filterStateHolder)
            ComicListLayout(state = state, handleUserAction = handleUserAction)
        }
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
        comicRepository = dependencyContainer.comicRepository,
    )
    return viewModel(factory = factory)
}
