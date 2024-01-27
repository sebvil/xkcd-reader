package com.colibrez.xkcdreader.android.ui.features.comiclist.filters

import app.cash.turbine.test
import io.kotest.assertions.NoopErrorCollector.subject
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.test.TestScope
import io.kotest.matchers.shouldBe

class FilterStateHolderTest : FreeSpec({

    var unreadFilterValue: Boolean = false
    var favoriteFilterValue: Boolean = false

    fun TestScope.getSubject(): FilterStateHolder {
        return FilterStateHolder(
            delegate = object : FiltersDelegate {
                override fun onUnreadFilterChanged(newValue: Boolean) {
                    unreadFilterValue = newValue
                }

                override fun onFavoriteFilterChanges(newValue: Boolean) {
                    favoriteFilterValue = newValue
                }
            },
        )
    }

    "handle" - {
        "UnreadFilterSelected updates state, calls delegate" {
            val subject = getSubject()
            subject.state.test {
                awaitItem().unread.selected shouldBe false
                unreadFilterValue shouldBe false

                subject.handle(FilterUserAction.UnreadFilterSelected(newValue = true))
                awaitItem().unread.selected shouldBe true
                unreadFilterValue shouldBe true

                subject.handle(FilterUserAction.UnreadFilterSelected(newValue = false))
                awaitItem().unread.selected shouldBe false
                unreadFilterValue shouldBe false
            }
        }

        "FavoriteFilterSelected updates state, calls delegate" {
            val subject = getSubject()
            subject.state.test {
                awaitItem().favorites.selected shouldBe false
                favoriteFilterValue shouldBe false

                subject.handle(FilterUserAction.FavoriteFilterSelected(newValue = true))
                awaitItem().favorites.selected shouldBe true
                favoriteFilterValue shouldBe true

                subject.handle(FilterUserAction.FavoriteFilterSelected(newValue = false))
                awaitItem().favorites.selected shouldBe false
                favoriteFilterValue shouldBe false
            }
        }
    }
})
