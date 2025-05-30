package com.amineaytac.biblictora.ui.quotes

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.amineaytac.biblictora.core.data.model.QuoteItem
import com.amineaytac.biblictora.databinding.ItemQuoteBinding

class QuoteListAdapter(
    private val onDeleteClickListener: (String) -> Unit
) : ListAdapter<QuoteItem, QuoteListAdapter.ViewHolder>(COMPARATOR) {

    inner class ViewHolder(private val binding: ItemQuoteBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(quote: QuoteItem) = with(binding) {

            tvQuote.text = quote.quote
                .replace("\\n", "\n")
                .replace("\\\"", "\"")
            ivDeleteQuote.setOnClickListener {
                onDeleteClickListener.invoke(quote.quote)
            }
        }
    }

    companion object {
        private val COMPARATOR = object : DiffUtil.ItemCallback<QuoteItem>() {
            override fun areItemsTheSame(oldItem: QuoteItem, newItem: QuoteItem) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: QuoteItem,
                newItem: QuoteItem
            ) = oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemQuoteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val quote = getItem(position)
        holder.bind(quote)
    }
}