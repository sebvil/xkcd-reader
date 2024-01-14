package com.colibrez.xkcdreader.android.ui.features.comiclist

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.colibrez.xkcdreader.android.ui.components.comic.ComicListItem
@Composable
fun ComicListLayout(
    state: ComicListState,
    handleUserAction: (ComicListUserAction) -> Unit,
    modifier: Modifier = Modifier
) {
    when (state) {
        is ComicListState.Data -> {
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
                        onToggleFavorite = {
                            handleUserAction(
                                ComicListUserAction.ToggleFavorite(
                                    comicNum = item.comicNumber,
                                    isFavorite = it,
                                ),
                            )
                        },
                        modifier = Modifier.padding(vertical = 8.dp),
                    )
                }
            }
        }

        is ComicListState.Loading -> {
            Box(modifier = modifier, contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }
}
