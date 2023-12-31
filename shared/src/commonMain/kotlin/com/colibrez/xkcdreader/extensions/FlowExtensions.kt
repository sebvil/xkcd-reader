package com.colibrez.xkcdreader.extensions

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

fun <T, R> Flow<List<T>>.mapValues(transform: (T) -> R): Flow<List<R>> {
    return this.map {
        it.map(transform)
    }
}