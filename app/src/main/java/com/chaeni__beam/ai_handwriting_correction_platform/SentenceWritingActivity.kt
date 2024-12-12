package com.chaeni__beam.ai_handwriting_correction_platform

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.chaeni__beam.ai_handwriting_correction_platform.databinding.ActivitySentenceWritingBinding

class SentenceWritingActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySentenceWritingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySentenceWritingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val selectedFont = intent.getStringExtra("selectedFont") ?: "돋움체" // 기본값 설정
        Log.d("ActivitySentenceWritingBinding", "Received selectedFont: $selectedFont")

        // shortPracticeBtn1 클릭 시 이벤트 설정
        binding.shortPracticeBtn1.setOnClickListener {
            val intent = Intent(this, PracticeDrawingActivity::class.java)
            intent.putExtra("recognizedText", "내가 마음에 드는 글을 예쁜 글씨체로 쓰고 싶다.")
            intent.putExtra("selectedFont", selectedFont) // 선택된 폰트를 전달
            startActivity(intent)
        }
    }
}
