package com.colibrez.xkcdreader.android.ui.features.comiclist

import app.cash.turbine.test
import com.colibrez.xkcdreader.android.extension.advanceUntilIdle
import com.colibrez.xkcdreader.android.ui.components.comic.ListComic
import com.colibrez.xkcdreader.data.repository.FakeComicRepository
import com.colibrez.xkcdreader.model.comicFixtures
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.test.TestScope
import io.kotest.datatest.withData
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class ComicListStateHolderTest : FreeSpec({
    lateinit var comicRepositoryDep: FakeComicRepository
    lateinit var showComicInvocations: MutableList<Long>
    lateinit var onShownComicsChangedInvocations: MutableList<List<Long>>

    lateinit var subject: ComicListStateHolder
    lateinit var props: MutableStateFlow<ComicListProps>

    fun TestScope.getSubject() = ComicListStateHolder(
        props = props,
        viewModelScope = this,
        comicRepository = comicRepositoryDep,
        delegate = object : ComicListDelegate {
            override fun onComicSelected(comicNumber: Long) {
                showComicInvocations.add(comicNumber)
            }

            override fun onShownComicsChanged(shownComics: List<Long>) {
                onShownComicsChangedInvocations.add(shownComics)
            }
        },
    )

    beforeTest {
        comicRepositoryDep = FakeComicRepository()
        props = MutableStateFlow(
            ComicListProps(
                isUnreadFilterApplied = false,
                isFavoriteFilterApplied = false,
                searchQuery = "",
            ),
        )
        showComicInvocations = mutableListOf()
        onShownComicsChangedInvocations = mutableListOf()
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
                        .toImmutableList(),
                )
            }
        }

        "gets updated when search query changes" {
            subject = getSubject()
            subject.state.test {
                awaitItem() shouldBe ComicListState.Loading
                advanceUntilIdle()
                awaitItem() shouldBe ComicListState.Data(
                    comics = comicFixtures
                        .map { ListComic.fromExternalModel(it) }
                        .toImmutableList(),
                )

                // Our fake implementation only matches on comic number
                props.update {
                    it.copy(searchQuery = "1")
                }

                awaitItem() shouldBe ComicListState.Data(
                    comics = comicFixtures.subList(0, 1)
                        .map { ListComic.fromExternalModel(it) }
                        .toImmutableList(),
                )

                props.update {
                    it.copy(searchQuery = "")
                }

                awaitItem() shouldBe ComicListState.Data(
                    comics = comicFixtures
                        .map { ListComic.fromExternalModel(it) }
                        .toImmutableList(),
                )
            }
        }

        "gets updated when read filter changes" {
            subject = getSubject()
            subject.state.test {
                awaitItem() shouldBe ComicListState.Loading
                advanceUntilIdle()
                awaitItem() shouldBe ComicListState.Data(
                    comics = comicFixtures
                        .map { ListComic.fromExternalModel(it) }
                        .toImmutableList(),
                )

                props.update {
                    it.copy(isUnreadFilterApplied = true)
                }

                awaitItem() shouldBe ComicListState.Data(
                    comics = comicFixtures
                        .filter { !it.isRead }
                        .map { ListComic.fromExternalModel(it) }
                        .toImmutableList(),
                )

                props.update {
                    it.copy(
                        isUnreadFilterApplied = false,
                    )
                }
                awaitItem() shouldBe ComicListState.Data(
                    comics = comicFixtures
                        .map { ListComic.fromExternalModel(it) }
                        .toImmutableList(),
                )
            }
        }

        "gets updated when favorite filter changes" {
            subject = getSubject()
            subject.state.test {
                awaitItem() shouldBe ComicListState.Loading
                advanceUntilIdle()
                awaitItem() shouldBe ComicListState.Data(
                    comics = comicFixtures
                        .map { ListComic.fromExternalModel(it) }
                        .toImmutableList(),
                )

                props.update {
                    it.copy(isFavoriteFilterApplied = true)
                }
                awaitItem() shouldBe ComicListState.Data(
                    comics = comicFixtures
                        .filter { it.isFavorite }
                        .map { ListComic.fromExternalModel(it) }
                        .toImmutableList(),
                )

                props.update {
                    it.copy(isFavoriteFilterApplied = false)
                }
                awaitItem() shouldBe ComicListState.Data(
                    comics = comicFixtures
                        .map { ListComic.fromExternalModel(it) }
                        .toImmutableList(),
                )
            }
        }
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
                showComicInvocations shouldHaveSize 0
                subject.handle(ComicListUserAction.ComicClicked(comic.number, comic.title))
                showComicInvocations shouldContainExactly listOf(comic.number)
            }
        }
    }
})
