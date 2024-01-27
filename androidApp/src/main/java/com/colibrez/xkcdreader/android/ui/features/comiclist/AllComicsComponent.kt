package com.colibrez.xkcdreader.android.ui.features.comiclist

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.colibrez.xkcdreader.android.DependencyContainer
import com.colibrez.xkcdreader.android.ui.core.mvvm.BaseUiComponent
import com.colibrez.xkcdreader.android.ui.core.mvvm.Handler
import com.colibrez.xkcdreader.android.ui.core.mvvm.NoArguments
import com.colibrez.xkcdreader.android.ui.core.mvvm.NoDelegate
import com.colibrez.xkcdreader.android.ui.core.mvvm.NoProps
import com.colibrez.xkcdreader.android.ui.core.mvvm.componentScope
import com.colibrez.xkcdreader.android.ui.features.comic.ComicComponent
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
        BackHandler(enabled = state.comicScreen != null) {
            handle(AllComicsAction.HideComic)
        }
        val comicVisibilityState = remember {
            MutableTransitionState(state.comicScreen != null)
        }

        var comicScreen: ComicComponent? by remember {
            mutableStateOf(state.comicScreen)
        }

        LaunchedEffect(key1 = state.comicScreen) {
            state.comicScreen?.also {
                comicScreen = it
                comicVisibilityState.targetState = true
            } ?: run {
                comicVisibilityState.targetState = false
            }
        }

        LaunchedEffect(key1 = comicVisibilityState.currentState) {
            if (!comicVisibilityState.currentState && comicVisibilityState.isIdle) {
                comicScreen = null
            }
        }

        Box(modifier = modifier) {
            Column(modifier = Modifier.fillMaxSize()) {
                state.searchComponent.Content(modifier = Modifier.align(Alignment.CenterHorizontally))
                state.filtersComponent.Content(modifier = Modifier)
                HorizontalDivider(modifier = Modifier.fillMaxWidth())
                state.listScreen.Content(modifier = Modifier)
            }

            AnimatedVisibility(
                visibleState = comicVisibilityState,
                enter = slideInHorizontally(initialOffsetX = { it }),
                exit = slideOutHorizontally(targetOffsetX = { it }),
            ) {
                comicScreen?.Content(modifier = Modifier)
            }
        }
    }
}
