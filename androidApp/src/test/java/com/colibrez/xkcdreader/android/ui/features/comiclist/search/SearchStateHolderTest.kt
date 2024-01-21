package com.colibrez.xkcdreader.android.ui.features.comiclist.search

import app.cash.turbine.test
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.test.TestScope
import io.kotest.matchers.shouldBe

class SearchStateHolderTest : FreeSpec({
    lateinit var subject: SearchStateHolder

    fun TestScope.getSubject(): SearchStateHolder =
        SearchStateHolder(viewModelScope = this)

    "handle" - {
        "QuerySubmitted and SearchCleared update state" {
            subject = getSubject()
            subject.state.test {
                awaitItem() shouldBe SearchState(searchQuery = "")

                subject.handle(SearchUserAction.QuerySubmitted("a"))
                awaitItem() shouldBe SearchState(searchQuery = "a")

                subject.handle(SearchUserAction.SearchCleared)
                awaitItem() shouldBe SearchState(searchQuery = "")
            }
        }
    }
})
