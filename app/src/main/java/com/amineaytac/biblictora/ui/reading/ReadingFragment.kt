package com.amineaytac.biblictora.ui.reading

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.view.ActionMode
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.viewModels
import androidx.lifecycle.distinctUntilChanged
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.amineaytac.biblictora.R
import com.amineaytac.biblictora.core.data.model.Book
import com.amineaytac.biblictora.core.data.model.ReadingBook
import com.amineaytac.biblictora.core.network.NetworkConnection
import com.amineaytac.biblictora.core.network.NetworkListener
import com.amineaytac.biblictora.databinding.FragmentReadingBinding
import com.amineaytac.biblictora.ui.basereading.BaseReadingFragment
import com.amineaytac.biblictora.ui.basereading.ReadingStyleManager
import com.amineaytac.biblictora.util.gone
import com.amineaytac.biblictora.util.visible
import com.yagmurerdogan.toasticlib.Toastic
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.core.view.isVisible

@Suppress("DEPRECATION")
@AndroidEntryPoint
class ReadingFragment : BaseReadingFragment(), NetworkListener {

    private lateinit var binding: FragmentReadingBinding
    private val viewModel: ReadingViewModel by viewModels()
    private val args: ReadingFragmentArgs by navArgs()
    private lateinit var readingBook: ReadingBook
    private var userIsTouching = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentReadingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            readingStyleManager = ReadingStyleManager(requireContext())
            readingStyle = readingStyleManager.loadReadingStyle()
            binding.webView.gone()
            binding.progressBar.visible()
            setListeners()
            readingBook = args.readingBook
            observeIsBookItemReading(readingBook.id.toString())
            bindWebView(readingBook)
        } catch (e: Exception) {
            findNavController().popBackStack()
            Toastic.toastic(
                context = requireContext(),
                message = getString(R.string.file_not_exist),
                duration = Toastic.LENGTH_SHORT,
                type = Toastic.ERROR,
                isIconAnimated = true
            ).show()
        }

        val networkConnection = NetworkConnection(requireContext())
        networkConnection.observe(viewLifecycleOwner) { isConnected ->
            onNetworkStateChanged(isConnected)
        }
    }

    private fun setListeners() = with(binding) {
        toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        ivBookDetail.setOnClickListener {
            readingBook.apply {
                val book = Book(id, authors, bookshelves, languages, title, formats, image)
                findNavController().navigate(
                    ReadingFragmentDirections.navigateToBookDetailFragment(
                        book
                    )
                )
            }
        }
        btnAddQuote.setOnClickListener {
            addQuoteToBook(webView, readingBook)
        }
        tvSetStyle.setOnClickListener {
            showReadingStyleDialog {
                setReadingStyle()
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility", "SetJavaScriptEnabled")
    private fun bindWebView(book: ReadingBook) = with(binding) {
        with(webView) {
            val url = if (book.formats.textHtml.isNotEmpty() && book.formats.textHtml != "null") {
                book.formats.textHtml
            } else if (book.formats.textHtmlUtf8.isNotEmpty() && book.formats.textHtmlUtf8 != "null") {
                book.formats.textHtmlUtf8
            } else {
                null
            }

            if (url != null) {
                loadUrl(url)
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true

                val gestureDetector = GestureDetector(
                    requireContext(),
                    object : GestureDetector.SimpleOnGestureListener() {
                        override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                            if (btnAddQuote.isVisible) {
                                btnAddQuote.gone()
                            }
                            return true
                        }
                    })

                setOnLongClickListener {
                    startActionMode(actionModeCallback)
                    setOnTouchListener { _, motionEvent ->
                        gestureDetector.onTouchEvent(motionEvent)
                        false
                    }
                    false
                }

                webViewClient = object : WebViewClient() {

                    @Deprecated("Deprecated in Java")
                    override fun shouldOverrideUrlLoading(view: WebView?, url: String): Boolean {
                        view?.loadUrl(url)
                        return true
                    }

                    var scrollRestored = false
                    override fun onPageFinished(view: WebView?, url: String?) {
                        if (!scrollRestored) {
                            scrollRestored = true
                            CoroutineScope(Dispatchers.Main).launch {
                                delay(1000)
                                view?.scrollTo(0, readingBook.readingProgress)
                                setReadingStyle()
                                progressBar.gone()
                                webView.visible()
                                setupUserScrollListener()
                            }
                        }
                    }

                    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                        super.onPageStarted(view, url, favicon)
                    }
                }
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupUserScrollListener() = with(binding.webView) {
        setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> userIsTouching = true
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> userIsTouching = false
            }
            false
        }

        setOnScrollChangeListener { _, _, scrollY, _, _ ->
            if (userIsTouching) {
                val maxScroll = (contentHeight * scale - height).toInt()
                if (maxScroll <= 0) return@setOnScrollChangeListener
                val progressPercentage =
                    ((scrollY.toFloat() / maxScroll) * 100).toInt().coerceIn(0, 100)
                viewModel.updatePercentage(readingBook.id, progressPercentage, scrollY)
            }
        }
    }

    private fun observeGetItemReading(id: String) {
        viewModel.getBookItemReading(id).distinctUntilChanged().observe(viewLifecycleOwner) { readingBookResult ->
            readingBookResult?.let { readingBook = it }
        }
    }

    private fun observeIsBookItemReading(id: String) {
        viewModel.isBookItemReading(id).distinctUntilChanged().observe(viewLifecycleOwner) {
            if (it) {
                observeGetItemReading(readingBook.id.toString())
            } else {
                Toastic.toastic(
                    context = requireContext(),
                    message = getString(R.string.reading_book_error),
                    duration = Toastic.LENGTH_SHORT,
                    type = Toastic.ERROR,
                    isIconAnimated = true
                ).show()
                findNavController().popBackStack()
            }
        }
    }

    private val actionModeCallback = object : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            binding.btnAddQuote.visible()
            return false
        }

        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            return false
        }

        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
            return false
        }

        override fun onDestroyActionMode(mode: ActionMode?) {}
    }

    private fun setReadingStyle() {
        binding.webView.evaluateJavascript(
            """
                (function() {
                    var style = document.createElement('style');
                    style.innerHTML = `
                        body {
                            font-size: ${readingStyle.textFontSize}px;
                            background-color: ${readingStyle.backgroundColorHex};
                            color: ${readingStyle.textColorHex};
                        }
                    `;
                    document.head.appendChild(style);
                })();
            """.trimIndent(), null
        )
    }

    override fun onNetworkStateChanged(isConnected: Boolean) {
        if (!isConnected) {
            onInternetDisconnected(requireContext())
        } else {
            onInternetConnected(requireContext())
        }
    }

    override fun onInternetConnected(context: Context) {
        bindWebView(readingBook)
    }
}