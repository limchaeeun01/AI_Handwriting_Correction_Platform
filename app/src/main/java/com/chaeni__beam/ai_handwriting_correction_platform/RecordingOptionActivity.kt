package com.chaeni__beam.ai_handwriting_correction_platform

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.chaeni__beam.ai_handwriting_correction_platform.databinding.ActivityMainBinding
import com.chaeni__beam.ai_handwriting_correction_platform.databinding.ActivityRecordingOptionBinding

class RecordingOptionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecordingOptionBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecordingOptionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val selectedFont = intent.getStringExtra("selectedFont") ?: "돋움체" // 기본값 설정
        Log.d("RecordingOptionActivity", "Received selectedFont: $selectedFont")

        binding.pictureOptionBtn.setOnClickListener{
            val intent = Intent(this, PictureActivity::class.java)
            intent.putExtra("selectedFont", selectedFont) // 선택된 폰트를 전달
            startActivity(intent)
        }

        binding.drawingOptionBtn.setOnClickListener{
            val intent = Intent(this, DrawingActivity::class.java)
            intent.putExtra("selectedFont", selectedFont) // 선택된 폰트를 전달
            startActivity(intent)
        }


    }
}