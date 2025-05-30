package com.amineaytac.biblictora.core.data.repo

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.amineaytac.biblictora.core.data.model.Book
import com.amineaytac.biblictora.core.data.model.MyBooksItem
import com.amineaytac.biblictora.core.data.model.QuoteBook
import com.amineaytac.biblictora.core.data.model.QuoteItem
import com.amineaytac.biblictora.core.data.model.ReadFormats
import com.amineaytac.biblictora.core.data.model.ReadingBook
import com.amineaytac.biblictora.core.database.entity.FavoriteEntity
import com.amineaytac.biblictora.core.database.entity.MyBooksEntity
import com.amineaytac.biblictora.core.database.entity.QuotesEntity
import com.amineaytac.biblictora.core.database.entity.ReadingStatusEntity
import com.amineaytac.biblictora.core.network.dto.Author
import com.amineaytac.biblictora.core.network.dto.BookResponse
import com.amineaytac.biblictora.core.network.dto.Formats
import com.amineaytac.biblictora.core.network.dto.quotes.QuoteResponse
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import retrofit2.Response

typealias RestBooksResponse = Response<BookResponse>
typealias AuthorsResponse = List<Author?>?
typealias LanguageResponse = List<String?>?
typealias RandomQuoteResponse = Response<QuoteResponse>

fun RestBooksResponse.toBookList(): List<Book> {
    return body()!!.results!!.map { book ->
        val bookshelves = book?.bookshelves?.map {
            if (it.isNullOrEmpty()) {
                ""
            } else {
                it
            }
        } ?: emptyList()

        Book(
            book?.id ?: -1,
            book?.authors.toAuthorString(),
            bookshelves,
            book?.languages.toLanguageString(),
            emptyStringToAnonymous(book?.title),
            book?.formats?.toReadFormats() ?: ReadFormats("", ""),
            book?.formats?.imagejpeg.toString()
        )
    }
}

fun RestBooksResponse.toBookListWithLanguagesFilter(languages: List<String>): List<Book> {
    return body()!!.results!!.filter {
        var control = false
        languages.forEach { language ->
            if (it?.languages?.contains(language) == true) {
                control = true
            }
        }
        control
    }.map { book ->
        val bookshelves = book?.bookshelves?.map {
            if (it.isNullOrEmpty()) {
                ""
            } else {
                it
            }
        } ?: emptyList()

        Book(
            book?.id ?: -1,
            book?.authors.toAuthorString(),
            bookshelves,
            book?.languages.toLanguageString(),
            emptyStringToAnonymous(book?.title),
            book?.formats?.toReadFormats() ?: ReadFormats("", ""),
            book?.formats?.imagejpeg.toString()
        )
    }
}

fun emptyStringToAnonymous(str: String?): String {
    return if (str.isNullOrEmpty()) "Anonymous" else str
}

fun AuthorsResponse.toAuthorString(): String {
    var authors = ""

    this?.forEachIndexed { index, author ->
        val name = author?.name?.split(", ")
        if (name?.size == 1) {
            authors += name[0]
        } else {
            authors += name?.get(1) ?: ""
            authors += " "
            authors += name?.get(0) ?: ""
            if (this.size != index + 1) {
                authors += ", "
            }
        }
    }
    return emptyStringToAnonymous(authors)
}

fun LanguageResponse.toLanguageString(): String {
    var language = ""
    this?.forEachIndexed { index, s ->
        language += s?.uppercase()
        if (this.size != index + 1) {
            language += ", "
        }
    }
    return emptyStringToAnonymous(language)
}

fun Formats.toReadFormats(): ReadFormats {
    return ReadFormats(
        this.texthtml.toString(), this.texthtmlCharsetutf8.toString()
    )
}

fun FavoriteEntity.toBook(): Book {
    return Book(
        id = this.id,
        authors = this.authors,
        bookshelves = this.bookshelves,
        languages = this.languages,
        title = this.title,
        formats = this.formats,
        image = this.image
    )
}

fun Book.toFavoriteItemEntity(): FavoriteEntity {
    return FavoriteEntity(
        id = id,
        authors = authors,
        bookshelves = bookshelves,
        languages = languages,
        title = title,
        formats = formats,
        image = image
    )
}

fun QuotesEntity.toQuoteBook(): QuoteBook {
    val quotesListType = object : TypeToken<List<QuoteItem>>() {}.type
    val quotesList: List<QuoteItem> = Gson().fromJson(this.quotesList, quotesListType)

    return QuoteBook(
        id = this.id,
        authors = this.authors,
        title = this.title,
        image = this.image,
        quotesList = quotesList
    )
}

fun ReadingStatusEntity.toReadingBook(): ReadingBook {
    return ReadingBook(
        id = this.id,
        authors = this.authors,
        bookshelves = this.bookshelves,
        languages = this.languages,
        title = this.title,
        formats = this.formats,
        image = this.image,
        readingStates = this.readingStates,
        readingPercentage = this.readingPercentage,
        readingProgress = this.readingProgress
    )
}

fun ReadingBook.toStatusEntity(): ReadingStatusEntity {
    return ReadingStatusEntity(
        id = this.id,
        authors = this.authors,
        bookshelves = this.bookshelves,
        languages = this.languages,
        title = this.title,
        formats = this.formats,
        image = this.image,
        readingStates = this.readingStates,
        readingPercentage = this.readingPercentage,
        readingProgress = this.readingProgress
    )
}

fun RandomQuoteResponse.toQuote(): QuoteResponse {
    return body().let {
        QuoteResponse(
            author = "- " + it?.author, quote = "'" + it?.quote + "'"
        )
    }
}

fun MyBooksEntity.toMyBooksItem(): MyBooksItem {
    return MyBooksItem(
        id = this.id,
        name = this.name,
        filePath = this.filePath,
        fileType = this.fileType,
        lastPage = this.lastPage
    )
}

fun MyBooksItem.toMyBooksEntity(): MyBooksEntity {
    return MyBooksEntity(
        id = this.id,
        name = this.name,
        filePath = this.filePath,
        fileType = this.fileType,
        lastPage = this.lastPage
    )
}

fun LiveData<ReadingStatusEntity?>.toLiveDataReadingBook(): LiveData<ReadingBook?> {
    return this.map { readingStatusEntity ->
        readingStatusEntity?.let {
            ReadingBook(
                id = it.id,
                authors = it.authors,
                bookshelves = it.bookshelves,
                languages = it.languages,
                title = it.title,
                formats = it.formats,
                image = it.image,
                readingStates = it.readingStates,
                readingPercentage = it.readingPercentage,
                readingProgress = it.readingProgress
            )
        }
    }
}

fun LiveData<QuotesEntity>.toLiveDataQuoteBook(): LiveData<QuoteBook> {
    return this.map { quotesEntity ->
        QuoteBook(
            id = quotesEntity.id,
            authors = quotesEntity.authors,
            title = quotesEntity.title,
            image = quotesEntity.image,
            quotesList = quotesEntity.quotesList.toQuoteItemList()
        )
    }
}

fun String.toQuoteItemList(): List<QuoteItem> {
    return this.let {
        val type = object : TypeToken<List<QuoteItem>>() {}.type
        try {
            Gson().fromJson(it, type)
        } catch (e: JsonSyntaxException) {
            null
        }
    } ?: emptyList()
}