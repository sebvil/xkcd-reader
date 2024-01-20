package com.colibrez.xkcdreader.android.ui.components.comic

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.colibrez.xkcdreader.android.ui.components.FavoriteButton

@Composable
fun ComicListItem(
    state: ListComic,
    onClick: () -> Unit,
    onToggleFavorite: (isFavorite: Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    ListItem(
        headlineContent = {
            Text(
                text = "${state.comicNumber}. ${state.title}",
                fontWeight = if (state.isRead) null else FontWeight.ExtraBold,
            )
        },
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        leadingContent = {
            ComicImage(state.imageUrl)
        },
        trailingContent = {
            FavoriteButton(isFavorite = state.isFavorite, onFavoriteChanged = onToggleFavorite)
        },
    )
}

@Composable
private fun ComicImage(imageUrl: String) {
    var loading by remember {
        mutableStateOf(true)
    }
    if (loading) {
        // Images load fast enough that a placeholder is a bit jarring
        Box(modifier = Modifier.size(64.dp))
    }
    AsyncImage(
        model = imageUrl,
        contentDescription = null,
        modifier = Modifier.size(64.dp),
        onSuccess = {
            loading = false
        },
        onError = {
            loading = false
        },
    )
}
