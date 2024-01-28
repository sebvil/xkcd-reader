package com.colibrez.xkcdreader.android.ui.features.comiclist

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.colibrez.xkcdreader.android.DependencyContainer
import com.colibrez.xkcdreader.android.ui.core.mvvm.BaseUiComponent
import com.colibrez.xkcdreader.android.ui.core.mvvm.Handler
import com.colibrez.xkcdreader.android.ui.core.mvvm.NoArguments
import com.colibrez.xkcdreader.android.ui.core.mvvm.NoDelegate
import com.colibrez.xkcdreader.android.ui.core.mvvm.NoProps
import com.colibrez.xkcdreader.android.ui.core.mvvm.componentScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AllComicsComponent(override val componentScope: CoroutineScope = componentScope()) :
    BaseUiComponent<AllComicsState, AllComicsAction, AllComicsStateHolder, NoArguments, NoProps, NoDelegate>(
        arguments = NoArguments,
        delegate = NoDelegate,
        props = MutableStateFlow(NoProps),
    ) {

    override fun createStateHolder(
        dependencyContainer: DependencyContainer,
        arguments: NoArguments,
        props: StateFlow<NoProps>,
        delegate: NoDelegate
    ): AllComicsStateHolder {
        return AllComicsStateHolder()
    }

    @Composable
    override fun Content(
        state: AllComicsState,
        handle: Handler<AllComicsAction>,
        modifier: Modifier
    ) {
        BackHandler(enabled = state.selectedComic != null) {
            handle(AllComicsAction.HideComic)
        }

        Box(modifier = modifier) {
            Column(modifier = Modifier.fillMaxSize()) {
                state.searchComponent.Content(modifier = Modifier.align(Alignment.CenterHorizontally))
                state.filtersComponent.Content(modifier = Modifier)
                HorizontalDivider(modifier = Modifier.fillMaxWidth())
                state.listScreen.Content(modifier = Modifier)
            }

            AnimatedVisibility(
                visible = state.selectedComic != null,
                enter = slideInHorizontally(initialOffsetX = { it }),
                exit = slideOutHorizontally(targetOffsetX = { it }),
            ) {
                state.comicScreen.Content(modifier = Modifier)
            }
        }
    }
}
