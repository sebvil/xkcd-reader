package com.colibrez.xkcdreader.extensions

import app.cash.sqldelight.Query
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOne
import app.cash.sqldelight.coroutines.mapToOneNotNull
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.coroutines.CoroutineContext

fun <T : Any> Query<T>.getOne(coroutineContext: CoroutineContext): Flow<T> {
    return asFlow().mapToOneNotNull(coroutineContext)
}


fun <T : Any, R> Query<T>.getOne(coroutineContext: CoroutineContext, transform: (T) -> R): Flow<R> {
    return getOne(coroutineContext).map(transform)
}

fun <T : Any> Query<T>.getList(
    coroutineContext: CoroutineContext,
): Flow<List<T>> {
    return asFlow().mapToList(coroutineContext)
}


fun <T : Any, R> Query<T>.getList(
    coroutineContext: CoroutineContext,
    transform: (T) -> R
): Flow<List<R>> {
    return getList(coroutineContext).mapValues(transform)
}