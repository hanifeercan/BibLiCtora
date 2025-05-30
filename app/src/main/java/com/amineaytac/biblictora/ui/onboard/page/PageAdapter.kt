package com.amineaytac.biblictora.ui.onboard.page

import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.amineaytac.biblictora.R
import com.amineaytac.biblictora.ui.onboard.OnBoardFragment

class PageAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    private val imageList = listOf(
        R.drawable.ic_first,
        R.drawable.ic_second,
        R.drawable.ic_third,
        R.drawable.ic_fourth,
        R.drawable.ic_fifth,
        R.drawable.ic_sixth
    )

    private val descriptionList = listOf(
        R.string.screen_first,
        R.string.screen_second,
        R.string.screen_third,
        R.string.screen_fourth,
        R.string.screen_fifth,
        R.string.screen_sixth,
    )

    override fun getItemCount() = imageList.size

    override fun createFragment(position: Int) = OnBoardFragment.newInstance(
        imageList[position],
        descriptionList[position]
    )
}