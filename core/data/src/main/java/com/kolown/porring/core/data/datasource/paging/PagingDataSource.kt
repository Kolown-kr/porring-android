package com.kolown.porring.core.data.datasource.paging

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState

internal class PagingDataSource<T : Any>(
    private val perPage: Int,
    private val fetcher: suspend (startKey: String?, perPage: Int) -> Result<List<T>>,
    private val keySelector: (T) -> String
) : PagingSource<String, T>() {
    override fun getRefreshKey(state: PagingState<String, T>): String? = null

    override suspend fun load(params: LoadParams<String>): LoadResult<String, T> {
        val start = params.key
        val response = fetcher(start, perPage)

        return when {
            response.isSuccess -> {
                val data = response.getOrNull().orEmpty()
                LoadResult.Page(
                    data = data,
                    prevKey = data.firstOrNull()?.let(keySelector),
                    nextKey = data.lastOrNull()?.let(keySelector),
                )
            }

            else -> {
                LoadResult.Error(response.exceptionOrNull() ?: Exception("Unknown error"))
            }
        }
    }

    companion object {
        private const val DEFAULT_PER_PAGE = 10

        fun <T : Any> createPager(
            pageSize: Int = DEFAULT_PER_PAGE,
            keySelector: (T) -> String,
            fetcher: suspend (startKey: String?, perPage: Int) -> Result<List<T>>,
        ) = Pager(
            config = PagingConfig(pageSize = pageSize, enablePlaceholders = true),
            pagingSourceFactory = {
                PagingDataSource(
                    perPage = pageSize,
                    fetcher = fetcher,
                    keySelector = keySelector
                )
            }
        ).flow
    }
}