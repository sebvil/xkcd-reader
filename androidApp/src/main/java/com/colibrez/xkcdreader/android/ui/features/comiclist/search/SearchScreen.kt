package com.colibrez.xkcdreader.android.ui.features.comiclist.search

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.colibrez.xkcdreader.android.ui.components.comic.ComicListItem
import com.colibrez.xkcdreader.android.ui.features.comiclist.ComicListUserAction

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    searchStateHolder: SearchStateHolder,
    modifier: Modifier = Modifier,
    handleUserAction: (ComicListUserAction) -> Unit = {}
) {
    var query by rememberSaveable {
        mutableStateOf("")
    }
    var active by rememberSaveable {
        mutableStateOf(false)
    }
    SearchBar(
        query = query,
        onQueryChange = {
            query = it
            searchStateHolder.handle(SearchUserAction.QuerySubmitted(it))
        },
        onSearch = {},
        active = active,
        onActiveChange = { active = it },
        modifier = modifier,
        placeholder = {
            Text(text = "Search comics")
        },
        trailingIcon = if (query.isNotEmpty()) {
            {
                IconButton(onClick = { searchStateHolder.handle(SearchUserAction.SearchCleared) }) {
                    Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear search")
                }
            }
        } else {
            null
        },
        leadingIcon = if (active) {
            {
                IconButton(onClick = { active = false }) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Go back")
                }
            }
        } else {
            {
                Icon(imageVector = Icons.Default.Search, contentDescription = null)
            }
        },
    ) {
        val searchState by searchStateHolder.state.collectAsState()
        LazyColumn {
            items(searchState.results) { comic ->
                ComicListItem(
                    state = comic,
                    onClick = {
                        handleUserAction(
                            ComicListUserAction.ComicClicked(
                                comicNum = comic.comicNumber,
                                comicTitle = comic.title,
                            ),
                        )
                    },
                    onToggleFavorite = {
                        handleUserAction(
                            ComicListUserAction.ToggleFavorite(
                                comicNum = comic.comicNumber,
                                isFavorite = it,
                            ),
                        )
                    },
                )
            }
        }
    }
}
