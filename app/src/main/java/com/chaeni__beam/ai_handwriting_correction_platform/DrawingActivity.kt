package com.chaeni__beam.ai_handwriting_correction_platform

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.chaeni__beam.ai_handwriting_correction_platform.databinding.ActivityDrawingBinding
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class DrawingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDrawingBinding

    private lateinit var handwrittenImageFile: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDrawingBinding.inflate(layoutInflater)

        binding.recordingBtn.setOnClickListener {
            processAndSendComparison()
        }

        binding.resetBtn.setOnClickListener {
            binding.drawingBoardView.clearCanvas() // 그림판 초기화 메서드 호출
            Toast.makeText(this, "그림판이 초기화되었습니다.", Toast.LENGTH_SHORT).show()
        }


        // DrawingBoardView 크기 조정
        adjustDrawingBoardViewSize()

        setContentView(binding.root)
    }

    // DrawingBoardView 크기 조정하는 메서드
    private fun adjustDrawingBoardViewSize() {
        val drawingBoardView = binding.drawingBoardView

        // View가 완전히 준비된 후 크기를 조정하는 방법
        drawingBoardView.post {
            val screenWidth = drawingBoardView.width
            val screenHeight = (screenWidth * 15) / 7

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
            sendImageToServer(handwrittenImageFile)
        } else {
            Log.e("FileError", "손글씨 이미지 파일이 없습니다.")
        }
    }

    // drawingBoardView의 전체를 캡처하는 함수
    fun captureView(view: View): Bitmap {
        // 캡쳐할 높이를 절반으로 설정
        val halfHeight = view.height / 3

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

    // '/api/ocr' 엔드포인트
    private fun sendImageToServer(imageFile: File) {
        val client = OkHttpClient()
        handwrittenImageFile = imageFile // 전송된 파일을 전역 변수에 저장


        // 이미지 파일을 서버로 전송할 MultipartBody 구성
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                "file", imageFile.name,
                RequestBody.create("image/*".toMediaTypeOrNull(), imageFile)
            ) // 이미지 파일 전송
            .build()

        // 서버에 POST 요청
        val request = Request.Builder()
            .url("http://192.168.200.106:8080/api/handwriting/ocr")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // 네트워크 오류 처리
                e.printStackTrace()
                runOnUiThread {
                    // UI 업데이트: 오류 메시지 표시
                    Log.d("test", "Network error: ${e.message}")
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    // OCR 처리 결과를 처리하는 코드
                    val responseBody = response.body?.string()

                    runOnUiThread {
                        if (responseBody != null) {
                            Log.d("test", responseBody)
                            // OCR 결과가 텍스트라면, TextView에 표시

                            // recognizedText와 이미지 경로를 Intent에 담아 전달
                            val intent = Intent(this@DrawingActivity, GridActivity::class.java)
                            intent.putExtra("recognizedText", responseBody) // OCR 결과
                            intent.putExtra("imagePath", imageFile.absolutePath) // 이미지 경로
                            startActivity(intent)
                        } else {
                            // 서버에서 받은 내용이 없을 경우 오류 처리
                            Log.d("test", "Empty response body")
                        }
                    }
                } else {
                    // 서버 오류 처리
                    runOnUiThread {
                        // UI 업데이트: 오류 메시지 표시
                        Log.d("test", "OCR 처리 오류: ${response.code}")
                    }
                }
            }
        })
    }




}
