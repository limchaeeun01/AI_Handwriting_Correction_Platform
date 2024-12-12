package com.chaeni__beam.ai_handwriting_correction_platform

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.chaeni__beam.ai_handwriting_correction_platform.adapter.GridAdapter
import com.chaeni__beam.ai_handwriting_correction_platform.databinding.ActivityPracticeDrawingBinding
import java.io.File
import java.io.FileOutputStream

class PracticeDrawingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPracticeDrawingBinding
    private var recognizedText: String? = null
    private lateinit var selectedFont: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPracticeDrawingBinding.inflate(layoutInflater)

        selectedFont = intent.getStringExtra("selectedFont") ?: "돋움체" // 기본값 설정
        Log.d("PracticeDrawingActivity", "Received selectedFont: $selectedFont")

        recognizedText = intent.getStringExtra("recognizedText")

        // GridView 초기화
        setupGrid(selectedFont)

        binding.drawingBoardView.setGridSize(5, 7)

        binding.resetBtn.setOnClickListener {
            binding.drawingBoardView.clearCanvas() // 그림판 초기화 메서드 호출
            Toast.makeText(this, "그림판이 초기화되었습니다.", Toast.LENGTH_SHORT).show()
        }

        adjustDrawingBoardViewSize()

        binding.recordingBtn.setOnClickListener {
            processAndSendComparison()
        }

        setContentView(binding.root)
    }

    private fun setupGrid(selectedFont: String) {
        binding.gridView.numColumns = 7 // 7열 고정

        val displayMetrics = resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val cellSize = screenWidth / 7

        // GridView 간격 설정
        binding.gridView.verticalSpacing = 1
        binding.gridView.horizontalSpacing = 1

        // GridView 어댑터 설정
        val adapter = GridAdapter(this, recognizedText ?: "", cellSize, selectedFont)
        binding.gridView.adapter = adapter
    }


    private fun adjustDrawingBoardViewSize() {
        val drawingBoardView = binding.drawingBoardView

        // View가 완전히 준비된 후 크기를 조정하는 방법
        drawingBoardView.post {
            val screenWidth = drawingBoardView.width
            val screenHeight = (screenWidth * 5) / 7

            // 높이를 계산하여 설정
            val layoutParams = drawingBoardView.layoutParams
            layoutParams.height = screenHeight
            drawingBoardView.layoutParams = layoutParams
        }
    }

    private fun processAndSendComparison() {
        // 1. 캡처된 Bitmap 생성
        val generatedBitmap = captureView(binding.drawingBoardView)

        // 2. 비트맵을 임시 파일로 저장
        val handwrittenImageFile = saveBitmapToFile(generatedBitmap, "handwritten_image.png")

        // 3. 손글씨 이미지 파일 전송
        if (handwrittenImageFile.exists()) {
            val intent = Intent(this@PracticeDrawingActivity, GridActivity::class.java)
            intent.putExtra("recognizedText", recognizedText) // OCR 결과
            intent.putExtra("imagePath", handwrittenImageFile.absolutePath) // 이미지 경로
            startActivity(intent)
        } else {
            Log.e("FileError", "손글씨 이미지 파일이 없습니다.")
        }
    }

    // drawingBoardView의 전체를 캡처하는 함수
    fun captureView(view: View): Bitmap {
        // 캡쳐할 높이를 절반으로 설정
        val halfHeight = view.height

        // 절반 높이의 비트맵 생성
        val bitmap = Bitmap.createBitmap(view.width, halfHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // View의 상단 절반만 캡쳐
        canvas.clipRect(0, 0, view.width, halfHeight)
        view.draw(canvas)

        return bitmap
    }

    // 비트맵을 파일로 저장하는 함수
    fun saveBitmapToFile(bitmap: Bitmap, filename: String): File {
        val context = this
        val dir = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "captures")
        if (!dir.exists()) {
            dir.mkdirs() // 디렉토리가 없다면 생성
        }

        val file = File(dir, filename)
        FileOutputStream(file).use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream) // PNG 형식으로 압축하여 저장
        }

        return file // 저장된 파일을 반환
    }


}