package com.amineaytac.biblictora.ui.discover.adapter

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.amineaytac.biblictora.R
import com.amineaytac.biblictora.core.data.model.Book
import com.amineaytac.biblictora.databinding.ItemDiscoverBookBinding
import com.amineaytac.biblictora.util.gone
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target

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
                override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                    pictureView.setBitmap(bitmap)
                    progressBarPicture.gone()
                }

                override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                    pictureView.setBitmap(
                        BitmapFactory.decodeResource(
                            resources, R.drawable.ic_failure_book_picture
                        )
                    )
                    progressBarPicture.gone()
                }

                override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}
            }

            Picasso.get().load(item.image).into(imageTarget)

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