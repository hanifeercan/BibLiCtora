package com.amineaytac.biblictora.ui.favoritebooks

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.amineaytac.biblictora.R
import com.amineaytac.biblictora.core.data.model.Book
import com.amineaytac.biblictora.databinding.FragmentFavoriteBooksBinding
import com.amineaytac.biblictora.util.gone
import com.amineaytac.biblictora.util.visible
import com.amineaytc.biblictora.util.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoriteBooksFragment : Fragment(R.layout.fragment_favorite_books) {

    private val binding by viewBinding(FragmentFavoriteBooksBinding::bind)
    private val viewModel: FavoriteBooksViewModel by viewModels()
    private lateinit var favoriteBooksAdapter: FavoriteBooksAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getFavoriteBooks()
        bindBookAdapter()
        observeFavoriteBooks()
    }

    private fun bindBookAdapter() = with(binding) {

        favoriteBooksAdapter = FavoriteBooksAdapter(resources, {

        }, { book ->
            buildAlertDialog(book)
        })

        rvFavoriteBooks.layoutManager = GridLayoutManager(requireContext(), 2)
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
        }
    }
}