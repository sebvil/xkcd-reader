package com.colibrez.xkcdreader.android.ui.features.comic.latest

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSavedStateRegistryOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.colibrez.xkcdreader.android.XkcdReaderApplication
import com.colibrez.xkcdreader.android.ui.core.navigation.Screen
import com.colibrez.xkcdreader.android.ui.features.comic.ComicLayout
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination
@Composable
fun LatestComicScreen(
    navigator: DestinationsNavigator,
    viewModel: LatestComicViewModel = latestComicViewModel()
) {
    Screen(viewModel = viewModel, navigator = navigator) { state, handleUserAction ->
        ComicLayout(state = state, handleUserAction = handleUserAction, hasBackButton = false)
    }
}

@Composable
fun latestComicViewModel(
    savedStateRegistryOwner: SavedStateRegistryOwner = LocalSavedStateRegistryOwner.current,
): LatestComicViewModel {
    val dependencyContainer =
        (LocalContext.current.applicationContext as XkcdReaderApplication).dependencyContainer
    val factory = LatestComicViewModel.Factory(
        owner = savedStateRegistryOwner,
        comicRepository = dependencyContainer.comicRepository,
    )
    return viewModel(factory = factory)
}
