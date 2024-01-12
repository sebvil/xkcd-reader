package com.colibrez.xkcdreader.android.ui.features.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector
import com.colibrez.xkcdreader.android.ui.features.destinations.ComicListScreenDestination
import com.colibrez.xkcdreader.android.ui.features.destinations.FavoritesScreenDestination
import com.ramcosta.composedestinations.spec.DirectionDestinationSpec

enum class NavigationBarDestination(
    val direction: DirectionDestinationSpec,
    val icon: ImageVector,
    val label: String
) {
    AllComics(direction = ComicListScreenDestination, icon = Icons.Default.List, label = "All comics"),
    Favorites(direction = FavoritesScreenDestination, icon = Icons.Default.Star, label = "Favorites"),
}
