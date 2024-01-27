package com.colibrez.xkcdreader.android.ui.features.comiclist.filters

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.colibrez.xkcdreader.android.DependencyContainer
import com.colibrez.xkcdreader.android.ui.core.mvvm.BaseUiComponent
import com.colibrez.xkcdreader.android.ui.core.mvvm.NoArguments
import com.colibrez.xkcdreader.android.ui.core.mvvm.NoProps
import com.colibrez.xkcdreader.android.ui.core.mvvm.componentScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FiltersComponent(
    override val componentScope: CoroutineScope = componentScope(),
    delegate: FiltersDelegate
) :
    BaseUiComponent<FiltersState, FilterUserAction, FilterStateHolder, NoArguments, NoProps, FiltersDelegate>(
        arguments = NoArguments,
        delegate = delegate,
        props = MutableStateFlow(NoProps),
    ) {

    @Composable
    override fun Content(
        state: FiltersState,
        handle: (FilterUserAction) -> Unit,
        modifier: Modifier
    ) {
        FilterBar(
            state = state,
            handle = handle,
            modifier = modifier,
            contentPadding = PaddingValues(horizontal = 16.dp),
        )
    }

    override fun createStateHolder(
        dependencyContainer: DependencyContainer,
        arguments: NoArguments,
        props: StateFlow<NoProps>,
        delegate: FiltersDelegate
    ): FilterStateHolder {
        return FilterStateHolder(delegate = delegate)
    }
}
