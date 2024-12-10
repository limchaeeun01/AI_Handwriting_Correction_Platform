package com.chaeni__beam.ai_handwriting_correction_platform

import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.chaeni__beam.ai_handwriting_correction_platform.adapter.GridAdapter
import com.chaeni__beam.ai_handwriting_correction_platform.databinding.ActivityGridBinding
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class GridActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGridBinding
    private var recognizedText: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGridBinding.inflate(layoutInflater)

        // Intent로 받은 recognizedText 값 확인
        recognizedText = intent.getStringExtra("recognizedText")
        setupGrid()

        binding.captureButton.setOnClickListener {
            captureGrid()
        }

        setContentView(binding.root)
    }

    private fun setupGrid() {
        // 8열 고정
        binding.gridView.numColumns = 8

        // 화면 너비 계산
        val displayMetrics = resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val cellSize = screenWidth / 8

        // GridView의 verticalSpacing과 horizontalSpacing 설정
        binding.gridView.verticalSpacing = 1
        binding.gridView.horizontalSpacing = 1

        // Adapter 설정
        val adapter = GridAdapter(recognizedText ?: "", cellSize)
        binding.gridView.adapter = adapter
    }


    private fun captureGrid() {
        // GridView의 실제 크기 가져오기
        val gridWidth = binding.gridView.width
        val gridHeight = binding.gridView.height

        // GridView를 캡쳐할 Bitmap 생성
        val bitmap = Bitmap.createBitmap(gridWidth, gridHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        binding.gridView.draw(canvas)

        // 캡쳐된 이미지를 파일로 저장
        val imageFile = File(cacheDir, "captured_grid.png")
        try {
            FileOutputStream(imageFile).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
            Toast.makeText(this, "원고지 캡쳐 완료", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "캡쳐 실패", Toast.LENGTH_SHORT).show()
        }
    }
}
