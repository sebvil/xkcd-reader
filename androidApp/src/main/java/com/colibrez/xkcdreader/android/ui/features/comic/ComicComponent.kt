package com.colibrez.xkcdreader.android.ui.features.comic

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.colibrez.xkcdreader.android.DependencyContainer
import com.colibrez.xkcdreader.android.ui.core.mvvm.BaseUiComponent
import com.colibrez.xkcdreader.android.ui.core.mvvm.ComponentProps
import com.colibrez.xkcdreader.android.ui.core.mvvm.NoArguments
import com.colibrez.xkcdreader.android.ui.core.mvvm.componentScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow


data class ComicProps(
    val comicNumber: Long?,
    val shownComics: List<Long>,
    val isShowingComic: Boolean,
    val popScreen: () -> Unit,
    val showComic: (Long) -> Unit,
) : ComponentProps


class ComicComponent(
    props: StateFlow<ComicProps>,
    delegate: ComicDelegate,
    override val componentScope: CoroutineScope = componentScope()
) : BaseUiComponent<ComicState, ComicUserAction, ComicStateHolder, NoArguments, ComicProps, ComicDelegate>(
    arguments = NoArguments,
    delegate = delegate,
    props = props,
) {

    override fun createStateHolder(
        dependencyContainer: DependencyContainer,
        arguments: NoArguments,
        props: StateFlow<ComicProps>,
        delegate: ComicDelegate
    ): ComicStateHolder {
        return ComicStateHolder(
            delegate = delegate,
            comicProps = props,
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
