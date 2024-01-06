package com.colibrez.xkcdreader.android.ui.components.paging

import app.cash.turbine.test
import com.colibrez.xkcdreader.android.data.repository.paging.FakePagingDataSource
import com.colibrez.xkcdreader.android.data.repository.paging.PagingStatus
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.test.TestScope
import io.kotest.core.test.testCoroutineScheduler
import io.kotest.datatest.withData
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

@OptIn(ExperimentalStdlibApi::class, ExperimentalCoroutinesApi::class)
class PagingStateHolderTest : FreeSpec({

    lateinit var pagingDataSourceDep: FakePagingDataSource<Int>
    lateinit var subject: PagingStateHolder<String, Int>

    fun TestScope.getSubject(): PagingStateHolder<String, Int> {
        return PagingStateHolder(
            pageSize = PAGE_SIZE,
            viewModelScope = this,
            pagingDataSource = pagingDataSourceDep,
            itemTransform = Int::toString
        )
    }

    beforeTest {
        pagingDataSourceDep = FakePagingDataSource()
    }

    "state" - {
        "status is updated when paging data source status changes" - {
            withData(
                PagingStatus.Idle(endOfPaginationReached = false),
                PagingStatus.Idle(endOfPaginationReached = true),
                PagingStatus.NetworkError(message = "Error")
            ) { status ->
                subject = getSubject()
                subject.state.map { it.status }.test {
                    awaitItem() shouldBe PagingStatus.Loading
                    pagingDataSourceDep.stateValue.update {
                        it.copy(status = status)
                    }
                    awaitItem() shouldBe status
                    pagingDataSourceDep.stateValue.update {
                        it.copy(status = PagingStatus.Loading)
                    }
                    awaitItem() shouldBe PagingStatus.Loading
                }
            }
        }

        "items are updated when paging data source items change" - {
            withData(
                nameFn = { it.joinToString() },
                listOf(1,2,3),
                listOf(3,5,7,9)
            ) { items ->
                subject = getSubject()
                subject.state.map { it.items }.test {
                    awaitItem() shouldBe listOf()
                    pagingDataSourceDep.stateValue.update {
                        it.copy(items = items)
                    }
                    awaitItem() shouldBe items.map(Int::toString)
                    pagingDataSourceDep.stateValue.update {
                        it.copy(items = listOf())
                    }
                    expectNoEvents()
                }
            }
        }
    }



    "handle" - {
        "FetchPage fetches page from paging data source" - {
            withData(
                mapOf(
                    "when is initial fetch" to true,
                    "when is not initial fetch" to false
                )
            ) {isInitialFetch ->
                subject = getSubject()
                subject.handle(PagingUserAction.FetchPage(isInitialFetch = isInitialFetch))
                testCoroutineScheduler.advanceUntilIdle()
                pagingDataSourceDep.fetchInvocations shouldContainExactly  listOf(
                    FakePagingDataSource.FetchArguments(
                        pageSize = PAGE_SIZE,
                        isInitialFetch = isInitialFetch
                    ))
            }
        }
    }
}) {
    companion object {
        private const val PAGE_SIZE = 20L
    }
}
