package com.colibrez.xkcdreader.android

import android.app.Application

class XkcdReaderApplication : Application() {

    lateinit var dependencyContainer: DependencyContainer
    override fun onCreate() {
        super.onCreate()
        dependencyContainer = DependencyContainer(this)
    }
}