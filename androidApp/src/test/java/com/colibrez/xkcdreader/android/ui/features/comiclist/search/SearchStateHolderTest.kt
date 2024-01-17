package com.colibrez.xkcdreader.android.ui.features.comiclist.search

import app.cash.turbine.test
import com.colibrez.xkcdreader.android.ui.components.comic.ListComic
import com.colibrez.xkcdreader.data.repository.FakeSearchRepository
import com.colibrez.xkcdreader.model.comicFixtures
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.test.TestScope
import io.kotest.matchers.shouldBe
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

class SearchStateHolderTest : FreeSpec({
    lateinit var searchRepositoryDep: FakeSearchRepository
    lateinit var subject: SearchStateHolder

    fun TestScope.getSubject(): SearchStateHolder =
        SearchStateHolder(viewModelScope = this, searchRepository = searchRepositoryDep)

    beforeTest {
        searchRepositoryDep = FakeSearchRepository()
    }

    "handle" - {
        "QuerySubmitted and SearchCleared update state" {
            subject = getSubject()
            subject.state.test {
                awaitItem() shouldBe SearchState(results = persistentListOf())
                subject.handle(SearchUserAction.QuerySubmitted("a"))
                awaitItem() shouldBe SearchState(
                    results = comicFixtures.map {
                        ListComic.fromExternalModel(
                            it,
                        )
                    }.toImmutableList(),
                )

                subject.handle(SearchUserAction.SearchCleared)
                awaitItem() shouldBe SearchState(results = persistentListOf())
            }
        }
    }
})
