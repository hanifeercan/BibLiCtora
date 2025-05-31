package com.amineaytac.biblictora.ui.discover.adapter

import android.content.res.Resources
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import coil.request.ImageRequest
import coil.target.Target
import com.amineaytac.biblictora.R
import com.amineaytac.biblictora.core.data.model.Book
import com.amineaytac.biblictora.databinding.ItemDiscoverBookBinding
import com.amineaytac.biblictora.util.gone

class DiscoverBookAdapter(
    private val resources: Resources, private val onBookClickListener: (item: Book) -> Unit
) : PagingDataAdapter<Book, DiscoverBookAdapter.ViewHolder>(
    COMPARATOR
) {

    inner class ViewHolder(private val binding: ItemDiscoverBookBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Book, position: Int) = with(binding) {

            setIsRecyclable(false)
            tvName.text = item.title
            tvWriter.text = item.authors

            val imageTarget = object : Target {
                override fun onStart(placeholder: Drawable?) {}

                override fun onSuccess(result: Drawable) {
                    if (result is android.graphics.drawable.BitmapDrawable) {
                        pictureView.setBitmap(result.bitmap)
                    }
                    progressBarPicture.gone()
                }

                override fun onError(error: Drawable?) {
                    pictureView.setBitmap(
                        BitmapFactory.decodeResource(
                            resources, R.drawable.ic_failure_book_picture
                        )
                    )
                    progressBarPicture.gone()
                }
            }

            val imageLoader = ImageLoader(binding.root.context)
            val request = ImageRequest.Builder(binding.root.context)
                .data(item.image)
                .target(imageTarget)
                .build()
            imageLoader.enqueue(request)

            bookItemLayout.setOnClickListener {
                getItem(position)?.let {
                    onBookClickListener.invoke(it)
                }
            }
        }
    }

    companion object {
        private val COMPARATOR = object : DiffUtil.ItemCallback<Book>() {
            override fun areItemsTheSame(
                oldItem: Book, newItem: Book
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: Book, newItem: Book
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemDiscoverBookBinding.inflate(LayoutInflater.from(parent.context))
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        item?.let {
            holder.bind(item, position)
        }
    }
}