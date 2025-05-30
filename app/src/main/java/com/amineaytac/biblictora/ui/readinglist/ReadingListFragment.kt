package com.amineaytac.biblictora.ui.readinglist

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amineaytac.biblictora.R
import com.amineaytac.biblictora.databinding.FragmentReadingListBinding
import com.amineaytac.biblictora.ui.home.HomeFragmentDirections
import com.amineaytac.biblictora.util.gone
import com.amineaytac.biblictora.util.viewBinding
import com.amineaytac.biblictora.util.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReadingListFragment : Fragment(R.layout.fragment_reading_list) {

    private val binding by viewBinding(FragmentReadingListBinding::bind)
    private val viewModel: ReadingListViewModel by viewModels()
    private lateinit var bookAdapter: ReadingListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getReadingBooks()
        observeUi()
        bindBookAdapter()
    }

    private fun bindBookAdapter() = with(binding) {

        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView)

        bookAdapter = ReadingListAdapter {
            findNavController().navigate(HomeFragmentDirections.navigateToReadingFragment(it))
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = bookAdapter
    }

    private val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
        0,
        ItemTouchHelper.LEFT
    ) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean = false

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position = viewHolder.bindingAdapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val item = bookAdapter.currentList[position]
                viewModel.deleteReadingBookItems(item)
            }
        }
    }

    private fun observeUi() = with(binding) {
        viewModel.readingBookScreenUiState.observe(viewLifecycleOwner) {
            when {
                it.isError -> {
                    progressBar.gone()
                    ivEmptyImage.visible()
                }

                else -> {
                    if (it.books.isEmpty()) {
                        ivEmptyImage.visible()
                        progressBar.gone()
                    } else {
                        ivEmptyImage.gone()
                        bookAdapter.submitList(it.books) {
                            progressBar.gone()
                        }
                    }
                }
            }
        }
    }
}