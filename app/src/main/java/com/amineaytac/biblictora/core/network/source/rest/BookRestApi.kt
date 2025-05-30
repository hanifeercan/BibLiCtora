package com.amineaytac.biblictora.core.network.source.rest

import com.amineaytac.biblictora.core.network.dto.BookResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface BookRestApi {
    @GET(".")
    suspend fun getAllBooks(@Query("page") page: Int): Response<BookResponse>

    @GET(".")
    suspend fun getBooksWithSearch(
        @Query("page") page: Int, @Query("search") search: String
    ): Response<BookResponse>

    @GET(".")
    suspend fun getBooksWithLanguages(
        @Query("page") page: Int, @Query("languages") languages: List<String>
    ): Response<BookResponse>
}