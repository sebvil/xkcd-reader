package com.colibrez.xkcdreader.android.ui.features.comiclist.search

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    searchStateHolder: SearchStateHolder,
    modifier: Modifier = Modifier,
) {
    var query by rememberSaveable {
        mutableStateOf("")
    }

    SearchBar(
        query = query,
        onQueryChange = {
            query = it
            searchStateHolder.handle(SearchUserAction.QuerySubmitted(it))
        },
        onSearch = {},
        active = false,
        onActiveChange = {},
        modifier = modifier,
        placeholder = {
            Text(text = "Search comics")
        },
        trailingIcon = if (query.isNotEmpty()) {
            {
                IconButton(onClick = {
                    query = ""
                    searchStateHolder.handle(SearchUserAction.SearchCleared)
                }) {
                    Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear search")
                }
            }
        } else {
            null
        },
        leadingIcon = if (query.isNotEmpty()) {
            null
        } else {
            {
                Icon(imageVector = Icons.Default.Search, contentDescription = null)
            }
        },
    ) {}
}
