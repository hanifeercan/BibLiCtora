package com.amineaytac.biblictora.ui.splash

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.amineaytac.biblictora.R
import com.amineaytac.biblictora.databinding.FragmentSplashBinding

class SplashFragment : Fragment() {

    private lateinit var binding: FragmentSplashBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSplashBinding.inflate(inflater, container, false)

        Handler(Looper.getMainLooper()).postDelayed({

            if (onBoardingIsFinished()) {
                findNavController().navigate(R.id.action_splashFragment_to_homeFragment)
            } else {
                findNavController().navigate(R.id.action_splashFragment_to_pageFragment)
            }
        }, 5000)

        return binding.root
    }

    private fun onBoardingIsFinished(): Boolean {
        val sharedPreferences =
            requireActivity().getSharedPreferences(
                getString(R.string.on_boarding),
                Context.MODE_PRIVATE
            )
        return sharedPreferences.getBoolean("finished", false)
    }
}