package com.amineaytac.biblictora.ui.pdfviewer

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.amineaytac.biblictora.R
import com.amineaytac.biblictora.databinding.FragmentPdfViewerBinding
import com.amineaytac.biblictora.util.viewBinding
import com.yagmurerdogan.toasticlib.Toastic
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

@AndroidEntryPoint
class PdfViewerFragment : Fragment(R.layout.fragment_pdf_viewer) {

    private val binding by viewBinding(FragmentPdfViewerBinding::bind)
    private val viewModel: PdfViewerViewModel by viewModels()
    private var lastPage = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pdfUriString = arguments?.getString("pdfPath")

        if (!pdfUriString.isNullOrEmpty()) {
            getLastReadPage(pdfUriString)
        } else {
            showToastic(getString(R.string.pdf_not_parsed))
        }
    }

    private fun getLastReadPage(filePath: String) {
        viewModel.getLastPage(filePath)
        viewModel.lastPage.observe(viewLifecycleOwner) {
            lastPage = it
            openPdfFromUri(Uri.parse(filePath), lastPage)
        }
    }

    private fun openPdfFromUri(pdfUri: Uri, lastPage: Int) {
        try {
            val inputStream: InputStream? = requireContext().contentResolver.openInputStream(pdfUri)
            inputStream?.let {
                val tempFile = File(requireContext().cacheDir, "temp_pdf.pdf")
                val outputStream = FileOutputStream(tempFile)
                it.copyTo(outputStream)
                outputStream.close()
                it.close()

                binding.pdfView.fromFile(tempFile).enableSwipe(true).swipeHorizontal(false)
                    .enableDoubletap(true).defaultPage(lastPage).onPageChange { page, _ ->
                        this.lastPage = page
                    }.load()
            } ?: run {
                showToastic(getString(R.string.pdf_not_open))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            showToastic(getString(R.string.pdf_not_loaded))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        val pdfUriString = arguments?.getString("pdfPath")
        if (!pdfUriString.isNullOrEmpty()) {
            viewModel.updateLastPage(pdfUriString, lastPage)
        }
    }

    private fun showToastic(message: String) {
        Toastic.toastic(
            context = requireContext(),
            message = message,
            duration = Toastic.LENGTH_SHORT,
            type = Toastic.WARNING,
            isIconAnimated = true
        ).show()
    }
}