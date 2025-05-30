package com.amineaytac.biblictora.core.network.source.rest

import com.amineaytac.biblictora.core.network.dto.BookResponse
import retrofit2.Response
import javax.inject.Inject

class RestDataSourceImpl @Inject constructor(private val bookRestApi: BookRestApi) :
    RestDataSource {

    override suspend fun getAllBooks(page: Int): Response<BookResponse> {
        return bookRestApi.getAllBooks(page)
    }

    override suspend fun getBooksWithSearch(page: Int, search: String): Response<BookResponse> {
        return bookRestApi.getBooksWithSearch(page, search)
    }

    override suspend fun getBooksWithLanguages(
        page: Int,
        languages: List<String>
    ): Response<BookResponse> {
        return bookRestApi.getBooksWithLanguages(page, languages)
    }
}