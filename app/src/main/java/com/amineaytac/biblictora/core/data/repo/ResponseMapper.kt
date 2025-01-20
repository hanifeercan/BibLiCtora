package com.amineaytac.biblictora.core.data.repo

import com.amineaytac.biblictora.core.data.model.Book
import com.amineaytac.biblictora.core.data.model.ReadFormats
import com.amineaytac.biblictora.core.data.model.ReadingBook
import com.amineaytac.biblictora.core.database.entity.FavoriteEntity
import com.amineaytac.biblictora.core.database.entity.ReadingStatusEntity
import com.amineaytac.biblictora.core.network.dto.Author
import com.amineaytac.biblictora.core.network.dto.BookResponse
import com.amineaytac.biblictora.core.network.dto.Formats
import retrofit2.Response

typealias RestBooksResponse = Response<BookResponse>
typealias AuthorsResponse = List<Author?>?
typealias LanguageResponse = List<String?>?

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