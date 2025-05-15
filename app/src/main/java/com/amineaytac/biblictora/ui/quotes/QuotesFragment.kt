package com.amineaytac.biblictora.ui.quotes

import android.annotation.SuppressLint
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
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.amineaytac.biblictora.R
import com.amineaytac.biblictora.core.data.model.QuoteBook
import com.amineaytac.biblictora.core.data.model.QuoteItem
import com.amineaytac.biblictora.databinding.FragmentQuotesBinding
import com.amineaytac.biblictora.util.gone
import com.amineaytac.biblictora.util.viewBinding
import com.amineaytac.biblictora.util.visible
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.sqrt

@AndroidEntryPoint
class QuotesFragment : Fragment(R.layout.fragment_quotes) {

    private val binding by viewBinding(FragmentQuotesBinding::bind)
    private val viewModel: QuotesFragmentViewModel by viewModels()
    private lateinit var quotesBookAdapter: QuotesBookAdapter
    private val itemTouchInterceptor = ItemTouchInterceptor()
    private lateinit var quoteListAdapter: QuoteListAdapter
    private lateinit var quoteBook: QuoteBook
    private var bookId = -1

    companion object {
        const val SPAN_COUNT = 3
        const val NO_CLIP = 0
        const val CLIP_TOP = 1
        const val CLIP_BOTTOM = 2
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvQuoteList.visibility = View.GONE
        viewModel.getQuoteBooks()
        bindBookAdapter()
        bindQuoteListAdapter()
        observeQuoteBooks()
    }

    private fun createTransparentColor(color: Int): Int {
        val alpha = Color.alpha(color)
        val red = Color.red(color)
        val green = Color.green(color)
        val blue = Color.blue(color)

        val transparentAlpha = (alpha * 0.5f).toInt()
        return Color.argb(transparentAlpha, red, green, blue)
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

    private fun bindUI() = with(binding) {

        if (quoteBook.image.isNotEmpty()) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val bitmap = Picasso.get().load(quoteBook.image).get()
                    withContext(Dispatchers.Main) {
                        bindBackground(bitmap)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        tvTitle.text = quoteBook.title
        tvQuoteAuthor.text = quoteBook.authors
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

                    clQuoteList.setBackgroundColor(transparentDominantColor)

                    if (vibrantColor != 0) {
                        if (calculateColorDistanceToWhite(vibrantColor) < 200) {
                            tvTitle.setTextColor(requireContext().getColor(R.color.black))
                        }
                        cvBgBookPicture.setCardBackgroundColor(vibrantColor)
                    } else {
                        if (calculateColorDistanceToWhite(dominantColor) < 200) {
                            tvTitle.setTextColor(requireContext().getColor(R.color.black))
                        }
                        cvBgBookPicture.setCardBackgroundColor(dominantColor)
                    }
                }
            }
        }
    }

    private fun bindBookAdapter() = with(binding) {

        quotesBookAdapter = QuotesBookAdapter { book, imageView ->
            startTransitionAndOpenDetail(book, imageView)
        }

        rvImage.adapter = quotesBookAdapter
        rvImage.layoutManager = GridLayoutManager(requireContext(), SPAN_COUNT)
    }

    private fun bindQuoteListAdapter() = with(binding) {

        quoteListAdapter = QuoteListAdapter { quote ->
            buildAlertDialog(quote)
        }

        rvQuoteList.apply {
            layoutManager = LinearLayoutManager(
                requireContext(), LinearLayoutManager.VERTICAL, false
            )
            setHasFixedSize(false)
            adapter = quoteListAdapter
        }
    }

    private fun buildAlertDialog(quote: String) {
        AlertDialog.Builder(requireContext(), R.style.CustomAlertDialogTheme)
            .setMessage(getString(R.string.remove_favorite_quote))
            .setPositiveButton("Yes") { dialog, _ ->

                viewModel.deleteQuoteFromBook(quoteBook.id, quote)
                quotesBookAdapter.submitList(emptyList())
                viewModel.getQuoteBooks()

                dialog.dismiss()
            }.setNegativeButton("No") { dialog, _ -> dialog.dismiss() }.create().show()
    }

    private fun startTransitionAndOpenDetail(
        item: QuoteBook, clickedImageView: ImageView
    ) = with(binding) {

        quoteBook = item

        rvQuoteList.visible()
        pbQuoteList.visible()
        quoteListAdapter.submitList(emptyList())

        val rect = Rect()
        clickedImageView.getGlobalVisibleRect(rect)

        val clipType = when {
            rect.height() == clickedImageView.height -> NO_CLIP
            rect.top > 0 -> CLIP_TOP
            else -> CLIP_BOTTOM
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
            CLIP_TOP -> set.connect(
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
        tvQuoteAuthor.text = ""

        clickedImageView.alpha = 0.0f
        movingImageView.visible()
        movingImageView.setImageDrawable(clickedImageView.drawable)
        motionLayout.apply {
            updateState(R.id.start, set)
            setTransition(R.id.start, R.id.end)
            setTransitionListener({ start, _ ->
                itemTouchInterceptor.enable()

                pbQuoteList.visible()

                if (start == startState) {
                    clickedImageView.alpha = 0.0f
                    movingImageView.alpha = 1.0f
                }
            }, { state ->
                if (state == startState) {
                    bookId = -1
                    quotesBookAdapter.submitList(emptyList())
                    viewModel.getQuoteBooks()
                    itemTouchInterceptor.disable()
                    clickedImageView.alpha = 1.0f
                    movingImageView.alpha = 0.0f
                }
                if (state == endState) {
                    getQuoteBook(item)
                    bookId = item.id
                    bindUI()
                    rvQuoteList.post {
                        pbQuoteList.gone()
                        rvQuoteList.visible()
                        quoteListAdapter.submitList(item.quotesList)
                    }
                }
            })
            transitionToEnd()
        }
    }

    private fun observeQuoteBooks() = with(binding) {
        viewModel.quoteBooksScreenUiState.observe(viewLifecycleOwner) {
            if (it.books.isNotEmpty()) {
                quotesBookAdapter.submitList(it.books) {
                    if (!quotesBookAdapter.containsId(bookId)) {
                        rvQuoteList.gone()
                        motionLayout.transitionToStart()
                    }
                    rvImage.visible()
                    progressBar.gone()
                    pbQuoteList.gone()
                    tvInfo.gone()
                    ivEmptyQuoteBooks.gone()
                }

            } else {
                rvQuoteList.gone()
                motionLayout.transitionToStart()
                progressBar.gone()
                pbQuoteList.gone()
                rvImage.gone()
                tvInfo.visible()
                ivEmptyQuoteBooks.visible()
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getQuoteBook(quoteBook: QuoteBook) = with(binding) {
        viewModel.getQuoteBook(quoteBook.id).observe(viewLifecycleOwner) { quoteBookEntity ->
            quoteBookEntity?.let {

                val quotesListType = object : TypeToken<List<QuoteItem>>() {}.type
                val quotesList: List<QuoteItem> = Gson().fromJson(it.quotesList, quotesListType)

                if (quotesList.isEmpty()) {
                    rvQuoteList.gone()
                    motionLayout.transitionToStart()
                } else {
                    rvQuoteList.visible()
                    quoteListAdapter.submitList(quotesList)
                }
            }
        }
    }
}