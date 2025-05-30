package com.amineaytac.biblictora.ui.home

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.amineaytac.biblictora.databinding.FragmentHomeBinding
import com.google.android.material.tabs.TabLayoutMediator

class ViewPagerAdapter(
    list: ArrayList<Fragment>,
    fm: FragmentManager,
    lifecycle: Lifecycle
) : FragmentStateAdapter(fm, lifecycle) {

    private val fragmentList = list

    override fun getItemCount(): Int {
        return fragmentList.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }

    fun createTabLayoutMediator(binding: FragmentHomeBinding): TabLayoutMediator {
        return TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "Discover"
                1 -> tab.text = "Reading List"
                else -> null
            }
        }
    }
}