package com.colibrez.xkcdreader.android.ui.features.comic

import app.cash.turbine.test
import com.colibrez.xkcdreader.android.extension.advanceUntilIdle
import com.colibrez.xkcdreader.android.ui.core.navigation.NavigationState
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

class ComicStateHolderTest : FreeSpec({
    lateinit var comicRepositoryDep: FakeComicRepository
    lateinit var subject: ComicStateHolder

    fun TestScope.getSubject(
        comicNumber: Long = DEFAULT_COMIC_NUMBER,
        comicTitle: String = ""
    ): ComicStateHolder {
        return ComicStateHolder(
            arguments = ComicScreenArguments(
                comicNumber = comicNumber,
                comicTitle = comicTitle,
            ),
            viewModelScope = this,
            comicRepository = comicRepositoryDep,
        )
    }

    beforeTest {
        comicRepositoryDep = FakeComicRepository()
    }

    "state comic properties match properties from repository comic" - {
        withData(nameFn = { "$it.number" }, comicFixtures) { comic ->
            subject = getSubject(comicNumber = comic.number, comicTitle = comic.title)
            subject.state.test {
                awaitItem() shouldBe ComicState.Loading(
                    comicNumber = comic.number,
                )
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
                )
            }
        }
    }

    "init marks comic as read" {
        subject = getSubject()
        advanceUntilIdle()
        comicRepositoryDep.markAsSeenInvocations shouldContainExactly listOf(
            FakeComicRepository.MarkAsSeenArgs(
                comicNum = DEFAULT_COMIC_NUMBER,
                userId = 0L,
            ),
        )
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
                        DEFAULT_COMIC_NUMBER,
                        isFavorite,
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
                awaitItem() shouldBe ComicState.Loading(
                    comicNumber = DEFAULT_COMIC_NUMBER,
                )
                advanceUntilIdle()
                awaitItem() shouldHaveShowDialogValueOf false
                subject.handle(ComicUserAction.ImageClicked)
                awaitItem() shouldHaveShowDialogValueOf true
                subject.handle(ComicUserAction.OverlayClicked)
                awaitItem() shouldHaveShowDialogValueOf false
            }
        }

        "BackButtonClicked sets navigation state to NavigateUp" {
            subject = getSubject()
            subject.navigationState.test {
                awaitItem() shouldBe null
                subject.handle(ComicUserAction.BackButtonClicked)
                awaitItem() shouldBe NavigationState.NavigateUp
            }
        }
    }
}) {
    companion object {
        private const val DEFAULT_COMIC_NUMBER = 1L
    }
}
