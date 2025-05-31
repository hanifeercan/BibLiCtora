package com.amineaytac.biblictora.ui.favoritebooks

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.amineaytac.biblictora.R
import com.amineaytac.biblictora.core.data.model.Book
import com.amineaytac.biblictora.databinding.ItemFavoriteBookBinding

class FavoriteBooksAdapter(
    private val onBookClickListener: (item: Book, ImageView) -> Unit,
    private val onHeartClickListener: (item: Book) -> Unit
) : ListAdapter<Book, FavoriteBooksAdapter.ViewHolder>(
    COMPARATOR
) {

    inner class ViewHolder(private val binding: ItemFavoriteBookBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Book, position: Int) = with(binding) {

            tvName.text = item.title
            ivBook.load(item.image) {
                error(R.drawable.ic_detail_book)
            }

            heartView.setOnClickListener {
                getItem(position)?.let {
                    onHeartClickListener.invoke(it)
                }
            }
            ivBook.setOnClickListener {
                getItem(position)?.let {
                    onBookClickListener.invoke(it, ivBook)
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
        val binding = ItemFavoriteBookBinding.inflate(LayoutInflater.from(parent.context))
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        item?.let {
            holder.bind(item, position)
        }
    }
}