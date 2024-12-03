package com.chaeni__beam.ai_handwriting_correction_platform

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.chaeni__beam.ai_handwriting_correction_platform.databinding.ActivityDrawingBinding
import com.chaeni__beam.ai_handwriting_correction_platform.databinding.ActivityRecordingOptionBinding

class DrawingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDrawingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDrawingBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}