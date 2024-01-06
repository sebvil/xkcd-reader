package com.colibrez.xkcdreader.android.data.repository.paging

sealed interface PagingStatus {

    /**
     * Fetching data.
     */
    data object Loading : PagingStatus

    /**
     * Not actively fetching data, no error.
     *
     * @param endOfPaginationReached indicates there are no more items to be fetched.
     */
    data class Idle(val endOfPaginationReached: Boolean) : PagingStatus

    /**
     * An error occurred while fetching data.
     *
     * @param message message to display to the user.
     */
    data class NetworkError(val message: String) : PagingStatus

    fun canFetchData(): Boolean {
        return  when (this) {
            is Idle -> !endOfPaginationReached
            is Loading -> false
            is NetworkError -> true
        }
    }
}