package com.amineaytac.biblictora.ui.quotes

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.amineaytac.biblictora.R
import com.amineaytac.biblictora.core.data.model.QuoteBook
import com.amineaytac.biblictora.databinding.FragmentQuotesBinding
import com.amineaytac.biblictora.util.gone
import com.amineaytac.biblictora.util.visible
import com.amineaytc.biblictora.util.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QuotesFragment : Fragment(R.layout.fragment_quotes) {

    private val binding by viewBinding(FragmentQuotesBinding::bind)
    private val viewModel: QuotesFragmentViewModel by viewModels()
    private lateinit var quotesBookAdapter: QuotesBookAdapter
    private val itemTouchInterceptor = ItemTouchInterceptor()

    companion object {
        const val SPAN_COUNT = 3
        const val NO_CLIP = 0
        const val CLIP_TOP = 1
        const val CLIP_BOTTOM = 2
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getQuoteBooks()
        bindBookAdapter()
        observeQuoteBooks()
    }

    private fun bindBookAdapter() = with(binding) {

        quotesBookAdapter = QuotesBookAdapter { book, imageView ->
            startTransitionAndOpenDetail(book, imageView)
        }

        rvImage.adapter = quotesBookAdapter
        rvImage.layoutManager = GridLayoutManager(requireContext(), SPAN_COUNT)
    }

    private fun startTransitionAndOpenDetail(
        item: QuoteBook, clickedImageView: ImageView
    ) = with(binding) {

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

        clickedImageView.alpha = 0.0f
        movingImageView.visibility = View.VISIBLE
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
                }
            })
            transitionToEnd()
        }

        motionLayout.postDelayed({
            // TODO: item will be bind
        }, 600)
    }

    private fun observeQuoteBooks() = with(binding) {
        viewModel.quoteBooksScreenUiState.observe(viewLifecycleOwner) {
            if (it.books.isNotEmpty()) {
                quotesBookAdapter.submitList(it.books) {
                    rvImage.visible()
                    progressBar.gone()
                    tvInfo.gone()
                    ivEmptyQuoteBooks.gone()
                }
            } else {
                progressBar.gone()
                rvImage.gone()
                tvInfo.visible()
                ivEmptyQuoteBooks.visible()
            }
        }
    }
}