package com.colibrez.xkcdreader.extensions

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn

inline fun <T, R> StateFlow<T>.mapAsStateFlow(
    scope: CoroutineScope,
    started: SharingStarted = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
    crossinline transform: (T) -> R
) = map(transform).stateIn(scope, started, transform(value))