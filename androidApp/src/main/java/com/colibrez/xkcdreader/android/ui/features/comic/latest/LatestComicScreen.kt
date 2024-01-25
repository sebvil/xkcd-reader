package com.colibrez.xkcdreader.android.ui.features.comic.latest

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.colibrez.xkcdreader.android.DependencyContainer
import com.colibrez.xkcdreader.android.ui.core.mvvm.BaseUiComponent
import com.colibrez.xkcdreader.android.ui.core.mvvm.StateHolder
import com.colibrez.xkcdreader.android.ui.core.mvvm.componentScope
import com.colibrez.xkcdreader.android.ui.features.comic.ComicLayout
import com.colibrez.xkcdreader.android.ui.features.comic.ComicState
import com.colibrez.xkcdreader.android.ui.features.comic.ComicUserAction
import kotlinx.coroutines.CoroutineScope

class LatestComicScreen(
    override val componentScope: CoroutineScope = componentScope()
) : BaseUiComponent<ComicState, ComicUserAction>() {

    override fun createStateHolder(dependencyContainer: DependencyContainer): StateHolder<ComicState, ComicUserAction> {
        return LatestComicStateHolder(
            viewModelScope = componentScope,
            comicRepository = dependencyContainer.comicRepository,
        )
    }

    @Composable
    override fun Content(state: ComicState, handle: (ComicUserAction) -> Unit, modifier: Modifier) {
        ComicLayout(
            state = state,
            handleUserAction = handle,
            hasBackButton = false,
            modifier = modifier,
        )
    }
}
