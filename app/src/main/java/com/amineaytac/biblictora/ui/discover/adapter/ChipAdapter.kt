package com.amineaytac.biblictora.ui.discover.adapter

import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amineaytac.biblictora.R
import com.amineaytac.biblictora.databinding.ItemChipBinding
import com.amineaytac.biblictora.ui.discover.LanguageChipBox
import com.google.android.material.chip.ChipDrawable

class ChipAdapter(
    val recyclerView: RecyclerView,
    private val chips: MutableList<LanguageChipBox>,
    private val chipClickStates: Array<Boolean>,
    private val onChipClickListener: (position: Int) -> Unit
) : RecyclerView.Adapter<ChipAdapter.ChipViewHolder>() {
    inner class ChipViewHolder(private val binding: ItemChipBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: LanguageChipBox, position: Int) = with(binding) {

            chip.text = item.text

            val chipDrawable = if (chipClickStates[position]) {
                ChipDrawable.createFromAttributes(
                    binding.root.context, null, 0, R.style.ClickedChip
                )
            } else {
                ChipDrawable.createFromAttributes(
                    binding.root.context, null, 0, R.style.NotClickedChip
                )
            }
            chip.setChipDrawable(chipDrawable)

            chip.isChipIconVisible = true
            chip.chipIcon = AppCompatResources.getDrawable(root.context, item.iconFlag)

            val column = position % (recyclerView.layoutManager as GridLayoutManager).spanCount
            val layoutParams = chip.layoutParams as FrameLayout.LayoutParams

            when (column) {
                0 -> {
                    layoutParams.gravity = Gravity.END
                    chip.layoutParams = layoutParams
                }

                1 -> {
                    layoutParams.gravity = Gravity.CENTER
                    chip.layoutParams = layoutParams
                }

                else -> {
                    layoutParams.gravity = Gravity.START
                    chip.layoutParams = layoutParams
                }
            }

            chip.setOnClickListener {
                onChipClickListener.invoke(position)
                this@ChipAdapter.notifyItemChanged(position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChipViewHolder {
        val binding = ItemChipBinding.inflate(LayoutInflater.from(parent.context))
        return ChipViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChipViewHolder, position: Int) {
        val item = chips[position]
        item.let {
            holder.bind(item, position)
        }
    }

    override fun getItemCount(): Int {
        return chips.size
    }
}