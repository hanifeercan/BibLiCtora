package com.amineaytac.biblictora.ui.mybooks

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amineaytac.biblictora.R
import com.amineaytac.biblictora.core.data.model.MyBooksItem
import com.amineaytac.biblictora.databinding.FragmentMyBooksBinding
import com.amineaytac.biblictora.databinding.RenameDialogLayoutBinding
import com.amineaytac.biblictora.util.gone
import com.amineaytac.biblictora.util.viewBinding
import com.amineaytac.biblictora.util.visible
import com.yagmurerdogan.toasticlib.Toastic
import dagger.hilt.android.AndroidEntryPoint
import java.util.UUID

@AndroidEntryPoint
class MyBooksFragment : Fragment(R.layout.fragment_my_books) {

    private val binding by viewBinding(FragmentMyBooksBinding::bind)
    private val myBooksViewModel: MyBooksViewModel by viewModels()
    private lateinit var myBooksAdapter: MyBooksAdapter
    private var lastPage = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        myBooksViewModel.getFiles()
        setupRecyclerView()
        observeFiles()
        setupClickListeners()
        swipeToGesture(binding.rvMyBooks)
    }

    private val pickFileLauncher =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
            uri?.let { selectedUri ->
                requireContext().contentResolver.takePersistableUriPermission(
                    selectedUri, Intent.FLAG_GRANT_READ_URI_PERMISSION
                )

                val fileName = getFileNameFromUri(selectedUri)

                when (getFileExtension(fileName)) {
                    "pdf" -> {
                        showRenameDialog(selectedUri, fileName, "pdf")
                    }

                    "epub" -> {
                        showRenameDialog(selectedUri, fileName, "epub")
                    }

                    else -> {
                        Toastic.toastic(
                            context = requireContext(),
                            message = getString(R.string.unsupported_file),
                            duration = Toastic.LENGTH_SHORT,
                            type = Toastic.WARNING,
                            isIconAnimated = true
                        ).show()
                    }
                }
            } ?: Toastic.toastic(
                context = requireContext(),
                message = getString(R.string.could_not_file),
                duration = Toastic.LENGTH_SHORT,
                type = Toastic.WARNING,
                isIconAnimated = true
            ).show()
        }

    private fun getFileExtension(fileName: String): String {
        return fileName.substringAfterLast('.', "")
    }

    private fun showRenameDialog(selectedUri: Uri, defaultName: String, fileType: String) {
        val binding = RenameDialogLayoutBinding.inflate(LayoutInflater.from(requireContext()))

        binding.etPdfName.setText(defaultName)

        val dialog = AlertDialog.Builder(requireContext()).setView(binding.root).create()

        binding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        binding.btnSave.setOnClickListener {
            val userInput = binding.etPdfName.text.toString().trim()
            val finalName = userInput.ifEmpty { defaultName }
            val filePath = selectedUri.toString()

            getLastReadPage(filePath)
            val fileItem = MyBooksItem(
                id = (UUID.randomUUID().mostSignificantBits % Int.MAX_VALUE).toInt(),
                name = finalName,
                filePath = filePath,
                fileType = fileType,
                lastPage = lastPage
            )

            myBooksViewModel.addFileItem(fileItem)
            dialog.dismiss()
        }

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
    }

    private fun getLastReadPage(filePath: String) {
        myBooksViewModel.getLastPage(filePath)
        myBooksViewModel.lastPage.observe(viewLifecycleOwner) {
            lastPage = it
        }
    }

    private fun setupRecyclerView() {
        myBooksAdapter = MyBooksAdapter(onMyBooksItemClickListener = { item ->
            if (item.fileType == "epub") {
                openEpub(Uri.parse(item.filePath))
            } else {
                openPdf(item)
            }
        })
        binding.rvMyBooks.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMyBooks.adapter = myBooksAdapter
    }

    private fun observeFiles() = with(binding) {
        myBooksViewModel.myBooksScreenUiState.observe(viewLifecycleOwner) {
            if (it.pdfs.isNotEmpty()) {
                myBooksAdapter.submitList(it.pdfs)
                rvMyBooks.visible()
                tvEmptyDescrp.gone()
                ivEmptyImage.gone()
            } else {
                rvMyBooks.gone()
                tvEmptyDescrp.visible()
                ivEmptyImage.visible()
            }
        }
    }

    private fun openEpub(epubUri: Uri) {
        val action = MyBooksFragmentDirections.navigateToEpubViewerFragment(epubUri.toString())
        findNavController().navigate(action)
    }

    private fun openPdf(myBooksItem: MyBooksItem) {
        val action =
            MyBooksFragmentDirections.actionMyBooksFragmentToPdfViewerFragment(myBooksItem.filePath)
        findNavController().navigate(action)
    }

    private fun setupClickListeners() = with(binding) {
        fbAddBook.setOnClickListener {
            pickFileLauncher.launch(arrayOf("application/pdf", "application/epub+zip"))
        }
    }

    private fun getFileNameFromUri(uri: Uri): String {
        val documentFile = DocumentFile.fromSingleUri(requireContext(), uri)
        return documentFile?.name ?: "Unknown.pdf"
    }

    private fun swipeToGesture(itemRv: RecyclerView?) = with(binding) {
        val swipeGesture = object : SwipeGesture(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val deletedItem =
                        myBooksViewModel.myBooksScreenUiState.value?.pdfs?.get(position)
                    deletedItem?.let {
                        myBooksViewModel.deleteFileItem(it)
                    }
                    val newList =
                        myBooksViewModel.myBooksScreenUiState.value?.pdfs?.toMutableList()?.apply {
                            removeAt(position)
                        }
                    myBooksAdapter.submitList(newList)
                }
            }
        }
        val touchHelper = ItemTouchHelper(swipeGesture)
        touchHelper.attachToRecyclerView(itemRv)
    }
}