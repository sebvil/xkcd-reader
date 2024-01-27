package com.colibrez.xkcdreader.android.ui.core.mvvm

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import com.colibrez.xkcdreader.android.DependencyContainer
import kotlinx.coroutines.flow.StateFlow

@Stable
interface UiComponent<
    State : UiState,
    Action : UserAction,
    SH : StateHolder<State, Action>,
    Arguments : ComponentArguments,
    Props : ComponentProps,
    Delegate : ComponentDelegate
    > {

    fun createStateHolder(
        dependencyContainer: DependencyContainer,
        arguments: Arguments,
        props: StateFlow<Props>,
        delegate: Delegate
    ): SH

    @Composable
    fun Content(modifier: Modifier)

    fun onClear()
}
