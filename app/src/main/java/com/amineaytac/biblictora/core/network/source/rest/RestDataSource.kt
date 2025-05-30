package com.amineaytac.biblictora.core.network.source.rest

import com.amineaytac.biblictora.core.network.dto.BookResponse
import retrofit2.Response

interface RestDataSource {

    suspend fun getAllBooks(page: Int): Response<BookResponse>
    suspend fun getBooksWithSearch(page: Int, search: String): Response<BookResponse>
    suspend fun getBooksWithLanguages(page: Int, languages: List<String>): Response<BookResponse>
}