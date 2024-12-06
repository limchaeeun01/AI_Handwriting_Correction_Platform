package com.chaeni__beam.ai_handwriting_correction_platform.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.chaeni__beam.ai_handwriting_correction_platform.FontViewModel
import com.chaeni__beam.ai_handwriting_correction_platform.R
import com.chaeni__beam.ai_handwriting_correction_platform.adapter.SpinnerAdapter
import com.chaeni__beam.ai_handwriting_correction_platform.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    private lateinit var fontViewModel: FontViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        fontViewModel = ViewModelProvider(requireActivity()).get(FontViewModel::class.java)

        val data = arrayOf("돋움체", "굴림체", "바탕체")
        val adapter = SpinnerAdapter(requireContext(), data)

        binding.spinner.adapter = adapter
        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedFont = parent?.getItemAtPosition(position).toString()
                fontViewModel.selectedFont = selectedFont  // ViewModel에 값 저장
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // 필요 시 처리
            }
        }

        // ViewModel에서 선택된 폰트 값 읽기
        val selectedFont = fontViewModel.selectedFont
        val position = data.indexOf(selectedFont)
        binding.spinner.setSelection(position)

        binding.basicsBtn.setOnClickListener {
            // Fragment 교체
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, BasicsFragment())  // 'fragment_container'는 교체할 컨테이너의 ID
                .addToBackStack(null)  // 백스택에 추가하여 뒤로 가기 버튼으로 이전 Fragment로 돌아갈 수 있게 함
                .commit()
        }

        binding.practiceBtn.setOnClickListener {
            // Fragment 교체
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, PracticeFragment())  // 'fragment_container'는 교체할 컨테이너의 ID
                .addToBackStack(null)  // 백스택에 추가하여 뒤로 가기 버튼으로 이전 Fragment로 돌아갈 수 있게 함
                .commit()
        }


        return binding.root
    }
}
