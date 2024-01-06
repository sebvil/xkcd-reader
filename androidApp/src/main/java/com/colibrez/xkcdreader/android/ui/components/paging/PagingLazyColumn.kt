package com.colibrez.xkcdreader.android.ui.components.paging

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.colibrez.xkcdreader.android.data.repository.paging.PagingStatus

@Composable
fun <T> PagingLazyColumn(
    stateHolder: PagingStateHolder<T, *>,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    reverseLayout: Boolean = false,
    verticalArrangement: Arrangement.Vertical =
        if (!reverseLayout) Arrangement.Top else Arrangement.Bottom,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    flingBehavior: FlingBehavior = ScrollableDefaults.flingBehavior(),
    userScrollEnabled: Boolean = true,
    itemKey: ((T) -> Any)? = null,
    contentType: (T) -> Any? = { null },
    itemRow: @Composable LazyItemScope.(T) -> Unit
) {
    val state by stateHolder.state.collectAsState()

    val isNearEnd by remember {
        derivedStateOf {
            val lastItemIndex = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
                ?: return@derivedStateOf true
            lastItemIndex > state.items.size - stateHolder.pageSize * 2
        }
    }

    LaunchedEffect(key1 = isNearEnd, key2 = state.items.size) {
        if (isNearEnd && state.status.canFetchData()) {
            stateHolder.handle(PagingUserAction.FetchPage(isInitialFetch = state.items.isEmpty()))
        }
    }

    LazyColumn(
        modifier = modifier,
        state = listState,
        contentPadding = contentPadding,
        reverseLayout = reverseLayout,
        verticalArrangement = verticalArrangement,
        horizontalAlignment = horizontalAlignment,
        flingBehavior = flingBehavior,
        userScrollEnabled = userScrollEnabled
    ) {
        items(items = state.items, key = itemKey, contentType = contentType, itemRow)

        when (val status = state.status) {
            is PagingStatus.Idle -> Unit
            PagingStatus.Loading -> {
                item {
                    LaunchedEffect(key1 = Unit) {
                        listState.animateScrollToItem(state.items.size)
                    }
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            }

            is PagingStatus.NetworkError -> {
                item {
                    LaunchedEffect(key1 = Unit) {
                        listState.animateScrollToItem(state.items.size)
                    }
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = status.message)
                        Button(onClick = {
                            stateHolder.handle(
                                PagingUserAction.FetchPage(
                                    isInitialFetch = false
                                )
                            )
                        }) {
                            Text(text = "Try again")
                        }
                    }
                }

            }
        }
    }
}