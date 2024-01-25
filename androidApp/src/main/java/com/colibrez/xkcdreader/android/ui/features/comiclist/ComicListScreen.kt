package com.colibrez.xkcdreader.android.ui.features.comiclist

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.colibrez.xkcdreader.android.DependencyContainer
import com.colibrez.xkcdreader.android.ui.components.comic.ComicListItem
import com.colibrez.xkcdreader.android.ui.core.mvvm.BaseUiComponent
import com.colibrez.xkcdreader.android.ui.core.mvvm.componentScope
import com.colibrez.xkcdreader.android.ui.features.comic.ComicScreenArguments
import com.colibrez.xkcdreader.android.ui.features.comiclist.filters.FilterBar
import com.colibrez.xkcdreader.android.ui.features.comiclist.filters.FilterStateHolder
import com.colibrez.xkcdreader.android.ui.features.comiclist.search.SearchBar
import com.colibrez.xkcdreader.android.ui.features.comiclist.search.SearchStateHolder
import kotlinx.coroutines.CoroutineScope

@Composable
fun ComicListLayout(
    state: ComicListState,
    handleUserAction: (ComicListUserAction) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues()
) {
    when (state) {
        is ComicListState.Data -> {
            LazyColumn(modifier = modifier, contentPadding = contentPadding) {
                items(state.comics) { item ->
                    ComicListItem(
                        state = item,
                        onClick = {
                            handleUserAction(
                                ComicListUserAction.ComicClicked(
                                    comicNum = item.comicNumber,
                                    comicTitle = item.title,
                                ),
                            )
                        },
                        onToggleFavorite = {
                            handleUserAction(
                                ComicListUserAction.ToggleFavorite(
                                    comicNum = item.comicNumber,
                                    isFavorite = it,
                                ),
                            )
                        },
                    )
                }
            }
        }

        is ComicListState.Loading -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }
}

class ComicListScreen(
    private val showComic: (ComicScreenArguments) -> Unit,
    override val componentScope: CoroutineScope = componentScope()
) : BaseUiComponent<ComicListState, ComicListUserAction>() {

    private val filterStateHolder: FilterStateHolder = FilterStateHolder()

    private val searchStateHolder = SearchStateHolder(componentScope)

    override fun createStateHolder(dependencyContainer: DependencyContainer): ComicListStateHolder {
        return ComicListStateHolder(
            viewModelScope = componentScope,
            comicRepository = dependencyContainer.comicRepository,
            showComic = showComic,
            filterStateHolder = filterStateHolder,
            searchStateHolder = searchStateHolder,
        )
    }

    @Composable
    override fun Content(
        state: ComicListState,
        handle: (ComicListUserAction) -> Unit,
        modifier: Modifier
    ) {
        Column(modifier = modifier.fillMaxSize()) {
            SearchBar(
                searchStateHolder = searchStateHolder,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
            FilterBar(
                stateHolder = filterStateHolder,
                contentPadding = PaddingValues(horizontal = 16.dp),
            )
            Divider(modifier = Modifier.fillMaxWidth())
            ComicListLayout(state = state, handleUserAction = handle)
        }
    }
}
