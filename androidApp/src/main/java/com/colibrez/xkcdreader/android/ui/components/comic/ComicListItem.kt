package com.colibrez.xkcdreader.android.ui.components.comic

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
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

@Composable
fun ComicListItem(state: ListComic, modifier: Modifier = Modifier, onClick: () -> Unit) {
    ListItem(
        headlineContent = {
            Text(
                text = "${state.comicNumber}. ${state.title}",
                fontWeight = if (state.isRead) null else FontWeight.ExtraBold,
            )
        },
        modifier = modifier
            .padding(vertical = 8.dp)
            .clickable(onClick = onClick),
        leadingContent = {
            ComicImage(state.imageUrl)
        },
    )
}

@Composable
private fun ComicImage(imageUrl: String) {
    var loading by remember {
        mutableStateOf(true)
    }
    if (loading) {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.onSurface)
                .size(64.dp),
        )
    }
    AsyncImage(
        model = imageUrl,
        contentDescription = null,
        modifier = Modifier.sizeIn(minWidth = 64.dp, maxHeight = 64.dp, maxWidth = 64.dp),
        onSuccess = {
            loading = false
        },
        onError = {
            loading = false
        },
    )
}
