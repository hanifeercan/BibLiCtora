package com.amineaytac.biblictora.ui.epubviewer

import android.annotation.SuppressLint
import android.net.Uri
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
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.amineaytac.biblictora.R
import com.amineaytac.biblictora.core.data.model.ReadFormats
import com.amineaytac.biblictora.core.data.model.ReadingBook
import com.amineaytac.biblictora.databinding.FragmentEpubViewerBinding
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
import nl.siegmann.epublib.domain.Book
import nl.siegmann.epublib.domain.Resource
import nl.siegmann.epublib.epub.EpubReader
import java.io.File
import java.io.FileInputStream

@AndroidEntryPoint
class EpubViewerFragment : BaseReadingFragment() {

    private lateinit var binding: FragmentEpubViewerBinding
    private val viewModel: EpubViewerViewModel by viewModels()
    private lateinit var book: Book
    private lateinit var uri: Uri
    private var lastPage = 0
    private var myBooksId = -1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        readingStyleManager = ReadingStyleManager(requireContext())
        readingStyle = readingStyleManager.loadReadingStyle()
        bindUI()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentEpubViewerBinding.inflate(inflater, container, false)
        return binding.root
    }


    private fun setupListeners() = with(binding) {
        btnAddQuote.setOnClickListener {
            val title = book.title
            val author =
                book.metadata.authors.joinToString(", ") { it.firstname + " " + it.lastname }
            val readingBook = ReadingBook(
                myBooksId, author, listOf(), "", title, ReadFormats("", ""), "", "", lastPage, 0
            )

            addQuoteToBook(webView, readingBook)
        }
        tvSetStyle.setOnClickListener {
            showReadingStyleDialog {
                setReadingStyle()
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun bindUI() = with(binding) {
        webView.gone()
        progressBar.visible()
        webView.apply {
            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                allowFileAccess = true
                allowContentAccess = true
            }
            clearCache(true)
            clearHistory()
        }
        val epubUriString = arguments?.getString("epubPath")
        try {
            if (epubUriString != null) {
                val epubUri = Uri.parse(epubUriString)
                getLastReadPage(epubUriString.toString())
                getId(epubUriString.toString())
                bindWebView(epubUriString)
                openEpubFromUri(epubUri)
                uri = epubUri
            }
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
        setupListeners()
    }

    private fun getLastReadPage(filePath: String) {
        viewModel.getLastPage(filePath)
        viewModel.lastPage.observe(viewLifecycleOwner) {
            lastPage = it
        }
    }

    private fun getId(filePath: String) {
        viewModel.getId(filePath)
        viewModel.id.observe(viewLifecycleOwner) {
            myBooksId = it
        }
    }

    private fun saveResourceToFile(resource: Resource) {
        val dir = File(requireContext().cacheDir, "epub_resources")
        if (!dir.exists()) dir.mkdir()
        val fileName = resource.href.substringAfterLast("/")
        val file = File(dir, fileName)
        file.outputStream().use { fileOutputStream ->
            fileOutputStream.write(resource.data)
        }
    }

    private fun getHtmlContentFromEpub(book: Book): String {
        val fullHtmlContent = StringBuilder()
        val cssStyle = """
        <style>
            img {
                max-width: 80%;  
                height: auto; 
                display: block; 
                margin: 10px auto; 
            }
            body {
                text-align: justify; 
                font-size: ${readingStyle.textFontSize}px; 
                line-height: 1.4; 
                padding: 10px; 
                background-color: ${readingStyle.backgroundColorHex};  
                font-family: Arial, sans-serif;  
                color: ${readingStyle.textColorHex};
            }
        </style>
    """.trimIndent()
        fullHtmlContent.append("<html><head>$cssStyle</head><body>")

        val dir = File(requireContext().cacheDir, "epub_resources")
        if (!dir.exists()) dir.mkdir()
        val imageMap = mutableMapOf<String, String>()

        for (resource in book.resources.all) {
            if (resource.mediaType.toString().startsWith("image")) {
                saveResourceToFile(resource)
                val fileName = resource.href.substringAfterLast("/")
                val file = File(dir, fileName)
                val localPath = "file://${file.absolutePath}"
                imageMap[resource.href] = localPath
            }
        }

        for (resource in book.contents) {
            var htmlPage = String(resource.data)
            for ((oldPath, newPath) in imageMap) {
                if (oldPath.contains("../")) {
                    htmlPage = htmlPage.replace(oldPath, newPath)
                    htmlPage = htmlPage.replace(oldPath.replace("../", ""), newPath)
                } else {
                    htmlPage = htmlPage.replace(oldPath, newPath)
                }
            }
            fullHtmlContent.append(htmlPage)
        }
        fullHtmlContent.append("</body></html>")
        return fullHtmlContent.toString()
    }

    private fun openEpubFromUri(uri: Uri) = with(binding) {
        requireContext().contentResolver.openInputStream(uri)?.use { inputStream ->
            val tempFile = File(requireContext().cacheDir, "temp.epub")
            tempFile.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }

            book = loadEpubBook(tempFile.path)
            val htmlContent = getHtmlContentFromEpub(book)
            val fixedHtmlContent = htmlContent.replace("../file://", "file://")
            val baseUrl = "file://${requireContext().cacheDir}/epub_resources/"
            webView.loadDataWithBaseURL(baseUrl, fixedHtmlContent, "text/html", "UTF-8", null)
        }
    }

    private fun loadEpubBook(filePath: String): Book {
        val inputStream = FileInputStream(filePath)
        return EpubReader().readEpub(inputStream)
    }

    @SuppressLint("SetJavaScriptEnabled", "ClickableViewAccessibility")
    private fun bindWebView(epubUri: String) = with(binding) {
        with(webView) {

            setOnScrollChangeListener { _, _, scrollY, _, _ ->
                viewModel.updateLastPage(epubUri, scrollY)
            }

            val gestureDetector = GestureDetector(requireContext(),
                object : GestureDetector.SimpleOnGestureListener() {
                    override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                        if (btnAddQuote.visibility == View.VISIBLE) {
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

                override fun onPageFinished(view: WebView?, url: String?) {
                    CoroutineScope(Dispatchers.Main).launch {
                        delay(1000)
                        view?.scrollTo(0, lastPage)
                        delay(300)
                        progressBar.gone()
                        webView.visible()
                    }
                }
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
}