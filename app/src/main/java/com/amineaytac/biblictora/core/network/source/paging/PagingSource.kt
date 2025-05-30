package com.amineaytac.biblictora.core.network.source.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.amineaytac.biblictora.core.data.model.Book
import com.amineaytac.biblictora.core.data.repo.mapTo
import com.amineaytac.biblictora.core.data.repo.toBookList
import com.amineaytac.biblictora.core.data.repo.toBookListWithLanguagesFilter
import com.amineaytac.biblictora.core.network.source.rest.RestDataSource

class PagingSource(
    private val source: RestDataSource,
    private val funcKey: String,
    private val search: String = "",
    private val languages: List<String> = emptyList()
) : PagingSource<Int, Book>() {

    override fun getRefreshKey(state: PagingState<Int, Book>): Int? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(it)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Book> {

        val next: String?
        val prev: Any?

        return try {
            val position = params.key ?: 1
            val data = when (funcKey) {
                "getAllBooks" -> {
                    val response = source.getAllBooks(position)
                    next = response.body()?.next
                    prev = response.body()?.previous
                    response.mapTo { it.toBookList() }
                }

                "getBooksWithLanguages" -> {
                    val response = source.getBooksWithLanguages(position, languages)
                    next = response.body()?.next
                    prev = response.body()?.previous
                    response.mapTo { it.toBookList() }
                }

                "getBooksWithSearch" -> {
                    val response = source.getBooksWithSearch(position, search)
                    next = response.body()?.next
                    prev = response.body()?.previous
                    if (languages.isEmpty()) {
                        response.mapTo { it.toBookList() }
                    } else {
                        response.mapTo {
                            it.toBookListWithLanguagesFilter(
                                languages
                            )
                        }
                    }
                }

                else -> {
                    next = null
                    prev = null
                    emptyList()
                }
            }

            LoadResult.Page(
                data = data,
                prevKey = if (prev == null) null else position - 1,
                nextKey = if (next == null) null else position + 1
            )

        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}