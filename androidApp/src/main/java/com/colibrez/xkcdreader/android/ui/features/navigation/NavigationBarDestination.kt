package com.colibrez.xkcdreader.android.ui.features.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.NewReleases
import androidx.compose.ui.graphics.vector.ImageVector
import com.colibrez.xkcdreader.android.ui.core.mvvm.UiComponent
import com.colibrez.xkcdreader.android.ui.features.comic.latest.LatestComicScreen
import com.colibrez.xkcdreader.android.ui.features.comiclist.AllComicsComponent

enum class NavigationBarDestination(
    val component: UiComponent<*, *>,
    val icon: ImageVector,
    val label: String
) {
    Latest(component = LatestComicScreen(), icon = Icons.Default.NewReleases, label = "Latest"),
    AllComics(component = AllComicsComponent(), icon = Icons.Default.List, label = "All comics"),
}
