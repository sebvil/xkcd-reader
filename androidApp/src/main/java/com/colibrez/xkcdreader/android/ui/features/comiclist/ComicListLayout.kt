package com.colibrez.xkcdreader.android.ui.features.comiclist

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.colibrez.xkcdreader.android.ui.components.comic.ComicListItem

@Composable
fun ComicListLayout(
    state: ComicListState,
    handleUserAction: (ComicListUserAction) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues()
) {
    when (state) {
        is ComicListState.Data -> {
            LazyColumn(modifier = modifier, contentPadding = contentPadding) {
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
                    )
                }
            }
        }

        is ComicListState.Loading -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }
}
