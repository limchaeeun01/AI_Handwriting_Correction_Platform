package com.chaeni__beam.ai_handwriting_correction_platform.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chaeni__beam.ai_handwriting_correction_platform.R
import com.chaeni__beam.ai_handwriting_correction_platform.databinding.FragmentHomeBinding
import com.chaeni__beam.ai_handwriting_correction_platform.databinding.FragmentModalBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class ModalBottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentModalBottomSheetBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentModalBottomSheetBinding.inflate(inflater, container, false)

        binding.closeButton.setOnClickListener {
            dismiss()
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        // Adjust the height of the bottom sheet to be swiped up/down
        val dialog = dialog
        if (dialog != null) {
            val bottomSheet = dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            val behavior = com.google.android.material.bottomsheet.BottomSheetBehavior.from(bottomSheet!!)
            behavior.state = com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_HALF_EXPANDED // Default state
            behavior.isDraggable = true  // Enable swipe functionality
        }
    }


}