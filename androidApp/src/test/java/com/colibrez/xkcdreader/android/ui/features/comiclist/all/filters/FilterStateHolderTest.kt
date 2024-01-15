package com.colibrez.xkcdreader.android.ui.features.comiclist.all.filters

import app.cash.turbine.test
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class FilterStateHolderTest : FreeSpec({

    "handle" - {
        "IsReadFilterSelected updates state" {
            val subject = FilterStateHolder()
            subject.state.test {
                awaitItem() shouldBe FiltersState(isReadFilter = EnumFilterState(selection = ReadFilter.All))

                subject.handle(FilterUserAction.IsReadFilterSelected(newFilterValue = ReadFilter.Read))
                awaitItem() shouldBe FiltersState(isReadFilter = EnumFilterState(selection = ReadFilter.Read))

                subject.handle(FilterUserAction.IsReadFilterSelected(newFilterValue = ReadFilter.Unread))
                awaitItem() shouldBe FiltersState(isReadFilter = EnumFilterState(selection = ReadFilter.Unread))

                subject.handle(FilterUserAction.IsReadFilterSelected(newFilterValue = ReadFilter.All))
                awaitItem() shouldBe FiltersState(isReadFilter = EnumFilterState(selection = ReadFilter.All))
            }
        }
    }
})
