package com.colibrez.xkcdreader.android.ui.core.mvvm

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

fun componentScope(): CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
