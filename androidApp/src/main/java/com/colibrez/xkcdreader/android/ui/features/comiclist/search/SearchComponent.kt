package com.colibrez.xkcdreader.android.ui.features.comiclist.search

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.colibrez.xkcdreader.android.DependencyContainer
import com.colibrez.xkcdreader.android.ui.core.mvvm.BaseUiComponent
import com.colibrez.xkcdreader.android.ui.core.mvvm.Handler
import com.colibrez.xkcdreader.android.ui.core.mvvm.NoArguments
import com.colibrez.xkcdreader.android.ui.core.mvvm.NoProps
import com.colibrez.xkcdreader.android.ui.core.mvvm.NoState
import com.colibrez.xkcdreader.android.ui.core.mvvm.componentScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SearchComponent(
    delegate: SearchDelegate,
    override val componentScope: CoroutineScope = componentScope()
) : BaseUiComponent<NoState, SearchUserAction, SearchStateHolder, NoArguments, NoProps, SearchDelegate>(
    arguments = NoArguments,
    delegate = delegate,
    props = MutableStateFlow(NoProps),
) {
    override fun createStateHolder(
        dependencyContainer: DependencyContainer,
        arguments: NoArguments,
        props: StateFlow<NoProps>,
        delegate: SearchDelegate
    ): SearchStateHolder {
        return SearchStateHolder(delegate = delegate, stateHolderScope = componentScope)
    }

    @Composable
    override fun Content(
        state: NoState,
        handle: Handler<SearchUserAction>,
        modifier: Modifier
    ) {
        SearchBar(handle = handle, modifier = modifier)
    }
}
