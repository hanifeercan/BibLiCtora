package com.amineaytac.biblictora.ui.onboard

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.amineaytac.biblictora.databinding.FragmentOnBoardBinding

class OnBoardFragment : Fragment() {

    private lateinit var binding: FragmentOnBoardBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOnBoardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            arguments?.let {
                imageView.setImageResource(it.getInt(IMAGE))
                textView.text = requireContext().getString(it.getInt(DESCRIPTION))
            }
        }
        onBoardingFinished()
    }

    companion object {

        private const val IMAGE = "image"
        private const val DESCRIPTION = "description"

        @JvmStatic
        fun newInstance(image: Int, description: Int) =
            OnBoardFragment().apply {
                arguments = Bundle().apply {
                    putInt(IMAGE, image)
                    putInt(DESCRIPTION, description)
                }
            }
    }

    private fun onBoardingFinished() {
        val sharedPreferences =
            requireActivity().getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        val edit = sharedPreferences.edit()
        edit.putBoolean("Finished", true)
        edit.apply()
    }
}