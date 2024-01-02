package com.colibrez.xkcdreader.android.ui.core.mvvm

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.NavBackStackEntry
import androidx.savedstate.SavedStateRegistryOwner
import com.colibrez.xkcdreader.android.ui.features.navArgs

abstract class BaseViewModelFactory<VM : BaseViewModel<*, *>>(owner: SavedStateRegistryOwner) :
    AbstractSavedStateViewModelFactory(
        owner,
        defaultArgs = (owner as? NavBackStackEntry)?.arguments
    ) {

    abstract fun create(key: String, handle: SavedStateHandle): VM
    @Suppress("UNCHECKED_CAST")
    final override fun <T : ViewModel> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        return create(key, handle) as T
    }
}
