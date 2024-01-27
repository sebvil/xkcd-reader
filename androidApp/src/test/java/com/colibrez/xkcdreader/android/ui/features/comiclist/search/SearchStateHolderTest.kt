package com.colibrez.xkcdreader.android.ui.features.comiclist.search

import com.colibrez.xkcdreader.android.extension.advanceUntilIdle
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.test.TestScope
import io.kotest.matchers.shouldBe

class SearchStateHolderTest : FreeSpec({
    lateinit var subject: SearchStateHolder

    var searchQuery = ""

    fun TestScope.getSubject(): SearchStateHolder =
        SearchStateHolder(
            delegate = object : SearchDelegate {
                override fun onSearchQueryUpdated(newQuery: String) {
                    searchQuery = newQuery
                }
            },
            stateHolderScope = this,
        )

    "handle" - {
        "QuerySubmitted and SearchCleared update state" {
            subject = getSubject()
            subject.handle(SearchUserAction.QuerySubmitted("a"))
            advanceUntilIdle()
            searchQuery shouldBe "a"

            subject.handle(SearchUserAction.SearchCleared)
            advanceUntilIdle()
            searchQuery shouldBe ""
        }
    }
})
