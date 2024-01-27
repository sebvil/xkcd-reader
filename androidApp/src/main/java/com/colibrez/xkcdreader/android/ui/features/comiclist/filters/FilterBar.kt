package com.colibrez.xkcdreader.android.ui.features.comiclist.filters

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.colibrez.xkcdreader.android.ui.components.FilterChip

@Composable
fun FilterBar(
    state: FiltersState,
    handle: (FilterUserAction) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues()
) {
    LazyRow(
        modifier = modifier,
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item {
            FilterChip(
                displayName = state.unread.name,
                selected = state.unread.selected,
                onClick = { handle(FilterUserAction.UnreadFilterSelected(!state.unread.selected)) },
            )
        }

        item {
            FilterChip(
                displayName = state.favorites.name,
                selected = state.favorites.selected,
                onClick = { handle(FilterUserAction.FavoriteFilterSelected(!state.favorites.selected)) },
            )
        }
    }
}
