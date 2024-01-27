package com.colibrez.xkcdreader.android.ui.core.mvvm

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.colibrez.xkcdreader.android.DependencyContainer
import com.colibrez.xkcdreader.android.XkcdReaderApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.StateFlow
import org.jetbrains.annotations.MustBeInvokedByOverriders

abstract class BaseUiComponent<
    State : UiState,
    Action : UserAction,
    SH : StateHolder<State, Action>,
    Arguments : ComponentArguments,
    Props : ComponentProps,
    Delegate : ComponentDelegate
    >(
    private val arguments: Arguments,
    private val delegate: Delegate,
    private val props: StateFlow<Props>,
) :
    UiComponent<State, Action, SH, Arguments, Props, Delegate> {

    protected abstract val componentScope: CoroutineScope

    private var stateHolder: SH? = null

    @Composable
    protected abstract fun Content(state: State, handle: Handler<Action>, modifier: Modifier)

    private fun getOrCreateStateHolder(dependencyContainer: DependencyContainer): SH {
        return stateHolder ?: createStateHolder(
            dependencyContainer = dependencyContainer,
            arguments = arguments,
            props = props,
            delegate = delegate,
        ).also {
            stateHolder = it
        }
    }

    @Composable
    final override fun Content(modifier: Modifier) {
        key(this::class, arguments) {
            val dependencyContainer =
                (LocalContext.current.applicationContext as XkcdReaderApplication).dependencyContainer
            val stateHolder = remember {
                getOrCreateStateHolder(dependencyContainer)
            }
            val state by stateHolder.state.collectAsState()
            Content(state = state, handle = stateHolder::handle, modifier = modifier)
        }
    }

    @MustBeInvokedByOverriders
    override fun onClear() {
        componentScope.cancel()
    }
}
