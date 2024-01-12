package com.colibrez.xkcdreader.android

import android.app.Application
import kotlinx.coroutines.Dispatchers

@Suppress("InjectDispatcher")
class XkcdReaderApplication : Application() {

    lateinit var dependencyContainer: DependencyContainer
    override fun onCreate() {
        super.onCreate()
        dependencyContainer = DependencyContainer(
            applicationContext = this,
            ioDispatcher = Dispatchers.IO,
        )
    }
}
