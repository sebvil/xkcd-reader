package com.colibrez.xkcdreader.android.ui.features.comiclist.filters

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.colibrez.xkcdreader.android.ui.MyApplicationTheme
import kotlin.enums.enumEntries

@Composable
fun FilterMenu(
    filtersState: FiltersState,
    onIsReadFilterClicked: (ReadFilter) -> Unit,
    onFavoriteFilterClicked: (FavoriteFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        FilterSection(
            filter = filtersState.isReadFilter,
            icon = Icons.Default.RemoveRedEye,
            onFilterClicked = onIsReadFilterClicked,
            modifier = Modifier.padding(vertical = 8.dp),
        )
        Divider()
        FilterSection(
            filter = filtersState.favoriteFilter,
            icon = Icons.Default.Star,
            onFilterClicked = onFavoriteFilterClicked,
            modifier = Modifier.padding(vertical = 8.dp),
        )
    }
}

@Preview
@Composable
private fun FilterMenuPreviews() {
    MyApplicationTheme {
        Surface {
            FilterMenu(
                filtersState =
                FiltersState(
                    isReadFilter = Filter.IsRead(selection = ReadFilter.All),
                    favoriteFilter = Filter.Favorite(selection = FavoriteFilter.All),
                ),
                onIsReadFilterClicked = {},
                onFavoriteFilterClicked = {},
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalStdlibApi::class)
@Composable
inline fun <reified T> FilterSection(
    filter: Filter<T>,
    icon: ImageVector,
    crossinline onFilterClicked: (T) -> Unit,
    modifier: Modifier = Modifier,
) where T : Enum<T>, T : EnumFilter<T> {
    Column(modifier = modifier) {
        Row(horizontalArrangement = Arrangement.spacedBy(space = 8.dp)) {
            Icon(imageVector = icon, contentDescription = null)
            Text(text = filter.name, style = MaterialTheme.typography.bodyLarge)
        }
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            enumEntries<T>().forEach {
                FilterChip(
                    filter = it,
                    selected = filter.selection == it,
                    onClick = { onFilterClicked(it) },
                )
            }
        }
    }
}
