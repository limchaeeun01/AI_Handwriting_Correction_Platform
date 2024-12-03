package com.chaeni__beam.ai_handwriting_correction_platform.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.chaeni__beam.ai_handwriting_correction_platform.R
import com.chaeni__beam.ai_handwriting_correction_platform.databinding.FragmentBasicsBinding

class BasicsFragment : Fragment() {

    private lateinit var binding: FragmentBasicsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBasicsBinding.inflate(inflater, container, false)

        binding.backBtn.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment())  // HomeFragment로 교체
                .commit()
        }

        return binding.root
    }
}
