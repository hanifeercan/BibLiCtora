package com.amineaytac.biblictora.ui.favorite

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.amineaytac.biblictora.R
import com.amineaytac.biblictora.databinding.FragmentFavoriteBinding
import com.amineaytac.biblictora.ui.favoritebooks.FavoriteBooksFragment
import com.amineaytac.biblictora.ui.quotes.QuotesFragment
import com.amineaytac.biblictora.util.viewBinding

class FavoriteFragment : Fragment(R.layout.fragment_favorite) {

    private val binding by viewBinding(FragmentFavoriteBinding::bind)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindTabLayout()
    }

    private fun bindTabLayout() {
        val fragmentList = arrayListOf(
            FavoriteBooksFragment(), QuotesFragment()
        )

        val adapter = FavoriteViewPagerAdapter(
            fragmentList, childFragmentManager, lifecycle
        )

        val tabLayoutMediator = adapter.createTabLayoutMediator(binding)
        binding.viewPager.adapter = adapter
        tabLayoutMediator.attach()
    }
}