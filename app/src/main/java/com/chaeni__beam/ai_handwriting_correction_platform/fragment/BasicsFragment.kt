package com.chaeni__beam.ai_handwriting_correction_platform.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chaeni__beam.ai_handwriting_correction_platform.R
import com.chaeni__beam.ai_handwriting_correction_platform.databinding.FragmentBasicsBinding
import com.chaeni__beam.ai_handwriting_correction_platform.databinding.FragmentHomeBinding


class BasicsFragment : Fragment() {

    lateinit var binding: FragmentBasicsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBasicsBinding.inflate(inflater, container, false)

        return binding.root
    }


}