package com.colibrez.xkcdreader.android.ui.features.comiclist

import app.cash.turbine.test
import com.colibrez.xkcdreader.android.extension.advanceUntilIdle
import com.colibrez.xkcdreader.android.ui.components.comic.ListComic
import com.colibrez.xkcdreader.android.ui.core.mvvm.FakeStateHolder
import com.colibrez.xkcdreader.android.ui.core.navigation.NavigationState
import com.colibrez.xkcdreader.android.ui.features.comic.ComicScreenArguments
import com.colibrez.xkcdreader.android.ui.features.comiclist.filters.FavoriteFilter
import com.colibrez.xkcdreader.android.ui.features.comiclist.filters.Filter
import com.colibrez.xkcdreader.android.ui.features.comiclist.filters.FilterUserAction
import com.colibrez.xkcdreader.android.ui.features.comiclist.filters.FiltersState
import com.colibrez.xkcdreader.android.ui.features.comiclist.filters.ReadFilter
import com.colibrez.xkcdreader.data.repository.FakeComicRepository
import com.colibrez.xkcdreader.model.comicFixtures
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.test.TestScope
import io.kotest.datatest.withData
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.update

class ComicListStateHolderTest : FreeSpec({
    lateinit var comicRepositoryDep: FakeComicRepository
    lateinit var filterStateHolderDep: FakeStateHolder<FiltersState, FilterUserAction>
    lateinit var subject: ComicListStateHolder

    fun TestScope.getSubject() = ComicListStateHolder(
        viewModelScope = this,
        filterStateHolder = filterStateHolderDep,
        comicRepository = comicRepositoryDep,
    )

    beforeTest {
        comicRepositoryDep = FakeComicRepository()
        filterStateHolderDep =
            FakeStateHolder(
                FiltersState(
                    isReadFilter = Filter.IsRead(selection = ReadFilter.All),
                    favoriteFilter = Filter.Favorite(selection = FavoriteFilter.All),
                ),
            )
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

                filterStateHolderDep.stateFlow.update {
                    it.copy(
                        isReadFilter = Filter.IsRead(
                            selection = ReadFilter.Read,
                        ),
                    )
                }
                awaitItem() shouldBe ComicListState.Data(
                    comics = comicFixtures
                        .filter { it.isRead }
                        .map { ListComic.fromExternalModel(it) }
                        .toImmutableList(),
                )

                filterStateHolderDep.stateFlow.update {
                    it.copy(
                        isReadFilter = Filter.IsRead(
                            selection = ReadFilter.Unread,
                        ),
                    )
                }
                awaitItem() shouldBe ComicListState.Data(
                    comics = comicFixtures
                        .filter { !it.isRead }
                        .map { ListComic.fromExternalModel(it) }
                        .toImmutableList(),
                )

                filterStateHolderDep.stateFlow.update {
                    it.copy(
                        isReadFilter = Filter.IsRead(
                            selection = ReadFilter.All,
                        ),
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

                filterStateHolderDep.stateFlow.update {
                    it.copy(
                        favoriteFilter = Filter.Favorite(
                            selection = FavoriteFilter.Favorites,
                        ),
                    )
                }
                awaitItem() shouldBe ComicListState.Data(
                    comics = comicFixtures
                        .filter { it.isFavorite }
                        .map { ListComic.fromExternalModel(it) }
                        .toImmutableList(),
                )

                filterStateHolderDep.stateFlow.update {
                    it.copy(
                        favoriteFilter = Filter.Favorite(
                            selection = FavoriteFilter.All,
                        ),
                    )
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
