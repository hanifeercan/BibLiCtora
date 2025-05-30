package com.amineaytac.biblictora.ui.readinglist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.amineaytac.biblictora.R
import com.amineaytac.biblictora.core.data.model.ReadingBook
import com.amineaytac.biblictora.databinding.ItemReadingListBinding
import com.squareup.picasso.Picasso

class ReadingListAdapter(
    private val onItemClickListener: (item: ReadingBook) -> Unit
) : ListAdapter<ReadingBook, ReadingListAdapter.ViewHolder>(DiffCallback) {

    inner class ViewHolder(private val binding: ItemReadingListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ReadingBook) = with(binding) {

            tvName.text = item.title
            when (item.readingStates) {
                "willRead" -> {
                    tvReadingState.text = root.context.getString(R.string.will_read)
                }

                "reading" -> {
                    tvReadingState.text = root.context.getString(R.string.reading)
                }

                "haveRead" -> {
                    tvReadingState.text = root.context.getString(R.string.have_read)
                }
            }
            val percentageStr = "%${item.readingPercentage}"
            tvReadingPercentage.text = percentageStr
            progressBar.progress = item.readingPercentage

            Picasso.get().load(item.image).error(R.drawable.ic_detail_book)
                .placeholder(R.drawable.ic_detail_book).into(ivBookPicture)

            when (item.readingStates) {
                "willRead" -> {
                    ivReadingState.setBackgroundResource(R.drawable.ic_dark_will_read)
                }

                "reading" -> {
                    ivReadingState.setBackgroundResource(R.drawable.ic_dark_reading)
                }

                "haveRead" -> {
                    ivReadingState.setBackgroundResource(R.drawable.ic_dark_have_read)
                }
            }
            root.setOnClickListener {
                onItemClickListener.invoke(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemReadingListBinding.inflate(LayoutInflater.from(parent.context))
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        item.let {
            holder.bind(item)
        }
    }

    object DiffCallback : DiffUtil.ItemCallback<ReadingBook>() {
        override fun areItemsTheSame(
            oldItem: ReadingBook, newItem: ReadingBook
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: ReadingBook, newItem: ReadingBook
        ): Boolean {
            return oldItem == newItem
        }
    }
}