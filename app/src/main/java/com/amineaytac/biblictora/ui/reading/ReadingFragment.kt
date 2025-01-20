package com.amineaytac.biblictora.ui.reading

import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.distinctUntilChanged
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.amineaytac.biblictora.R
import com.amineaytac.biblictora.core.data.model.Book
import com.amineaytac.biblictora.core.data.model.ReadingBook
import com.amineaytac.biblictora.core.data.repo.toReadingBook
import com.amineaytac.biblictora.databinding.FragmentReadingBinding
import com.amineaytc.biblictora.util.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ReadingFragment : Fragment(R.layout.fragment_reading) {

    private val binding by viewBinding(FragmentReadingBinding::bind)
    private val viewModel: ReadingViewModel by viewModels()
    private val args: ReadingFragmentArgs by navArgs()
    private lateinit var readingBook: ReadingBook

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setListeners()
        readingBook = args.readingBook
        observeIsBookItemReading(readingBook.id.toString())
        bindWebView(readingBook)
    }

    private fun setListeners() = with(binding) {
        toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        llBookDetail.setOnClickListener {
            readingBook.apply {
                val book = Book(id, authors, bookshelves, languages, title, formats, image)
                findNavController().navigate(
                    ReadingFragmentDirections.navigateToBookDetailFragment(
                        book
                    )
                )
            }
        }
    }

    private fun bindWebView(book: ReadingBook) {
        with(binding.webView) {
            val url = if (book.formats.textHtml.isNotEmpty() && book.formats.textHtml != "null") {
                book.formats.textHtml
            } else if (book.formats.textHtmlUtf8.isNotEmpty() && book.formats.textHtmlUtf8 != "null") {
                book.formats.textHtmlUtf8
            } else {
                null
            }

            if (url != null) {
                loadUrl(url)

                settings.textZoom = 90

                setOnScrollChangeListener { _, _, scrollY, _, _ ->
                    val maxScroll = (contentHeight * this.scale - height).toInt()
                    val progressPercentage = (scrollY.toFloat() / maxScroll * 100).toInt()
                    viewModel.updatePercentage(book.id, progressPercentage, scrollY)
                }

                webViewClient = object : WebViewClient() {

                    @Deprecated("Deprecated in Java")
                    override fun shouldOverrideUrlLoading(view: WebView?, url: String): Boolean {
                        view?.loadUrl(url)
                        return true
                    }

                    override fun onPageFinished(view: WebView?, url: String?) {
                        CoroutineScope(Dispatchers.Main).launch {
                            delay(1000)
                            view?.scrollTo(0, readingBook.readingProgress)
                        }
                    }
                }

                webChromeClient = object : WebChromeClient() {
                    override fun onProgressChanged(view: WebView?, newProgress: Int) {
                        binding.progress.progress = newProgress

                        if (newProgress == 100) {
                            binding.progress.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }

    private fun observeGetItemReading(id: String) {
        viewModel.getBookItemReading(id).distinctUntilChanged().observe(viewLifecycleOwner) {
            readingBook = it.toReadingBook()
        }
    }

    private fun observeIsBookItemReading(id: String) {
        viewModel.isBookItemReading(id).distinctUntilChanged().observe(viewLifecycleOwner) {
            if (it) {
                observeGetItemReading(readingBook.id.toString())
            } else {
                Toast.makeText(
                    requireContext(), R.string.reading_book_error, Toast.LENGTH_LONG
                ).show()
                findNavController().popBackStack()
            }
        }
    }
}
