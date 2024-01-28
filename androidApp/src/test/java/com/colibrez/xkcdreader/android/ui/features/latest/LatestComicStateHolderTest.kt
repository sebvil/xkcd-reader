package com.colibrez.xkcdreader.android.ui.features.latest

import app.cash.turbine.test
import com.colibrez.xkcdreader.android.extension.advanceUntilIdle
import com.colibrez.xkcdreader.android.ui.features.comic.ComicState
import com.colibrez.xkcdreader.android.ui.features.comic.ComicUserAction
import com.colibrez.xkcdreader.android.ui.features.comic.latest.LatestComicStateHolder
import com.colibrez.xkcdreader.data.repository.FakeComicRepository
import com.colibrez.xkcdreader.model.comicFixtures
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.test.TestScope
import io.kotest.datatest.withData
import io.kotest.matchers.Matcher
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.compose.all
import io.kotest.matchers.equalityMatcher
import io.kotest.matchers.reflection.havingProperty
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.beInstanceOf

class LatestComicStateHolderTest : FreeSpec({
    lateinit var comicRepositoryDep: FakeComicRepository
    lateinit var subject: LatestComicStateHolder

    fun TestScope.getSubject(): LatestComicStateHolder =
        LatestComicStateHolder(viewModelScope = this, comicRepository = comicRepositoryDep)

    beforeTest {
        comicRepositoryDep = FakeComicRepository()
    }

    "state" - {
        "is updated when latest comic is" - {
            withData(
                nameFn = { "comic #$it" },
                comicFixtures.indices.map { it + 1 },
            ) { comicNumber ->
                val comics = comicFixtures.subList(0, comicNumber)
                comicRepositoryDep.comics.value = comics
                val comic = comics.last()
                subject = getSubject()
                subject.state.test {
                    awaitItem() shouldBe ComicState.Loading(comicNumber = null, navigationState = null)
                    advanceUntilIdle()
                    awaitItem() shouldBe ComicState.Data(
                        comicNumber = comic.number,
                        comicTitle = comic.title,
                        imageUrl = comic.imageUrl,
                        altText = comic.altText,
                        imageDescription = comic.transcript,
                        permalink = comic.permalink,
                        explainXckdPermalink = comic.explainXkcdPermalink,
                        isFavorite = comic.isFavorite,
                        showDialog = false,
                        navigationState = null,
                    )
                }
            }
        }
    }

    "handle" - {
        "ToggleFavorite toggles comic favorite status" - {
            withData(listOf(true, false)) { isFavorite ->
                subject = getSubject()
                subject.handle(
                    ComicUserAction.ToggleFavorite(
                        comicNum = DEFAULT_COMIC_NUMBER,
                        isFavorite = isFavorite,
                    ),
                )
                advanceUntilIdle()
                comicRepositoryDep.toggleFavoriteInvocations shouldContainExactly listOf(
                    FakeComicRepository.ToggleFavoriteArgs(
                        comicNum = DEFAULT_COMIC_NUMBER,
                        isFavorite = isFavorite,
                        userId = 0,
                    ),
                )
            }
        }

        "ImageClicked and OverlayClicked update showDialog state" {
            infix fun <T : ComicState> T.shouldHaveShowDialogValueOf(expected: Boolean): T {
                this shouldBe Matcher.all(
                    beInstanceOf<ComicState.Data>(),
                    havingProperty(
                        equalityMatcher(expected) to ComicState.Data::showDialog,
                    ),
                )
                return this
            }
            subject = getSubject()
            subject.state.test {
                awaitItem() shouldBe ComicState.Loading(comicNumber = null, navigationState = null)
                advanceUntilIdle()
                awaitItem() shouldHaveShowDialogValueOf false
                subject.handle(ComicUserAction.ImageClicked)
                awaitItem() shouldHaveShowDialogValueOf true
                subject.handle(ComicUserAction.OverlayClicked)
                awaitItem() shouldHaveShowDialogValueOf false
            }
        }
    }
}) {
    companion object {
        private const val DEFAULT_COMIC_NUMBER = 1L
    }
}
