package com.amineaytac.biblictora.ui.readinglist

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.amineaytac.biblictora.R
import com.amineaytac.biblictora.databinding.FragmentReadingListBinding
import com.amineaytac.biblictora.ui.home.HomeFragmentDirections
import com.amineaytac.biblictora.util.gone
import com.amineaytac.biblictora.util.visible
import com.amineaytc.biblictora.util.viewBinding
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

        bookAdapter = ReadingListAdapter {
            findNavController().navigate(HomeFragmentDirections.navigateToReadingFragment(it))
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = bookAdapter
    }

    private fun observeUi() = with(binding) {
        viewModel.readingBookScreenUiState.observe(viewLifecycleOwner) {
            when {
                it.isError -> {
                    tvInfo.text = it.errorMessage
                    tvInfo.visible()
                    progressBar.gone()
                }

                else -> {
                    if (it.books.isEmpty()) {
                        tvInfo.setText(R.string.reading_list_empty)
                        tvInfo.visible()
                        progressBar.gone()
                    } else {
                        tvInfo.gone()
                        bookAdapter.submitList(it.books) {
                            progressBar.gone()
                        }
                    }
                }
            }
        }
    }
}