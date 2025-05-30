package com.amineaytac.biblictora.ui.basereading

import android.os.Bundle
import android.view.LayoutInflater
import android.webkit.WebView
import android.widget.SeekBar
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.amineaytac.biblictora.R
import com.amineaytac.biblictora.core.data.model.ReadingBook
import com.amineaytac.biblictora.databinding.ReadingStyleDialogLayoutBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.yagmurerdogan.toasticlib.Toastic

abstract class BaseReadingFragment : Fragment() {

    protected lateinit var readingStyleManager: ReadingStyleManager
    protected lateinit var readingStyle: ReadingStyleManager.ReadingStyle
    private val viewModel: BaseReadingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        readingStyleManager = ReadingStyleManager(requireContext())
        readingStyle = readingStyleManager.loadReadingStyle()
    }

    protected fun showReadingStyleDialog(
        onStyleChanged: (ReadingStyleManager.ReadingStyle) -> Unit
    ) {
        val dialogBinding =
            ReadingStyleDialogLayoutBinding.inflate(LayoutInflater.from(requireContext()))
        val dialog = BottomSheetDialog(requireContext())
        dialog.setContentView(dialogBinding.root)

        val imageViews = listOf(
            dialogBinding.ivLight, dialogBinding.ivMediumLight, dialogBinding.ivDark
        )

        var fontSize = readingStyle.textFontSize
        dialogBinding.seekBar.progress = fontSize

        var selectedIndex = when (readingStyle.backgroundColorHex) {
            getColorHex(R.color.white) -> 0
            getColorHex(R.color.highlight) -> 1
            getColorHex(R.color.black) -> 2
            else -> 0
        }

        fun updateBorders() {
            imageViews.forEachIndexed { index, imageView ->
                val borderColor = if (index == selectedIndex) R.color.transparent_shēn_hóng_red
                else R.color.transparent_kettleman
                imageView.borderColor = ContextCompat.getColor(requireContext(), borderColor)
            }
        }

        updateBorders()

        dialogBinding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                fontSize = progress
                dialogBinding.txtFontSizeValue.textSize = fontSize.toFloat()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        imageViews.forEachIndexed { index, imageView ->
            imageView.setOnClickListener {
                selectedIndex = index
                updateBorders()
            }
        }

        dialogBinding.btnSave.setOnClickListener {
            val themeColorRes = when (selectedIndex) {
                0 -> R.color.white
                1 -> R.color.highlight
                2 -> R.color.black
                else -> R.color.white
            }
            val backgroundColorHex = getColorHex(themeColorRes)

            val textColorRes = if (selectedIndex == 2) R.color.white else R.color.black
            val textColorHex = getColorHex(textColorRes)

            val newStyle =
                ReadingStyleManager.ReadingStyle(backgroundColorHex, textColorHex, fontSize)
            readingStyleManager.saveReadingStyle(newStyle)
            readingStyle = newStyle
            onStyleChanged(newStyle)
            dialog.dismiss()
        }

        dialogBinding.btnCancel.setOnClickListener { dialog.dismiss() }

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
    }

    private fun getColorHex(@ColorRes colorResId: Int): String {
        val colorInt = ContextCompat.getColor(requireContext(), colorResId)
        return String.format("#%06X", 0xFFFFFF and colorInt)
    }

    protected fun addQuoteToBook(
        webView: WebView, readingBook: ReadingBook
    ) {
        webView.evaluateJavascript(
            "(function() { return window.getSelection().toString(); })();"
        ) { selectedText ->
            val text = selectedText.trim('"')
            if (text.isNotEmpty() && text != "null") {
                viewModel.addQuoteToBook(readingBook, text)
                Toastic.toastic(
                    context = requireContext(),
                    message = getString(R.string.quote_added),
                    duration = Toastic.LENGTH_SHORT,
                    type = Toastic.SUCCESS,
                    isIconAnimated = true
                ).show()
            } else {
                Toastic.toastic(
                    context = requireContext(),
                    message = getString(R.string.no_text),
                    duration = Toastic.LENGTH_SHORT,
                    type = Toastic.ERROR,
                    isIconAnimated = true
                ).show()
            }
        }
    }
}
