package com.amineaytac.biblictora.ui.favoritebooks

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.distinctUntilChanged
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.amineaytac.biblictora.R
import com.amineaytac.biblictora.core.data.model.Book
import com.amineaytac.biblictora.core.data.model.ReadingBook
import com.amineaytac.biblictora.core.data.repo.toReadingBook
import com.amineaytac.biblictora.databinding.FragmentFavoriteBooksBinding
import com.amineaytac.biblictora.ui.favorite.FavoriteFragmentDirections
import com.amineaytac.biblictora.ui.quotes.ItemTouchInterceptor
import com.amineaytac.biblictora.ui.quotes.QuotesFragment
import com.amineaytac.biblictora.ui.quotes.setTransitionListener
import com.amineaytac.biblictora.util.gone
import com.amineaytac.biblictora.util.lightenColor
import com.amineaytac.biblictora.util.visible
import com.amineaytc.biblictora.util.viewBinding
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.sqrt

@AndroidEntryPoint
class FavoriteBooksFragment : Fragment(R.layout.fragment_favorite_books) {

    private val binding by viewBinding(FragmentFavoriteBooksBinding::bind)
    private val viewModel: FavoriteBooksViewModel by viewModels()
    private lateinit var favoriteBooksAdapter: FavoriteBooksAdapter
    private val itemTouchInterceptor = ItemTouchInterceptor()
    private var readingBook: ReadingBook? = null
    private var isAddBookInReadingList = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getFavoriteBooks()
        bindBookAdapter()
        observeFavoriteBooks()
    }

    private fun bindBookAdapter() = with(binding) {

        favoriteBooksAdapter = FavoriteBooksAdapter({ book, imageView ->
            startTransitionAndOpenDetail(book, imageView)
        }, { book ->
            buildAlertDialog(book)
        })

        rvFavoriteBooks.layoutManager = GridLayoutManager(requireContext(), 3)
        rvFavoriteBooks.setHasFixedSize(true)
        rvFavoriteBooks.adapter = favoriteBooksAdapter
    }

    private fun buildAlertDialog(book: Book) = with(binding) {

        val alertDialog = AlertDialog.Builder(requireContext(), R.style.CustomAlertDialogTheme)
        alertDialog.setMessage(getString(R.string.remove_favorite_book, book.title))

        alertDialog.setPositiveButton("Yes") { dialog, _ ->
            favoriteBooksAdapter.submitList(emptyList())
            viewModel.deleteFavoriteBook(book)
            viewModel.getFavoriteBooks()
            dialog.dismiss()
        }
        alertDialog.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }
        alertDialog.create().show()
    }

    private fun startTransitionAndOpenDetail(
        item: Book, clickedImageView: ImageView
    ) = with(binding) {

        val rect = Rect()
        clickedImageView.getGlobalVisibleRect(rect)

        val clipType = when {
            rect.height() == clickedImageView.height -> QuotesFragment.NO_CLIP
            rect.top > 0 -> QuotesFragment.CLIP_TOP
            else -> QuotesFragment.CLIP_BOTTOM
        }

        val set = motionLayout.getConstraintSet(R.id.start)
        set.clear(R.id.movingImageView)
        set.constrainWidth(R.id.movingImageView, clickedImageView.width)
        set.constrainHeight(R.id.movingImageView, clickedImageView.height)
        set.connect(
            R.id.movingImageView,
            ConstraintSet.START,
            ConstraintSet.PARENT_ID,
            ConstraintSet.START,
            rect.left
        )
        when (clipType) {
            QuotesFragment.CLIP_TOP -> set.connect(
                R.id.movingImageView,
                ConstraintSet.BOTTOM,
                ConstraintSet.PARENT_ID,
                ConstraintSet.BOTTOM,
                motionLayout.bottom - rect.bottom
            )

            else -> set.connect(
                R.id.movingImageView,
                ConstraintSet.TOP,
                ConstraintSet.PARENT_ID,
                ConstraintSet.TOP,
                rect.top
            )
        }

        tvTitle.text = ""
        tvAuthor.text = ""

        clickedImageView.alpha = 0.0f
        movingImageView.visible()
        movingImageView.setImageDrawable(clickedImageView.drawable)
        motionLayout.apply {
            updateState(R.id.start, set)
            setTransition(R.id.start, R.id.end)
            setTransitionListener({ start, _ ->
                itemTouchInterceptor.enable()
                if (start == startState) {
                    clickedImageView.alpha = 0.0f
                    movingImageView.alpha = 1.0f
                }
            }, { state ->
                if (state == startState) {
                    itemTouchInterceptor.disable()
                    clickedImageView.alpha = 1.0f
                    movingImageView.alpha = 0.0f
                    readingBook = null
                    isAddBookInReadingList = false
                    bindReadingBook()
                }
                if (state == endState) {
                    bindDetailUI(item)
                }
            })
            transitionToEnd()
        }
    }

    private fun bindDetailUI(book: Book) = with(binding) {
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

        heartView.isClicked = true
        heartView.changeColor()
        heartView.setOnClickListener {
            buildAlertDialog(book)
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

    private fun bindReadingBook() = with(binding) {
        if (readingBook != null) {
            when (readingBook!!.readingStates) {
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
        } else {
            ivWillRead.setBackgroundResource(R.drawable.ic_dark_will_read)
            ivReading.setBackgroundResource(R.drawable.ic_dark_reading)
            ivHaveRead.setBackgroundResource(R.drawable.ic_dark_have_read)
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

    private fun observeIsItemReading(book: Book) {
        viewModel.isBookItemReading(book.id.toString()).distinctUntilChanged()
            .observe(viewLifecycleOwner) {
                isAddBookInReadingList = it
                if (it) {
                    observeGetItemReading(book)
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
                }
            } else {
                ivReading.setBackgroundResource(R.drawable.ic_light_reading)
                ivHaveRead.setBackgroundResource(R.drawable.ic_dark_have_read)
                ivWillRead.setBackgroundResource(R.drawable.ic_dark_will_read)
                readingBook = ReadingBook(
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
                viewModel.addReadingBookItem(readingBook!!)
            }
            findNavController().navigate(
                FavoriteFragmentDirections.navigateToReadingFragment(
                    readingBook!!
                )
            )
        }
    }

    private fun bindBackground(bitmap: Bitmap?) = with(binding) {
        if (bitmap != null) {

            ivBookPicture.setImageBitmap(bitmap)
            ivBookBackground.setImageBitmap(bitmap)

            androidx.palette.graphics.Palette.from(bitmap).generate { palette ->

                val dominantColor = palette?.dominantSwatch?.rgb ?: 0
                if (dominantColor != 0) {

                    val newColor = dominantColor.lightenColor()
                    val vibrantColor = palette?.vibrantSwatch?.rgb ?: 0

                    llLanguages.setBackgroundColor(newColor)
                    llBookshelves.setBackgroundColor(newColor)
                    llReadInfo.setBackgroundColor(newColor)

                    if (vibrantColor != 0) {
                        if (calculateColorDistanceToWhite(vibrantColor) < 200) {
                            tvTitle.setTextColor(requireContext().getColor(R.color.black))
                            btnReadingNow.setTextColor(requireContext().getColor(R.color.black))
                        }
                        cvBgBookPicture.setCardBackgroundColor(vibrantColor)
                        btnReadingNow.backgroundTintList =
                            android.content.res.ColorStateList.valueOf(vibrantColor)
                    } else {
                        if (calculateColorDistanceToWhite(dominantColor) < 200) {
                            tvTitle.setTextColor(requireContext().getColor(R.color.black))
                            btnReadingNow.setTextColor(requireContext().getColor(R.color.black))
                        }
                        cvBgBookPicture.setCardBackgroundColor(dominantColor)
                        btnReadingNow.backgroundTintList =
                            android.content.res.ColorStateList.valueOf(dominantColor)
                    }
                }
            }
        }
    }

    private fun observeFavoriteBooks() = with(binding) {
        viewModel.favoriteBooksScreenUiState.observe(viewLifecycleOwner) {
            if (it.books.isNotEmpty()) {
                favoriteBooksAdapter.submitList(it.books) {
                    progressBar.gone()
                    rvFavoriteBooks.visible()
                    tvInfo.gone()
                    ivEmptyFavoriteBooks.gone()
                }
            } else {
                progressBar.gone()
                rvFavoriteBooks.gone()
                tvInfo.visible()
                ivEmptyFavoriteBooks.visible()
            }
            motionLayout.transitionToStart()
        }
    }

    private fun observeGetItemReading(book: Book) {
        viewModel.getBookItemReading(book.id.toString()).distinctUntilChanged()
            .observe(viewLifecycleOwner) {
                if (isAddBookInReadingList) {
                    readingBook = it.toReadingBook()
                    bindReadingBook()
                }
            }
    }
}