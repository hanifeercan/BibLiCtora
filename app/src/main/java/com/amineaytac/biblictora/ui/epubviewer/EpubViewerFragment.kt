package com.amineaytac.biblictora.ui.epubviewer

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.amineaytac.biblictora.R
import com.amineaytac.biblictora.databinding.FragmentEpubViewerBinding
import com.amineaytac.biblictora.util.gone
import com.amineaytac.biblictora.util.visible
import com.amineaytc.biblictora.util.viewBinding
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
class EpubViewerFragment : Fragment(R.layout.fragment_epub_viewer) {

    private val binding by viewBinding(FragmentEpubViewerBinding::bind)
    private val viewModel: EpubViewerViewModel by viewModels()
    private lateinit var book: Book
    private lateinit var uri: Uri
    private var lastPage = 0
    private var themeColorHex = "#FFFFFF"
    private var textColorHex = "#000000"
    private var fontSize = 18
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        bindUI()
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
                getLastReadPage(epubUriString.toString()) { lastReadPage ->
                    lastPage = lastReadPage
                }
                bindWebView(epubUriString)
                openEpubFromUri(epubUri)
                uri = epubUri
            }
        } catch (e: Exception) {
            findNavController().popBackStack()
            Toastic.toastic(
                context = requireContext(),
                message = "File not exist!",
                duration = Toastic.LENGTH_SHORT,
                type = Toastic.ERROR,
                isIconAnimated = true
            ).show()
        }
    }

    private fun getLastReadPage(filePath: String, callback: (Int) -> Unit) {
        lifecycleScope.launch {
            try {
                val lastReadPage = viewModel.getLastPage(filePath)
                callback(lastReadPage)
            } catch (e: Exception) {
                callback(0)
            }
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
                font-size: ${fontSize}px; 
                line-height: 1.4; 
                padding: 10px; 
                background-color: ${themeColorHex};  
                font-family: Arial, sans-serif;  
                color: ${textColorHex};
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
}