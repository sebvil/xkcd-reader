package com.colibrez.xkcdreader.android.ui.core.navigation

import com.colibrez.xkcdreader.android.ui.features.destinations.Destination
import com.colibrez.xkcdreader.android.ui.features.destinations.TypedDestination
import com.ramcosta.composedestinations.spec.Direction

interface ScreenArguments<T>  {

    val direction: Direction
}
