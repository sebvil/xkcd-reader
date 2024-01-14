package com.colibrez.xkcdreader.android.ui.features.comiclist.all

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSavedStateRegistryOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.colibrez.xkcdreader.android.XkcdReaderApplication
import com.colibrez.xkcdreader.android.ui.core.navigation.Screen
import com.colibrez.xkcdreader.android.ui.features.comiclist.ComicListLayout
import com.colibrez.xkcdreader.android.ui.features.comiclist.all.filters.FilterStateHolder
import com.colibrez.xkcdreader.android.ui.features.comiclist.all.filters.FilterUserAction
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
    Screen(viewModel = viewModel, navigator = navigator) { state, handleUserAction ->
        Column {
            FilterBar(stateHolder = viewModel.filterStateHolder)
            ComicListLayout(state = state, handleUserAction = handleUserAction)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterBar(stateHolder: FilterStateHolder, modifier: Modifier = Modifier) {
    val state by stateHolder.state.collectAsState()
    // Using LazyRow to reduce refactoring if more filters are added
    LazyRow(modifier = modifier) {
        item {
            var expanded by remember { mutableStateOf(false) }

            Box(
                modifier = Modifier
                    .wrapContentSize(Alignment.TopStart)
            ) {

                ElevatedFilterChip(
                    selected = state.isReadFilter != null,
                    onClick = { expanded = true },
                    leadingIcon = state.isReadFilter?.let {
                        {
                            Icon(imageVector = Icons.Default.Check, contentDescription = null)
                        }
                    },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null
                        )
                    },
                    label = {
                        val text = when (state.isReadFilter) {
                            true -> "Read"
                            false -> "Unread"
                            null -> "All"
                        }

                        Text(text = text)
                    })
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("All") },
                        onClick = {
                            stateHolder.handle(FilterUserAction.IsReadFilterSelected(null))
                            expanded = false
                        },
                        trailingIcon = if (state.isReadFilter == null) {
                            {
                                Icon(imageVector = Icons.Default.Check, contentDescription = null)
                            }
                        } else null
                    )
                    DropdownMenuItem(
                        text = { Text("Unread") },
                        onClick = {
                            stateHolder.handle(FilterUserAction.IsReadFilterSelected(false))
                            expanded = false
                        },
                        trailingIcon = if (state.isReadFilter == false) {
                            {
                                Icon(imageVector = Icons.Default.Check, contentDescription = null)
                            }
                        } else null
                    )
                    DropdownMenuItem(
                        text = { Text("Read") },
                        onClick = {
                            stateHolder.handle(FilterUserAction.IsReadFilterSelected(true))
                            expanded = false
                        },
                        trailingIcon = if (state.isReadFilter == true) {
                            {
                                Icon(imageVector = Icons.Default.Check, contentDescription = null)
                            }
                        } else null
                    )
                }
            }
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
