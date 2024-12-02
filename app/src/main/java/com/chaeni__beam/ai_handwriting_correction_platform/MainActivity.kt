package com.chaeni__beam.ai_handwriting_correction_platform

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.chaeni__beam.ai_handwriting_correction_platform.databinding.ActivityMainBinding
import com.chaeni__beam.ai_handwriting_correction_platform.fragment.HomeFragment
import com.chaeni__beam.ai_handwriting_correction_platform.fragment.RecordingFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 앱 초기 실행 시 HomeFragment로 설정
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, HomeFragment())
                .commit()
        }

    }

}
