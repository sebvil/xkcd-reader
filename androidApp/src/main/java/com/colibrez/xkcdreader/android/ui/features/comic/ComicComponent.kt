package com.colibrez.xkcdreader.android.ui.features.comic

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.colibrez.xkcdreader.android.DependencyContainer
import com.colibrez.xkcdreader.android.ui.core.mvvm.BaseUiComponent
import com.colibrez.xkcdreader.android.ui.core.mvvm.NoProps
import com.colibrez.xkcdreader.android.ui.core.mvvm.componentScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ComicComponent(
    arguments: ComicArguments,
    delegate: ComicDelegate,
    override val componentScope: CoroutineScope = componentScope()
) : BaseUiComponent<ComicState, ComicUserAction, ComicStateHolder, ComicArguments, NoProps, ComicDelegate>(
    arguments = arguments,
    delegate = delegate,
    props = MutableStateFlow(NoProps),
) {

    override fun createStateHolder(
        dependencyContainer: DependencyContainer,
        arguments: ComicArguments,
        props: StateFlow<NoProps>,
        delegate: ComicDelegate
    ): ComicStateHolder {
        return ComicStateHolder(
            arguments = arguments,
            delegate = delegate,
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
