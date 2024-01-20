package com.colibrez.xkcdreader.android.ui.features.comiclist.all

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSavedStateRegistryOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.colibrez.xkcdreader.android.XkcdReaderApplication
import com.colibrez.xkcdreader.android.ui.core.navigation.Screen
import com.colibrez.xkcdreader.android.ui.features.comiclist.ComicListLayout
import com.colibrez.xkcdreader.android.ui.features.comiclist.all.filters.FilterBar
import com.colibrez.xkcdreader.android.ui.features.comiclist.search.SearchScreen
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@RootNavGraph(start = true)
@Destination
@Composable
fun AllComicsScreen(
    navigator: DestinationsNavigator,
    modifier: Modifier = Modifier,
    viewModel: AllComicsViewModel = allComicsViewModel(),
) {
    Screen(viewModel = viewModel, navigator = navigator) { state, handleUserAction ->
        Column(modifier = modifier.fillMaxSize()) {
            SearchScreen(
                searchStateHolder = viewModel.searchStateHolder,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                handleUserAction = handleUserAction,
            )
            FilterBar(
                stateHolder = viewModel.filterStateHolder,
                contentPadding = PaddingValues(horizontal = 16.dp),
            )
            Divider(modifier = Modifier.fillMaxWidth())
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
        searchRepository = dependencyContainer.searchRepository,
    )
    return viewModel(factory = factory)
}
