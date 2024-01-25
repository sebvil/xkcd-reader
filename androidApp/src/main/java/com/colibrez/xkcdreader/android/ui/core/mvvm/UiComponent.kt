package com.colibrez.xkcdreader.android.ui.core.mvvm

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.colibrez.xkcdreader.android.DependencyContainer
import com.colibrez.xkcdreader.android.XkcdReaderApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import org.jetbrains.annotations.MustBeInvokedByOverriders

@Stable
interface UiComponent<State : UiState, Action : UserAction> {

    fun createStateHolder(dependencyContainer: DependencyContainer): StateHolder<State, Action>

    @Composable
    fun Content(modifier: Modifier)

    fun onClear()
}

abstract class BaseUiComponent<State : UiState, Action : UserAction> : UiComponent<State, Action> {

    protected abstract val componentScope: CoroutineScope

    private var stateHolder: StateHolder<State, Action>? = null
    private fun getOrCreateStateHolder(dependencyContainer: DependencyContainer): StateHolder<State, Action> {
        return stateHolder ?: createStateHolder(dependencyContainer).also {
            stateHolder = it
        }
    }

    @Composable
    final override fun Content(modifier: Modifier) {
        key(this::class) {
            val dependencyContainer =
                (LocalContext.current.applicationContext as XkcdReaderApplication).dependencyContainer
            val stateHolder = remember {
                getOrCreateStateHolder(dependencyContainer)
            }
            val state by stateHolder.state.collectAsState()
            Content(state = state, handle = stateHolder::handle, modifier = modifier)
        }
    }

    @Composable
    protected abstract fun Content(state: State, handle: (Action) -> Unit, modifier: Modifier)

    @MustBeInvokedByOverriders
    override fun onClear() {
        componentScope.cancel()
    }
}

fun componentScope(): CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
