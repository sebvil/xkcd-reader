package com.colibrez.xkcdreader.android.ui.features.comiclist

import app.cash.turbine.test
import com.colibrez.xkcdreader.android.extension.advanceUntilIdle
import com.colibrez.xkcdreader.android.ui.core.navigation.NavigationState
import com.colibrez.xkcdreader.android.ui.features.comic.ComicScreenArguments
import com.colibrez.xkcdreader.data.repository.FakeComicRepository
import com.colibrez.xkcdreader.model.Comic
import com.colibrez.xkcdreader.model.comicFixtures
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.test.TestScope
import io.kotest.datatest.withData
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

class ComicListStateHolderTest : FreeSpec({
    lateinit var comicRepositoryDep: FakeComicRepository
    lateinit var subject: ComicListStateHolder

    fun TestScope.getSubject() = object : ComicListStateHolder(
        viewModelScope = this@getSubject,
        comicRepository = comicRepositoryDep,
    ) {
        override val comicsFlow: Flow<List<Comic>> = emptyFlow()
    }

    beforeTest {
        comicRepositoryDep = FakeComicRepository()
    }

    "handle" - {
        "ToggleFavorite toggles comic favorite status" - {
            withData(
                nameFn = { "for comic #${it.first} when it is ${if (it.second) "favorite" else "not favorite"}" },
                listOf(
                    1L to true,
                    1L to false,
                    2L to true,
                    2L to false,
                ),
            ) { (comicNumber, isFavorite) ->
                subject = getSubject()
                subject.handle(
                    ComicListUserAction.ToggleFavorite(
                        comicNum = comicNumber,
                        isFavorite = isFavorite,
                    ),
                )
                advanceUntilIdle()
                comicRepositoryDep.toggleFavoriteInvocations shouldContainExactly listOf(
                    FakeComicRepository.ToggleFavoriteArgs(
                        comicNum = comicNumber,
                        isFavorite = isFavorite,
                        userId = 0,
                    ),
                )
            }
        }

        "ComicClicked shows comic screen for comic" - {
            withData(nameFn = { "${it.number}. ${it.title}" }, comicFixtures) { comic ->
                subject = getSubject()
                subject.navigationState.test {
                    awaitItem() shouldBe null
                    subject.handle(ComicListUserAction.ComicClicked(comic.number, comic.title))
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
