package com.amineaytac.biblictora.ui.mybooks

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.amineaytac.biblictora.core.data.model.MyBooksItem
import com.amineaytac.biblictora.databinding.ItemMyBookBinding

class MyBooksAdapter(
    private val onMyBooksItemClickListener: (item: MyBooksItem) -> Unit
) : ListAdapter<MyBooksItem, MyBooksAdapter.ViewHolder>(
    COMPARATOR
) {

    inner class ViewHolder(private val binding: ItemMyBookBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: MyBooksItem, position: Int) = with(binding) {

            tvBookName.text = item.name

            llBookItem.setOnClickListener {
                getItem(position)?.let {
                    onMyBooksItemClickListener.invoke(it)
                }
            }
        }
    }

    companion object {
        private val COMPARATOR = object : DiffUtil.ItemCallback<MyBooksItem>() {
            override fun areItemsTheSame(
                oldItem: MyBooksItem, newItem: MyBooksItem
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: MyBooksItem, newItem: MyBooksItem
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMyBookBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        item?.let {
            holder.bind(item, position)
        }
    }
}