package com.chaeni__beam.ai_handwriting_correction_platform

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.chaeni__beam.ai_handwriting_correction_platform.adapter.GridAdapter
import com.chaeni__beam.ai_handwriting_correction_platform.databinding.ActivityGridBinding
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.concurrent.TimeUnit


class GridActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGridBinding
    private var recognizedText: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGridBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val selectedFont = intent.getStringExtra("selectedFont") ?: "돋움체" // 기본값 설정
        Log.d("GridActivity", "Received selectedFont: $selectedFont")


        recognizedText = intent.getStringExtra("recognizedText")
        Log.d("GridActivity", "Received recognizedText: $recognizedText")

        binding.writedText.text = recognizedText

        val imagePath = intent.getStringExtra("imagePath")

        if (imagePath.isNullOrEmpty()) {
            Toast.makeText(this, "이미지 경로를 받지 못했습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        // GridView 초기화
        setupGrid(selectedFont)

        // GridView 로드 완료 후 서버 전송 작업 시작
        binding.gridView.post {
            // 이미지 경로로 작업 시작
            val imageFile = File(imagePath)
            if (imageFile.exists()) {
                // 이미지를 ImageView에 표시
                val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
                binding.capturedImageView.setImageBitmap(bitmap)

                // 서버에 이미지 전송
                prepareAndSendImages(imagePath)
            } else {
                Toast.makeText(this, "이미지 파일이 존재하지 않습니다.", Toast.LENGTH_SHORT).show()
            }
        }
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

        binding.gridView.invalidate()
    }

    private fun captureGrid(): File {
        val gridWidth = binding.gridView.width
        val gridHeight = binding.gridView.height

        if (gridWidth == 0 || gridHeight == 0) {
            throw IOException("GridView 크기가 0입니다. 캡처를 수행할 수 없습니다.")
        }

        val bitmap = Bitmap.createBitmap(gridWidth, gridHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        binding.gridView.draw(canvas)

        val gridImageFile = File(cacheDir, "captured_grid.png")
        try {
            FileOutputStream(gridImageFile).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
            return gridImageFile
        } catch (e: IOException) {
            Log.e("CaptureGrid", "캡처 파일 저장 중 오류 발생", e)
            throw e
        }
    }


    private fun prepareAndSendImages(capturedImagePath: String) {
        if (recognizedText.isNullOrEmpty()) {
            runOnUiThread {
                Toast.makeText(this, "전송할 텍스트가 없습니다.", Toast.LENGTH_SHORT).show()
            }
            return
        }

        // 공백 제거 처리
        val sanitizedText = recognizedText!!.replace("\\s".toRegex(), "") // 모든 공백 제거
        Log.d("SanitizedText", "Sanitized recognizedText: $sanitizedText")

        val gridImageFile = captureGrid()
        sendImagesToServer(gridImageFile, capturedImagePath, sanitizedText)
    }

    private fun sendImagesToServer(gridImageFile: File, capturedImagePath: String, sanitizedText: String) {
        val client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()

        val gridImageRequestBody = gridImageFile.asRequestBody("image/png".toMediaTypeOrNull())
        val capturedImageFile = File(capturedImagePath)
        val capturedImageRequestBody = capturedImageFile.asRequestBody("image/png".toMediaTypeOrNull())
        val textRequestBody = sanitizedText.toRequestBody("text/plain".toMediaTypeOrNull())

        binding.loadingSpinner.visibility = View.VISIBLE
        binding.loadingText.visibility = View.VISIBLE

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("handwrittenImage", capturedImageFile.name, capturedImageRequestBody)
            .addFormDataPart("generatedImage", gridImageFile.name, gridImageRequestBody)
            .addFormDataPart("text", null, textRequestBody)
            .build()

        val request = Request.Builder()
            .url("http://192.168.200.106:8080/api/handwriting/correction")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    binding.loadingSpinner.visibility = View.GONE
                    binding.loadingText.visibility = View.GONE
                    Toast.makeText(this@GridActivity, "서버 전송 실패: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                runOnUiThread {
                    binding.loadingSpinner.visibility = View.GONE
                    binding.loadingText.visibility = View.GONE
                }
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    if (responseBody != null) {
                        Log.d("test", responseBody)
                    }
                    runOnUiThread {
                        binding.resultText.text = responseBody
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this@GridActivity, "서버 오류: ${response.code}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }


    private fun extractFeedback(responseBody: String?): String {
        return try {
            val jsonObject = JSONObject(responseBody ?: "")
            jsonObject.optString("feedback", "결과를 처리할 수 없습니다.")
        } catch (e: Exception) {
            "응답을 처리하는 동안 오류가 발생했습니다."
        }
    }




}
