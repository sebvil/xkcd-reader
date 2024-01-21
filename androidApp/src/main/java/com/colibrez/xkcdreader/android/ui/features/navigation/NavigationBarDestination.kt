package com.colibrez.xkcdreader.android.ui.features.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.NewReleases
import androidx.compose.ui.graphics.vector.ImageVector
import com.colibrez.xkcdreader.android.ui.features.destinations.ComicListScreenDestination
import com.colibrez.xkcdreader.android.ui.features.destinations.LatestComicScreenDestination
import com.ramcosta.composedestinations.spec.DirectionDestinationSpec

enum class NavigationBarDestination(
    val direction: DirectionDestinationSpec,
    val icon: ImageVector,
    val label: String
) {
    Latest(direction = LatestComicScreenDestination, icon = Icons.Default.NewReleases, label = "Latest"),
    AllComics(direction = ComicListScreenDestination, icon = Icons.Default.List, label = "All comics"),
}
