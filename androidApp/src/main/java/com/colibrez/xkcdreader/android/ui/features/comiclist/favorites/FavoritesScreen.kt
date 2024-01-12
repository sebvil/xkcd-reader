package com.colibrez.xkcdreader.android.ui.features.comiclist.favorites

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSavedStateRegistryOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.colibrez.xkcdreader.android.XkcdReaderApplication
import com.colibrez.xkcdreader.android.ui.components.comic.ComicListItem
import com.colibrez.xkcdreader.android.ui.core.navigation.Screen
import com.colibrez.xkcdreader.android.ui.features.comiclist.ComicListUserAction
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination
@Composable
fun FavoritesScreen(
    navigator: DestinationsNavigator,
    viewModel: FavoritesViewModel = favoritesViewModel()
) {
    Screen(viewModel = viewModel, navigator = navigator) { state, handleUserAction ->
        FavoritesLayout(
            state = state,
            handleUserAction = handleUserAction,
        )
    }
}

@Composable
fun FavoritesLayout(
    state: FavoritesState,
    handleUserAction: (ComicListUserAction) -> Unit,
    modifier: Modifier = Modifier
) {
    when (state) {
        is FavoritesState.Data -> {
            LazyColumn(modifier = modifier) {
                items(state.comics) { item ->
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
        }

        is FavoritesState.Loading -> {
            Box(modifier = modifier, contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
fun favoritesViewModel(
    savedStateRegistryOwner: SavedStateRegistryOwner = LocalSavedStateRegistryOwner.current
): FavoritesViewModel {
    val dependencyContainer =
        (LocalContext.current.applicationContext as XkcdReaderApplication).dependencyContainer

    val factory = FavoritesViewModel.Factory(
        owner = savedStateRegistryOwner,
        comicRepository = dependencyContainer.comicRepository,
    )
    return viewModel(factory = factory)
}
