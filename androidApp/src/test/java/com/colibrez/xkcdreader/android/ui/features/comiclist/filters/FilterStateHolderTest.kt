package com.colibrez.xkcdreader.android.ui.features.comiclist.filters

import app.cash.turbine.test
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class FilterStateHolderTest : FreeSpec({

    "handle" - {
        "IsReadFilterSelected updates state" {
            val subject = FilterStateHolder()
            subject.state.test {
                awaitItem().isReadFilter.selection shouldBe ReadFilter.All

                subject.handle(FilterUserAction.IsReadFilterSelected(newFilterValue = ReadFilter.Read))
                awaitItem().isReadFilter.selection shouldBe ReadFilter.Read

                subject.handle(FilterUserAction.IsReadFilterSelected(newFilterValue = ReadFilter.Unread))
                awaitItem().isReadFilter.selection shouldBe ReadFilter.Unread

                subject.handle(FilterUserAction.IsReadFilterSelected(newFilterValue = ReadFilter.All))
                awaitItem().isReadFilter.selection shouldBe ReadFilter.All
            }
        }
    }
})
