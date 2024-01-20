package com.colibrez.xkcdreader.android.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.IconToggleButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun FavoriteButton(
    isFavorite: Boolean,
    onFavoriteChanged: (isFavorite: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    IconToggleButton(
        checked = isFavorite,
        onCheckedChange = { onFavoriteChanged(isFavorite) },
        modifier = modifier,
        colors = IconButtonDefaults.iconToggleButtonColors(checkedContentColor = Color.Yellow),
    ) {
        Icon(
            imageVector = if (isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
            contentDescription = "Toggle favorite",
        )
    }
}
