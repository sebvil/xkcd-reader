package com.colibrez.xkcdreader.android.ui.core.mvvm

import androidx.lifecycle.ViewModel

abstract class BaseViewModel<State : UiState, Action : UserAction> : StateHolder<State, Action>, ViewModel()