package com.colibrez.xkcdreader.data.repository

import com.colibrez.xkcdreader.database.model.ComicEntity
import com.colibrez.xkcdreader.extensions.filterValues
import com.colibrez.xkcdreader.extensions.mapValues
import com.colibrez.xkcdreader.model.Comic
import com.colibrez.xkcdreader.model.comicFixtures
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull

class FakeComicRepository : ComicRepository {

    val comics = MutableStateFlow(comicFixtures)

    override fun getComic(num: Long): Flow<Comic> {
        return comics.mapNotNull { comics ->
            comics.find { it.number == num }
        }
    }

    override fun getLatest(): Flow<Comic> {
        TODO("Not yet implemented")
    }

    override fun getComicCount(): Flow<Long> {
        TODO("Not yet implemented")
    }

    override fun getAllComics(): Flow<List<Comic>> {
        TODO("Not yet implemented")
    }

    override fun getComicsPaged(
        next: Long,
        limit: Long
    ): Flow<List<Comic>> {
        TODO("Not yet implemented")
    }


    override fun getFavorites(): Flow<List<Comic>> {
        return comics.filterValues {
            it.isFavorite
        }
    }
    override suspend fun insertComics(comics: List<ComicEntity>) {
        TODO("Not yet implemented")
    }


    // region: markAsSeen
    data class MarkAsSeenArgs(val comicNum: Long, val userId: Long)

    val markAsSeenInvocations: MutableList<MarkAsSeenArgs> = mutableListOf()

    override suspend fun markAsSeen(comicNum: Long, userId: Long) {
        markAsSeenInvocations.add(MarkAsSeenArgs(comicNum, userId))
    }
    // endregion

    // region: toggleFavorites
    data class ToggleFavoriteArgs(val comicNum: Long, val isFavorite: Boolean, val userId: Long)

    val toggleFavoriteInvocations: MutableList<ToggleFavoriteArgs> = mutableListOf()

    override suspend fun toggleFavorite(comicNum: Long, isFavorite: Boolean, userId: Long) {
        toggleFavoriteInvocations.add(
            ToggleFavoriteArgs(
                comicNum = comicNum,
                isFavorite = isFavorite,
                userId = userId
            )
        )
    }
    // endregion
}