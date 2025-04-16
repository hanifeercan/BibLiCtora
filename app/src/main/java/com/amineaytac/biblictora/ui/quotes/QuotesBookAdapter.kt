package com.amineaytac.biblictora.ui.quotes

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.amineaytac.biblictora.R
import com.amineaytac.biblictora.core.data.model.QuoteBook
import com.amineaytac.biblictora.databinding.ItemQuoteListBinding
import com.squareup.picasso.Picasso

class QuotesBookAdapter(
    private val onBookClickListener: (item: QuoteBook, ImageView) -> Unit
) : ListAdapter<QuoteBook, QuotesBookAdapter.ViewHolder>(
    COMPARATOR
) {

    inner class ViewHolder(private val binding: ItemQuoteListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: QuoteBook, position: Int) = with(binding) {

            if (item.image.isEmpty()) {
                ivBook.setImageResource(R.drawable.ic_detail_book)
            } else {
                Picasso.get().load(item.image).error(R.drawable.ic_detail_book).into(ivBook)
            }

            tvName.text = item.title
            ivBook.setOnClickListener {
                getItem(position)?.let {
                    onBookClickListener.invoke(it, ivBook)
                }
            }
        }
    }

    fun containsId(id: Int): Boolean {
        return currentList.any { it.id == id }
    }

    companion object {
        private val COMPARATOR = object : DiffUtil.ItemCallback<QuoteBook>() {
            override fun areItemsTheSame(
                oldItem: QuoteBook, newItem: QuoteBook
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: QuoteBook, newItem: QuoteBook
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemQuoteListBinding.inflate(LayoutInflater.from(parent.context))
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        item?.let {
            holder.bind(item, position)
        }
    }
}