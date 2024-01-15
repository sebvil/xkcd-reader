package com.colibrez.xkcdreader.android.ui.features.comiclist.all

import app.cash.turbine.test
import com.colibrez.xkcdreader.android.extension.advanceUntilIdle
import com.colibrez.xkcdreader.android.ui.components.comic.ListComic
import com.colibrez.xkcdreader.android.ui.core.mvvm.FakeStateHolder
import com.colibrez.xkcdreader.android.ui.features.comiclist.ComicListState
import com.colibrez.xkcdreader.android.ui.features.comiclist.all.filters.EnumFilterState
import com.colibrez.xkcdreader.android.ui.features.comiclist.all.filters.FilterUserAction
import com.colibrez.xkcdreader.android.ui.features.comiclist.all.filters.FiltersState
import com.colibrez.xkcdreader.android.ui.features.comiclist.all.filters.ReadFilter
import com.colibrez.xkcdreader.data.repository.FakeComicRepository
import com.colibrez.xkcdreader.model.comicFixtures
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.test.TestScope
import io.kotest.matchers.shouldBe
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.update

class AllComicsStateHolderTest : FreeSpec({
    lateinit var comicRepositoryDep: FakeComicRepository
    lateinit var filterStateHolderDep: FakeStateHolder<FiltersState, FilterUserAction>
    lateinit var subject: AllComicsStateHolder

    fun TestScope.getSubject() = AllComicsStateHolder(
        viewModelScope = this,
        filterStateHolder = filterStateHolderDep,
        comicRepository = comicRepositoryDep,
    )

    beforeTest {
        comicRepositoryDep = FakeComicRepository()
        filterStateHolderDep =
            FakeStateHolder(FiltersState(isReadFilter = EnumFilterState(selection = ReadFilter.All)))
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

        "gets updated read filter changes" {
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
                        isReadFilter = EnumFilterState(
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
                        isReadFilter = EnumFilterState(
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
                        isReadFilter = EnumFilterState(
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
    }
})
