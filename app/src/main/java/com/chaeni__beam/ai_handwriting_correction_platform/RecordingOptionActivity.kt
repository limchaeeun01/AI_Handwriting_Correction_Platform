package com.chaeni__beam.ai_handwriting_correction_platform

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.chaeni__beam.ai_handwriting_correction_platform.databinding.ActivityMainBinding
import com.chaeni__beam.ai_handwriting_correction_platform.databinding.ActivityRecordingOptionBinding

class RecordingOptionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecordingOptionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecordingOptionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.pictureOptionBtn.setOnClickListener{
            startActivity(Intent(this, PictureActivity::class.java))
        }


    }
}