package com.colibrez.xkcdreader.android.ui.features.comiclist.all

import app.cash.turbine.test
import com.colibrez.xkcdreader.android.extension.advanceUntilIdle
import com.colibrez.xkcdreader.android.ui.components.comic.ListComic
import com.colibrez.xkcdreader.android.ui.features.comiclist.ComicListState
import com.colibrez.xkcdreader.data.repository.FakeComicRepository
import com.colibrez.xkcdreader.model.comicFixtures
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.test.TestScope
import io.kotest.matchers.shouldBe
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

class AllComicsStateHolderTest : FreeSpec({
    lateinit var comicRepositoryDep: FakeComicRepository
    lateinit var subject: AllComicsStateHolder

    fun TestScope.getSubject() = AllComicsStateHolder(
        viewModelScope = this,
        comicRepository = comicRepositoryDep,
    )

    beforeTest {
        comicRepositoryDep = FakeComicRepository()
    }

    "state" - {
        "gets updated when all comics change" {
            comicRepositoryDep.comics.value = listOf()
            subject = getSubject()
            subject.state.test {
                awaitItem() shouldBe ComicListState.Loading
                advanceUntilIdle()
                awaitItem() shouldBe ComicListState.Data(comics = persistentListOf())

                comicRepositoryDep.comics.value = comicFixtures.subList(0, 1)
                awaitItem() shouldBe ComicListState.Data(
                    comics = persistentListOf(
                        ListComic.fromExternalModel(
                            comicFixtures[0],
                        ),
                    ),
                )
                comicRepositoryDep.comics.value = comicFixtures
                awaitItem() shouldBe ComicListState.Data(
                    comics = comicFixtures
                        .map { ListComic.fromExternalModel(it) }
                        .sortedByDescending { it.comicNumber }
                        .toImmutableList(),
                )
            }
        }
    }
})
