package com.colibrez.xkcdreader.android.ui.features.comic

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.colibrez.xkcdreader.android.DependencyContainer
import com.colibrez.xkcdreader.android.ui.core.mvvm.BaseUiComponent
import com.colibrez.xkcdreader.android.ui.core.mvvm.StateHolder
import com.colibrez.xkcdreader.android.ui.core.mvvm.componentScope
import kotlinx.coroutines.CoroutineScope

class ComicScreen(
    private val arguments: ComicScreenArguments,
    private val popScreen: () -> Unit,
    override val componentScope: CoroutineScope = componentScope()
) : BaseUiComponent<ComicState, ComicUserAction>() {

    override fun createStateHolder(dependencyContainer: DependencyContainer): StateHolder<ComicState, ComicUserAction> {
        return ComicStateHolder(
            arguments = arguments,
            popScreen = popScreen,
            viewModelScope = componentScope,
            comicRepository = dependencyContainer.comicRepository,
        )
    }

    @Composable
    override fun Content(state: ComicState, handle: (ComicUserAction) -> Unit, modifier: Modifier) {
        ComicLayout(
            state = state,
            handleUserAction = handle,
            hasBackButton = true,
            modifier = modifier,
        )
    }
}
