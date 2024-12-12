package com.chaeni__beam.ai_handwriting_correction_platform.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.chaeni__beam.ai_handwriting_correction_platform.FontViewModel
import com.chaeni__beam.ai_handwriting_correction_platform.R
import com.chaeni__beam.ai_handwriting_correction_platform.RecordingOptionActivity
import com.chaeni__beam.ai_handwriting_correction_platform.SentenceWritingActivity
import com.chaeni__beam.ai_handwriting_correction_platform.databinding.FragmentPracticeBinding

class PracticeFragment : Fragment() {

    private lateinit var binding: FragmentPracticeBinding

    private lateinit var fontViewModel: FontViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPracticeBinding.inflate(inflater, container, false)

        fontViewModel = ViewModelProvider(
            requireActivity(), ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        ).get(FontViewModel::class.java)

        val selectedFont = fontViewModel.selectedFont

        // 뒤로가기 버튼 클릭 이벤트
        binding.backBtn.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment())  // HomeFragment로 교체
                .commit()
        }

        // sentenceWritingBtn 클릭 이벤트
        binding.sentenceWritingBtn.setOnClickListener {
            val intent = Intent(requireContext(), SentenceWritingActivity::class.java)
            intent.putExtra("selectedFont", selectedFont) // 선택된 폰트를 전달
            startActivity(intent)
        }

        return binding.root
    }
}
