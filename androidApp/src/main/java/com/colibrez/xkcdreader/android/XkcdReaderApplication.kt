package com.colibrez.xkcdreader.android

import android.app.Application
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.colibrez.xkcdreader.android.network.PrefetchDataWorker
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

        val fetchDataRequest = OneTimeWorkRequestBuilder<PrefetchDataWorker>().build()

        WorkManager.getInstance(this).enqueue(fetchDataRequest)
    }
}
