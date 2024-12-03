package com.chaeni__beam.ai_handwriting_correction_platform.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.navigation.fragment.findNavController
import com.chaeni__beam.ai_handwriting_correction_platform.R
import com.chaeni__beam.ai_handwriting_correction_platform.adapter.SpinnerAdapter
import com.chaeni__beam.ai_handwriting_correction_platform.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        val data = arrayOf("Spring", "Summer", "Fall", "Winter")
        val adapter = SpinnerAdapter(requireContext(), data)

        binding.spinner.adapter = adapter
        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                // 안전하게 처리
                view?.let {
                    // 필요시 작업 수행
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // 필요 시 처리
            }
        }

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
