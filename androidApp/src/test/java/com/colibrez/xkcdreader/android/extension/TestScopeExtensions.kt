package com.colibrez.xkcdreader.android.extension

import io.kotest.core.test.TestScope
import io.kotest.core.test.testCoroutineScheduler
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalStdlibApi::class, ExperimentalCoroutinesApi::class)
fun TestScope.advanceUntilIdle() = testCoroutineScheduler.advanceUntilIdle()