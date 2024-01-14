package com.colibrez.xkcdreader.android.network

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.colibrez.xkcdreader.android.XkcdReaderApplication
import com.colibrez.xkcdreader.data.model.asEntity
import com.colibrez.xkcdreader.data.repository.ComicRepository
import com.colibrez.xkcdreader.network.ApiClient
import com.colibrez.xkcdreader.network.model.NetworkComic

class PrefetchDataWorker(
    private val appContext: Context,
    workerParams: WorkerParameters,
) : CoroutineWorker(appContext, workerParams) {

    // TODO figure out better DI
    private val comicRepository: ComicRepository =
        (appContext as XkcdReaderApplication).dependencyContainer.comicRepository
    private val apiClient: ApiClient =
        (appContext as XkcdReaderApplication).dependencyContainer.apiClient

    // TODO FGS for < Android 12

    override suspend fun doWork(): Result {
        val latestFetchTimestamp = comicRepository.latestUpdateTimestamp()
        val oldestFetchedComic = apiClient.getNewestComics(latestFetchTimestamp, limit = 100L).map {
            comicRepository.insertComics(it.map(NetworkComic::asEntity))
            it.first().num
        }

        oldestFetchedComic.fold(
            onSuccess = { maxComicNumber ->
                apiClient.getNewestComics(
                    lastFetchTimestamp = latestFetchTimestamp,
                    maxComicNumber = maxComicNumber,
                ).onSuccess {
                    comicRepository.insertComics(it.map(NetworkComic::asEntity))
                }
            },
            onFailure = {
                kotlin.Result.failure(it)
            },
        )

        // Indicate whether the work finished successfully with the Result
        return Result.success()
    }
}
