package com.amineaytac.biblictora.ui.discover

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amineaytac.biblictora.R
import com.amineaytac.biblictora.core.network.NetworkConnection
import com.amineaytac.biblictora.core.network.NetworkListener
import com.amineaytac.biblictora.databinding.FragmentDiscoverBinding
import com.amineaytac.biblictora.ui.discover.adapter.ChipAdapter
import com.amineaytac.biblictora.ui.discover.adapter.DiscoverBookAdapter
import com.amineaytac.biblictora.ui.discover.adapter.LoaderAdapter
import com.amineaytac.biblictora.ui.home.HomeFragmentDirections
import com.amineaytac.biblictora.util.gone
import com.amineaytac.biblictora.util.visible
import com.amineaytc.biblictora.util.viewBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.yagmurerdogan.toasticlib.Toastic
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DiscoverFragment : Fragment(R.layout.fragment_discover), NetworkListener {

    private val binding by viewBinding(FragmentDiscoverBinding::bind)
    private val viewModel: DiscoverViewModel by viewModels()
    private var isChipGroupVisible = false
    private val chips = mutableListOf<LanguageChipBox>()
    private var chipClickStates = Array(12) { false }
    private lateinit var chipAdapter: ChipAdapter
    private lateinit var bookAdapter: DiscoverBookAdapter
    private lateinit var sheetBehavior: BottomSheetBehavior<LinearLayout>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (viewModel.isFirstRest) {
            callInitialViewModelFunctions()
        }
        setComponentVisibility()
        bindBookAdapter()
        observeUi()
        bindBackDrop()
        
        val networkConnection = NetworkConnection(requireContext())
        networkConnection.observe(viewLifecycleOwner) { isConnected ->
            onNetworkStateChanged(isConnected)
        }
    }

    override fun onNetworkStateChanged(isConnected: Boolean) {

        if (!isConnected) {
            Toastic.toastic(
                context = requireContext(),
                message = getString(R.string.check_internet_connection),
                duration = Toastic.LENGTH_SHORT,
                type = Toastic.ERROR,
                isIconAnimated = true
            ).show()
        }
    }

    private fun bindChipAdapter() = with(binding) {

        chipClickStates = viewModel.getChipClickStates()
        recyclerView.setHasFixedSize(true)
        chipAdapter =
            ChipAdapter(recyclerView, requireContext(), chips, chipClickStates) { position ->
                viewModel.setChipClickListener(position)
            }

        recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
        recyclerView.adapter = chipAdapter

        chips.clear()
        val languages = resources.getStringArray(R.array.languages)
        val flagDrawableIds = resources.obtainTypedArray(R.array.flag_drawable_ids)

        languages.forEachIndexed { index, str ->
            val (id, language, abbreviation) = str.split(",")
            chips.add(
                LanguageChipBox(
                    id.toInt(), language, abbreviation, flagDrawableIds.getResourceId(index, -1)
                )
            )
        }
    }

    private fun bindBookAdapter() = with(binding) {

        bookAdapter = DiscoverBookAdapter(resources) {
            findNavController().navigate(HomeFragmentDirections.navigateToBookDetailFragment(it))
        }
        rvBook.layoutManager = GridLayoutManager(requireContext(), 2)
        rvBook.setHasFixedSize(true)
        rvBook.adapter = bookAdapter.withLoadStateHeaderAndFooter(
            header = LoaderAdapter(), footer = LoaderAdapter()
        )

        val gridLayoutManager = rvBook.layoutManager as GridLayoutManager
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                val itemCount = gridLayoutManager.itemCount

                return if (position == itemCount - 1) {
                    gridLayoutManager.spanCount
                } else {
                    1
                }
            }
        }

        bookAdapter.addLoadStateListener {
            it.refresh.let { loadState ->
                progressBar.isVisible = loadState is LoadState.Loading
                rvBook.isVisible = loadState is LoadState.NotLoading
                ivFailurePicture.gone()
                tvFailureText.gone()
                ivFailurePicture.setBackgroundResource(0)

                if (loadState is LoadState.NotLoading) {
                    if (bookAdapter.itemCount == 0) {
                        rvBook.gone()
                        ivFailurePicture.visible()
                        tvFailureText.visible()
                        floatingButton.gone()
                        ivFailurePicture.setBackgroundResource(R.drawable.ic_failure_search)
                        tvFailureText.text = getString(R.string.try_searching_again)
                    } else {
                        rvBook.visible()
                        ivFailurePicture.gone()
                        tvFailureText.gone()
                        floatingButton.visible()
                    }
                }
            }
        }
    }

    private fun bindBackDrop() = with(binding) {

        floatingButton.setOnClickListener {
            rvBook.scrollToPosition(0)
        }

        rvBook.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                swipeRefresh.isEnabled = !recyclerView.canScrollVertically(-1)
            }
        })

        bindChipAdapter()
        bindSearchView()
        isChipGroupVisible = viewModel.getChipGroupVisibility()
        checkChipGroupVisibility()

        icLanguage.setOnClickListener {
            viewModel.setChipGroupVisibility()
            isChipGroupVisible = viewModel.getChipGroupVisibility()
            checkChipGroupVisibility()
        }

        sheetBehavior = BottomSheetBehavior.from(linearLayout)
        sheetBehavior.isFitToContents = false
        sheetBehavior.isHideable = false
        sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

        btnBackdropFilter.setOnClickListener {
            toggleFilters(sheetBehavior)
        }

        btnShowResults.setOnClickListener {
            val languages = chipClickStatesToLanguageList()
            val searchText = viewModel.getSearchText()

            val inputMethodManager =
                context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0)
            setComponentVisibility()

            if (searchText.isNotEmpty()) {
                viewModel.getBooksWithSearchFlow(searchText, languages)
                viewModel.getBooksWithSearch()
            } else if (languages.isNotEmpty() && searchText.isEmpty()) {
                viewModel.getBooksWithLanguagesFlow(languages)
                viewModel.getBooksWithLanguages()
            } else {
                viewModel.getAllBooks()
            }
            toggleFilters(sheetBehavior)
        }
    }

    private fun setComponentVisibility() = with(binding) {
        ivFailurePicture.gone()
        tvFailureText.gone()
        rvBook.gone()
        floatingButton.gone()
    }

    private fun chipClickStatesToLanguageList(): List<String> {
        val ids = mutableListOf<Int>()
        chipClickStates.forEachIndexed { index, bool ->
            if (bool) {
                ids.add(index)
            }
        }
        if (ids.isEmpty()) return emptyList()

        return ids.map {
            chips[it].abbreviation
        }
    }

    private fun checkChipGroupVisibility() = with(binding) {

        if (!isChipGroupVisible) {
            recyclerView.visibility = View.GONE
            btnShowResults.visibility = View.GONE
        } else {
            recyclerView.visibility = View.VISIBLE
            btnShowResults.visibility = View.VISIBLE
        }
    }

    private fun bindSearchView() = with(binding) {

        searchView.setQuery(viewModel.getSearchText(), false)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    toggleFilters(sheetBehavior)
                    val inputMethodManager =
                        context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0)
                    setComponentVisibility()
                    if (it.isNotEmpty()) {
                        viewModel.getBooksWithSearchFlow(it, chipClickStatesToLanguageList())
                        viewModel.getBooksWithSearch()
                    } else {
                        viewModel.getAllBooks()
                    }
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    viewModel.setSearchText(it)
                }
                return true
            }
        })
    }

    private fun toggleFilters(sheetBehavior: BottomSheetBehavior<LinearLayout>) {
        if (sheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        } else {
            sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    private fun callInitialViewModelFunctions() {
        viewModel.getAllBooks()
    }

    private fun observeUi() = with(binding) {
        viewModel.bookScreenUiState.observe(viewLifecycleOwner) {
            when {
                it.isError -> {
                    progressBar.gone()
                    rvBook.gone()
                    ivFailurePicture.visible()
                    tvFailureText.visible()
                    ivFailurePicture.setBackgroundResource(R.drawable.ic_failure_connection)
                    tvFailureText.text = it.errorMessage
                }

                else -> {
                    bindBookAdapter()
                    bookAdapter.submitData(lifecycle, it.books)
                }
            }
        }
    }

    fun callViewModelFunctionsAfterSuccessfulConnection() {
        if (bookAdapter.itemCount == 0) {
            val languages = chipClickStatesToLanguageList()
            val searchText = viewModel.getSearchText()

            val inputMethodManager =
                context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0)
            setComponentVisibility()

            if (searchText.isNotEmpty()) {
                viewModel.getBooksWithSearchFlow(searchText, languages)
                viewModel.getBooksWithSearch()
            } else if (languages.isNotEmpty() && searchText.isEmpty()) {
                viewModel.getBooksWithLanguagesFlow(languages)
                viewModel.getBooksWithLanguages()
            } else {
                viewModel.getAllBooksFlow()
                viewModel.getAllBooks()
            }
        }
    }
}