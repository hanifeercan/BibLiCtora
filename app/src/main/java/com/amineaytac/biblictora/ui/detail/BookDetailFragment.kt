package com.amineaytac.biblictora.ui.detail

import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.distinctUntilChanged
import androidx.navigation.fragment.findNavController
import androidx.palette.graphics.Palette
import com.amineaytac.biblictora.R
import com.amineaytac.biblictora.core.data.model.Book
import com.amineaytac.biblictora.core.data.model.ReadingBook
import com.amineaytac.biblictora.core.data.repo.toReadingBook
import com.amineaytac.biblictora.databinding.FragmentBookDetailBinding
import com.amineaytac.biblictora.util.gone
import com.amineaytc.biblictora.util.viewBinding
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.sqrt

@AndroidEntryPoint
class BookDetailFragment : Fragment(R.layout.fragment_book_detail) {

    private val binding by viewBinding(FragmentBookDetailBinding::bind)
    private val viewModel: BookDetailViewModel by viewModels()
    private var isFavorited = false
    private var readingBook: ReadingBook? = null
    private var isAddBookInReadingList = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindUI()
    }

    private fun createTransparentColor(color: Int): Int {
        val alpha = Color.alpha(color)
        val red = Color.red(color)
        val green = Color.green(color)
        val blue = Color.blue(color)

        val transparentAlpha = (alpha * 0.5f).toInt()

        return Color.argb(transparentAlpha, red, green, blue)
    }

    private fun bindUI() = with(binding) {
        if (arguments != null) {
            val book = BookDetailFragmentArgs.fromBundle(requireArguments()).book
            observeIsItemFavorited(book)
            observeIsItemReading(book)
            setReadingStateClickListener(book)
            bindReadingNow(book)
            if (book.image.isNotEmpty()) {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val bitmap = Picasso.get().load(book.image).get()
                        withContext(Dispatchers.Main) {
                            bindBackground(bitmap)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            tvAuthor.text = book.authors
            tvLanguages.text = book.languages
            tvTitle.text = book.title
            if (book.bookshelves.isEmpty()) {
                cvBookshelves.gone()
            } else {
                tvBookshelves.text = book.bookshelves.joinToString("\n")
            }
        }
    }

    private fun bindBackground(bitmap: Bitmap?) = with(binding) {
        if (bitmap != null) {

            ivBookPicture.setImageBitmap(bitmap)
            ivBookBackground.setImageBitmap(bitmap)

            Palette.from(bitmap).generate { palette ->

                val dominantColor = palette?.dominantSwatch?.rgb ?: 0
                if (dominantColor != 0) {

                    val transparentDominantColor = createTransparentColor(dominantColor)
                    val vibrantColor = palette?.vibrantSwatch?.rgb ?: 0

                    llLanguages.setBackgroundColor(transparentDominantColor)
                    llBookshelves.setBackgroundColor(transparentDominantColor)
                    llReadInfo.setBackgroundColor(transparentDominantColor)

                    if (vibrantColor != 0) {
                        if (calculateColorDistanceToWhite(vibrantColor) < 200) {
                            tvTitle.setTextColor(requireContext().getColor(R.color.black))
                            btnReadingNow.setTextColor(requireContext().getColor(R.color.black))
                        }
                        cvBgBookPicture.setCardBackgroundColor(vibrantColor)
                        btnReadingNow.backgroundTintList = ColorStateList.valueOf(vibrantColor)
                    } else {
                        if (calculateColorDistanceToWhite(dominantColor) < 200) {
                            tvTitle.setTextColor(requireContext().getColor(R.color.black))
                            btnReadingNow.setTextColor(requireContext().getColor(R.color.black))
                        }
                        cvBgBookPicture.setCardBackgroundColor(dominantColor)
                        btnReadingNow.backgroundTintList = ColorStateList.valueOf(dominantColor)
                    }
                }
            }
        }
    }

    private fun calculateColorDistanceToWhite(colorInt: Int): Double {
        val colorHex = String.format("#%06X", 0xFFFFFF and colorInt)
        val color = Color.parseColor(colorHex)
        val red = Color.red(color)
        val green = Color.green(color)
        val blue = Color.blue(color)

        val whiteRed = 255
        val whiteGreen = 255
        val whiteBlue = 255

        return sqrt(
            ((whiteRed - red) * (whiteRed - red) + (whiteGreen - green) * (whiteGreen - green) + (whiteBlue - blue) * (whiteBlue - blue)).toDouble()
        )
    }

    private fun observeIsItemFavorited(book: Book) {
        viewModel.isItemFavorited(book.id.toString()).observe(viewLifecycleOwner) {
            isFavorited = it
            bindHeartView(book)
        }
    }

    private fun observeGetItemReading(book: Book) {
        viewModel.getBookItemReading(book.id.toString()).distinctUntilChanged()
            .observe(viewLifecycleOwner) {
                if (isAddBookInReadingList) {
                    readingBook = it.toReadingBook()
                    bindReadingBook(it.toReadingBook())
                }
            }
    }

    private fun observeIsItemReading(book: Book) {
        viewModel.isBookItemReading(book.id.toString()).distinctUntilChanged()
            .observe(viewLifecycleOwner) {
                isAddBookInReadingList = it
                if (it) {
                    observeGetItemReading(book)
                }
            }
    }

    private fun bindReadingBook(readingBook: ReadingBook) = with(binding) {
        when (readingBook.readingStates) {
            "willRead" -> {
                ivWillRead.setBackgroundResource(R.drawable.ic_light_will_read)
                ivReading.setBackgroundResource(R.drawable.ic_dark_reading)
                ivHaveRead.setBackgroundResource(R.drawable.ic_dark_have_read)
            }

            "reading" -> {
                ivReading.setBackgroundResource(R.drawable.ic_light_reading)
                ivWillRead.setBackgroundResource(R.drawable.ic_dark_will_read)
                ivHaveRead.setBackgroundResource(R.drawable.ic_dark_have_read)
            }

            "haveRead" -> {
                ivHaveRead.setBackgroundResource(R.drawable.ic_light_have_read)
                ivReading.setBackgroundResource(R.drawable.ic_dark_reading)
                ivWillRead.setBackgroundResource(R.drawable.ic_dark_will_read)
            }
        }
    }

    private fun setReadingStateClickListener(book: Book) = with(binding) {

        ivWillRead.setOnClickListener {
            if (readingBook != null) {
                when (readingBook?.readingStates) {
                    "willRead" -> {
                        ivWillRead.setBackgroundResource(R.drawable.ic_dark_will_read)
                        viewModel.deleteReadingBookItem(readingBook!!)
                        isAddBookInReadingList = false
                        readingBook = null
                    }

                    "reading" -> {
                        ivReading.setBackgroundResource(R.drawable.ic_dark_reading)
                        ivWillRead.setBackgroundResource(R.drawable.ic_light_will_read)
                        viewModel.updateBookStatusAndPercentage(readingBook!!.id, "willRead", 0)
                    }

                    "haveRead" -> {
                        ivWillRead.setBackgroundResource(R.drawable.ic_light_will_read)
                        ivHaveRead.setBackgroundResource(R.drawable.ic_dark_have_read)
                        viewModel.updateBookStatusAndPercentage(readingBook!!.id, "willRead", 0)
                    }
                }
            } else {
                ivWillRead.setBackgroundResource(R.drawable.ic_light_will_read)
                val reading = ReadingBook(
                    id = book.id,
                    authors = book.authors,
                    bookshelves = book.bookshelves,
                    languages = book.languages,
                    title = book.title,
                    formats = book.formats,
                    image = book.image,
                    readingStates = "willRead",
                    readingPercentage = 0,
                    readingProgress = 0
                )
                viewModel.addReadingBookItem(reading)
            }
        }
        ivReading.setOnClickListener {
            if (readingBook != null) {
                when (readingBook?.readingStates) {
                    "willRead" -> {
                        ivWillRead.setBackgroundResource(R.drawable.ic_dark_will_read)
                        ivReading.setBackgroundResource(R.drawable.ic_light_reading)
                        viewModel.updateBookStatusAndPercentage(readingBook!!.id, "reading", 0)
                    }

                    "reading" -> {
                        ivReading.setBackgroundResource(R.drawable.ic_dark_reading)
                        viewModel.deleteReadingBookItem(readingBook!!)
                        isAddBookInReadingList = false
                        readingBook = null
                    }

                    "haveRead" -> {
                        ivReading.setBackgroundResource(R.drawable.ic_light_reading)
                        ivHaveRead.setBackgroundResource(R.drawable.ic_dark_have_read)
                        viewModel.updateBookStatusAndPercentage(readingBook!!.id, "reading", 0)
                    }
                }
            } else {
                ivReading.setBackgroundResource(R.drawable.ic_light_reading)
                val reading = ReadingBook(
                    id = book.id,
                    authors = book.authors,
                    bookshelves = book.bookshelves,
                    languages = book.languages,
                    title = book.title,
                    formats = book.formats,
                    image = book.image,
                    readingStates = "reading",
                    readingPercentage = 0,
                    readingProgress = 0
                )
                viewModel.addReadingBookItem(reading)
            }
        }
        ivHaveRead.setOnClickListener {
            if (readingBook != null) {
                when (readingBook?.readingStates) {
                    "willRead" -> {
                        ivHaveRead.setBackgroundResource(R.drawable.ic_light_have_read)
                        ivWillRead.setBackgroundResource(R.drawable.ic_dark_will_read)
                        viewModel.updateBookStatusAndPercentage(readingBook!!.id, "haveRead", 100)
                    }

                    "reading" -> {
                        ivReading.setBackgroundResource(R.drawable.ic_dark_reading)
                        ivHaveRead.setBackgroundResource(R.drawable.ic_light_have_read)
                        viewModel.updateBookStatusAndPercentage(readingBook!!.id, "haveRead", 100)
                    }

                    "haveRead" -> {
                        ivHaveRead.setBackgroundResource(R.drawable.ic_dark_have_read)
                        viewModel.deleteReadingBookItem(readingBook!!)
                        isAddBookInReadingList = false
                        readingBook = null
                    }
                }
            } else {
                ivHaveRead.setBackgroundResource(R.drawable.ic_light_have_read)
                val reading = ReadingBook(
                    id = book.id,
                    authors = book.authors,
                    bookshelves = book.bookshelves,
                    languages = book.languages,
                    title = book.title,
                    formats = book.formats,
                    image = book.image,
                    readingStates = "haveRead",
                    readingPercentage = 100,
                    readingProgress = 0
                )
                viewModel.addReadingBookItem(reading)
            }
        }
    }

    private fun bindReadingNow(book: Book) = with(binding) {
        btnReadingNow.setOnClickListener {
            if (readingBook != null) {
                if (readingBook?.readingStates != "reading") {
                    ivReading.setBackgroundResource(R.drawable.ic_light_reading)
                    ivHaveRead.setBackgroundResource(R.drawable.ic_dark_have_read)
                    ivWillRead.setBackgroundResource(R.drawable.ic_dark_will_read)
                    viewModel.updateBookStatusAndPercentage(readingBook!!.id, "reading", 0)
                    findNavController().navigate(
                        BookDetailFragmentDirections.navigateToReadingFragment(
                            readingBook!!
                        )
                    )
                } else {
                    findNavController().navigate(
                        BookDetailFragmentDirections.navigateToReadingFragment(
                            readingBook!!
                        )
                    )
                }
            } else {
                ivReading.setBackgroundResource(R.drawable.ic_light_reading)
                ivHaveRead.setBackgroundResource(R.drawable.ic_dark_have_read)
                ivWillRead.setBackgroundResource(R.drawable.ic_dark_will_read)
                val reading = ReadingBook(
                    id = book.id,
                    authors = book.authors,
                    bookshelves = book.bookshelves,
                    languages = book.languages,
                    title = book.title,
                    formats = book.formats,
                    image = book.image,
                    readingStates = "reading",
                    readingPercentage = 0,
                    readingProgress = 0
                )
                viewModel.addReadingBookItem(reading)
                findNavController().navigate(
                    BookDetailFragmentDirections.navigateToReadingFragment(
                        reading
                    )
                )
            }
        }
    }

    private fun onFavoriteClickListener(book: Book, isFavorited: Boolean) {
        if (isFavorited) {
            viewModel.addFavoriteItem(book)
        } else {
            viewModel.deleteFavoriteItem(book)
        }
        observeIsItemFavorited(book)
    }

    private fun bindHeartView(book: Book) = with(binding) {
        heartView.isClicked = isFavorited
        heartView.changeColor()
        heartView.setOnFavoriteClickListener {
            if (isFavorited) {
                onFavoriteClickListener(book, false)
            } else {
                onFavoriteClickListener(book, true)
            }
        }
    }
}