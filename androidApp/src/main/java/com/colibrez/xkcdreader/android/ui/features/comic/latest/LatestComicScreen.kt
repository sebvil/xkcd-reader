package com.colibrez.xkcdreader.android.ui.features.comic.latest

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.colibrez.xkcdreader.android.DependencyContainer
import com.colibrez.xkcdreader.android.ui.core.mvvm.BaseUiComponent
import com.colibrez.xkcdreader.android.ui.core.mvvm.NoArguments
import com.colibrez.xkcdreader.android.ui.core.mvvm.NoDelegate
import com.colibrez.xkcdreader.android.ui.core.mvvm.NoProps
import com.colibrez.xkcdreader.android.ui.core.mvvm.componentScope
import com.colibrez.xkcdreader.android.ui.features.comic.ComicLayout
import com.colibrez.xkcdreader.android.ui.features.comic.ComicState
import com.colibrez.xkcdreader.android.ui.features.comic.ComicUserAction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LatestComicScreen(
    override val componentScope: CoroutineScope = componentScope()
) : BaseUiComponent<ComicState, ComicUserAction, LatestComicStateHolder, NoArguments, NoProps, NoDelegate>(
    arguments = NoArguments,
    delegate = NoDelegate,
    props = MutableStateFlow(NoProps),
) {

    @Composable
    override fun Content(state: ComicState, handle: (ComicUserAction) -> Unit, modifier: Modifier) {
        ComicLayout(
            state = state,
            handleUserAction = handle,
            hasBackButton = false,
            modifier = modifier,
        )
    }

    override fun createStateHolder(
        dependencyContainer: DependencyContainer,
        arguments: NoArguments,
        props: StateFlow<NoProps>,
        delegate: NoDelegate
    ): LatestComicStateHolder {
        return LatestComicStateHolder(
            viewModelScope = componentScope,
            comicRepository = dependencyContainer.comicRepository,
        )
    }
}
