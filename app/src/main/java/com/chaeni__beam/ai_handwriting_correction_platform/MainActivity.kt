package com.chaeni__beam.ai_handwriting_correction_platform

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.chaeni__beam.ai_handwriting_correction_platform.databinding.ActivityMainBinding
import com.chaeni__beam.ai_handwriting_correction_platform.fragment.BasicsFragment
import com.chaeni__beam.ai_handwriting_correction_platform.fragment.HomeFragment
import com.chaeni__beam.ai_handwriting_correction_platform.fragment.RecordingFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) { // 첫 번째 로딩일 때만
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment())  // HomeFragment를 기본으로 추가
                .commit()
        }

        binding.recordingBtn.setOnClickListener{
            startActivity(Intent(this, RecordingOptionActivity::class.java))
        }


    }

    override fun onBackPressed() {
        val fragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        if (fragment is BasicsFragment) {
            // 만약 현재 보여지는 Fragment가 BasicsFragment라면, HomeFragment로 돌아가도록 설정
            supportFragmentManager.popBackStack() // 백스택에서 마지막 Fragment 제거 (이 경우 HomeFragment로 돌아감)
        } else {
            super.onBackPressed() // 그 외의 경우, 기본 뒤로 가기 동작
        }
    }


}
