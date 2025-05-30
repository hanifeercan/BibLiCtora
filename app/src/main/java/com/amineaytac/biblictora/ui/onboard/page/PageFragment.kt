package com.amineaytac.biblictora.ui.onboard.page

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getColor
import androidx.core.content.ContextCompat.getColorStateList
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.amineaytac.biblictora.R
import com.amineaytac.biblictora.databinding.FragmentPageBinding

class PageFragment : Fragment() {

    private lateinit var binding: FragmentPageBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPageBinding.inflate(inflater, container, false)

        setupViewPager()

        binding.icCancel.setOnClickListener {
            markOnboardingFinished()
            findNavController().navigate(R.id.action_pageFragment_to_homeFragment)
        }

        return binding.root
    }

    private fun setupViewPager() {
        val pageAdapter = PageAdapter(requireActivity().supportFragmentManager, lifecycle)
        binding.viewPager.adapter = pageAdapter
        binding.dotsIndicator.attachTo(binding.viewPager)

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                handlePageSelection(position)
            }
        })
    }

    private fun handlePageSelection(position: Int) {
        if (position in 0..4) {
            setupNextButton(
                textResId = R.string.next,
                backgroundColorResId = R.color.toad,
                textColorResId = R.color.moselle_green
            ) {
                binding.viewPager.currentItem++
            }
        } else {
            setupNextButton(
                textResId = R.string.got_it,
                backgroundColorResId = R.color.moselle_green,
                textColorResId = R.color.toad
            ) {
                markOnboardingFinished()
                findNavController().navigate(R.id.action_pageFragment_to_homeFragment)
            }
        }
    }

    private fun setupNextButton(
        textResId: Int,
        backgroundColorResId: Int,
        textColorResId: Int,
        onClick: () -> Unit
    ) {
        with(binding.btnNext) {
            text = getString(textResId)
            backgroundTintList = getColorStateList(requireContext(), backgroundColorResId)
            setTextColor(getColor(requireContext(), textColorResId))
            setOnClickListener { onClick() }
        }
    }

    private fun markOnboardingFinished() {
        val sharedPreferences = requireActivity().getSharedPreferences(
            getString(R.string.on_boarding),
            Context.MODE_PRIVATE
        )
        sharedPreferences.edit().apply {
            putBoolean(getString(R.string.finished), true)
            apply()
        }
    }
}