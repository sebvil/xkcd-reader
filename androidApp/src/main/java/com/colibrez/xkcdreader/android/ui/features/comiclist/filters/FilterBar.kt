package com.colibrez.xkcdreader.android.ui.features.comiclist.filters

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Tune
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.colibrez.xkcdreader.android.ui.components.BottomSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBar(
    stateHolder: FilterStateHolder,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues()
) {
    val state by stateHolder.state.collectAsState()
    val filters = listOf(state.isReadFilter, state.favoriteFilter).filter {
        it.selection != it.default
    }

    var showMenu by remember {
        mutableStateOf(false)
    }

    // Using LazyRow to reduce refactoring if more filters are added
    LazyRow(
        modifier = modifier,
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item {
            ElevatedFilterChip(
                selected = false,
                onClick = { showMenu = true },
                label = {
                    Text(text = "Filters")
                },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Tune, contentDescription = null)
                },
            )
        }
        items(filters) {
            ElevatedFilterChip(
                selected = true,
                onClick = { stateHolder.handle(FilterUserAction.ClearFilter(it)) },
                label = {
                    Text(text = it.selection.displayName)
                },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                    )
                },
            )
        }
    }

    if (showMenu) {
        BottomSheet(title = "Filters", onDismissRequest = { showMenu = false }) {
            FilterMenu(
                filtersState = state,
                onIsReadFilterClicked = {
                    stateHolder.handle(
                        FilterUserAction.IsReadFilterSelected(
                            it,
                        ),
                    )
                },
                onFavoriteFilterClicked = {
                    stateHolder.handle(
                        FilterUserAction.IsFavoriteFilterSelected(
                            it,
                        ),
                    )
                },
            )
        }
    }
}
