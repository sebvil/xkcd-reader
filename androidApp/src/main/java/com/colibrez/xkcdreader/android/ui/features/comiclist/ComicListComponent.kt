package com.colibrez.xkcdreader.android.ui.features.comiclist

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.colibrez.xkcdreader.android.DependencyContainer
import com.colibrez.xkcdreader.android.ui.core.mvvm.BaseUiComponent
import com.colibrez.xkcdreader.android.ui.core.mvvm.NoArguments
import com.colibrez.xkcdreader.android.ui.core.mvvm.componentScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow

class ComicListComponent(
    props: StateFlow<ComicListProps>,
    delegate: ComicListDelegate,
    override val componentScope: CoroutineScope = componentScope()
) : BaseUiComponent<
    ComicListState,
    ComicListUserAction,
    ComicListStateHolder,
    NoArguments,
    ComicListProps,
    ComicListDelegate,
    >(
    arguments = NoArguments,
    delegate = delegate,
    props = props,
) {

    override fun createStateHolder(
        dependencyContainer: DependencyContainer,
        arguments: NoArguments,
        props: StateFlow<ComicListProps>,
        delegate: ComicListDelegate
    ): ComicListStateHolder {
        return ComicListStateHolder(
            props = props,
            viewModelScope = componentScope,
            comicRepository = dependencyContainer.comicRepository,
            delegate = delegate,
        )
    }

    @Composable
    override fun Content(
        state: ComicListState,
        handle: (ComicListUserAction) -> Unit,
        modifier: Modifier
    ) {
        ComicListLayout(state = state, handleUserAction = handle, modifier = modifier)
    }
}
