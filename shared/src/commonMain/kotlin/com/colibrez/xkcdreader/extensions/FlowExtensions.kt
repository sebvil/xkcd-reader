package com.colibrez.xkcdreader.extensions

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

fun <T, R> Flow<List<T>>.mapValues(transform: (T) -> R): Flow<List<R>> {
    return this.map {
        it.map(transform)
    }
}

fun <T> Flow<T>.withDefault(value: T) = onStart { emit(value) }