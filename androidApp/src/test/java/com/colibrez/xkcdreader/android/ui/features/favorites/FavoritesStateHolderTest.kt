package com.colibrez.xkcdreader.android.ui.features.favorites

import app.cash.turbine.test
import com.colibrez.xkcdreader.android.extension.advanceUntilIdle
import com.colibrez.xkcdreader.android.ui.components.comic.ListComic
import com.colibrez.xkcdreader.android.ui.core.navigation.NavigationState
import com.colibrez.xkcdreader.android.ui.features.comic.ComicScreenArguments
import com.colibrez.xkcdreader.data.repository.FakeComicRepository
import com.colibrez.xkcdreader.model.comicFixtures
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.test.TestScope
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.update

class FavoritesStateHolderTest : FreeSpec({
    lateinit var comicRepositoryDep: FakeComicRepository
    lateinit var subject: FavoritesStateHolder

    fun TestScope.getSubject() = FavoritesStateHolder(
        viewModelScope = this,
        comicRepository = comicRepositoryDep,
    )

    beforeTest {
        comicRepositoryDep = FakeComicRepository()
    }

    "state" - {
        "gets updated when favorites change" {
            comicRepositoryDep.comics.update { comics ->
                comics.map { it.copy(isFavorite = false) }
            }
            subject = getSubject()
            subject.state.test {
                awaitItem() shouldBe FavoritesState.Loading
                advanceUntilIdle()
                awaitItem() shouldBe FavoritesState.Data(comics = persistentListOf())
                comicRepositoryDep.comics.update { comics ->
                    comics.toMutableList().apply {
                        set(0, get(0).copy(isFavorite = true))
                    }
                }
                awaitItem() shouldBe FavoritesState.Data(
                    comics = persistentListOf(
                        ListComic.fromExternalModel(
                            comicFixtures[0],
                        ),
                    ),
                )
                comicRepositoryDep.comics.update { comics ->
                    comics.map { it.copy(isFavorite = true) }
                }
                awaitItem() shouldBe FavoritesState.Data(
                    comics = comicFixtures.map { ListComic.fromExternalModel(it) }.toImmutableList(),
                )
            }
        }
    }

    "handle" - {
        "ComicClicked shows comic screen for comic" - {
            withData(nameFn = { "${it.number}. ${it.title}" }, comicFixtures) { comic ->
                subject = getSubject()
                subject.navigationState.test {
                    awaitItem() shouldBe null
                    subject.handle(FavoritesUserAction.ComicClicked(comic.number, comic.title))
                    awaitItem() shouldBe NavigationState.ShowScreen(
                        ComicScreenArguments(
                            comicNumber = comic.number,
                            comicTitle = comic.title,
                        ),
                    )
                }
            }
        }
    }
})