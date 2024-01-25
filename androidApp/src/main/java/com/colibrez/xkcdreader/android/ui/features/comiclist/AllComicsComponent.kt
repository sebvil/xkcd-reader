package com.colibrez.xkcdreader.android.ui.features.comiclist

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.colibrez.xkcdreader.android.DependencyContainer
import com.colibrez.xkcdreader.android.ui.core.mvvm.BaseUiComponent
import com.colibrez.xkcdreader.android.ui.core.mvvm.componentScope
import com.colibrez.xkcdreader.android.ui.features.comic.ComicScreen
import kotlinx.coroutines.CoroutineScope

class AllComicsComponent(override val componentScope: CoroutineScope = componentScope()) :
    BaseUiComponent<AllComicsState, AllComicsAction>() {

    override fun createStateHolder(dependencyContainer: DependencyContainer): AllComicsStateHolder {
        return AllComicsStateHolder()
    }

    @Composable
    override fun Content(
        state: AllComicsState,
        handle: (AllComicsAction) -> Unit,
        modifier: Modifier
    ) {
        BackHandler(enabled = state.comicScreen != null) {
            handle(AllComicsAction.HideComic)
        }
        val comicVisibilityState = remember {
            MutableTransitionState(state.comicScreen != null)
        }

        var comicScreen: ComicScreen? by remember {
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
            state.listScreen.Content(modifier = Modifier)

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
